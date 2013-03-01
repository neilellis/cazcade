/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.comms.datastore.server;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.messaging.FountainPubSub;
import cazcade.fountain.messaging.session.ClientSession;
import cazcade.fountain.messaging.session.ClientSessionManager;
import cazcade.fountain.security.SecurityProvider;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.AbstractRequest;
import cazcade.liquid.api.request.RetrievePoolRequest;
import cazcade.liquid.api.request.RetrieveUserRequest;
import cazcade.liquid.api.request.SerializedRequest;
import cazcade.vortex.comms.datastore.client.DataStoreService;
import cazcade.vortex.comms.datastore.client.LoggedOutException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.UnexpectedException;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//import com.newrelic.api.agent.NewRelic;

//todo: backport the notification parts to the notification servlet.

public final class DataStoreServiceImpl extends RemoteServiceServlet implements DataStoreService {
    public static final  boolean USE_CONTINUATIONS              = false;
    @Nonnull
    public static final  String  DEDUPCACHE                     = "dedupcache";
    public static final  boolean ALLOW_DUPLICATES               = true;
    @Nonnull
    public static final  String  NOTIFICATION_SESSION_ATTRIBUTE = "notificationSession";
    @Nonnull
    private static final Logger  log                            = Logger.getLogger(DataStoreServiceImpl.class);
    private static final long    serialVersionUID               = 860416951877283521L;
    @Nonnull
    private static final String  PAYLOAD                        = "com.google.gwt.payload";
    @Nonnull
    private static final String  JETTY_RETRY_REQUEST_EXCEPTION  = "org.mortbay.jetty.RetryRequest";
    private WebApplicationContext          applicationContext;
    private FountainDataStore              dataStore;
    private SecurityProvider               securityProvider;
    private boolean                        supportsContinuations;
    private ClientSessionManager           clientSessionManager;
    private Cache                          dedupCache;
    private FountainPubSub                 pubSub;
    private ClassPathXmlApplicationContext serverContext;

    /**
     * Throws the Jetty RetryRequest if found.
     *
     * @param caught the exception
     */
    static void throwIfRetryRequest(Throwable caught) {
        if (caught instanceof UnexpectedException) {
            caught = caught.getCause();
        }

        if (JETTY_RETRY_REQUEST_EXCEPTION.equals(caught.getClass().getName())) {
            throw (RuntimeException) caught;
        }
    }

    /**
     * Overridden to really throw Jetty RetryRequest Exception (as opposed to sending failure to client).
     *
     * @param e the exception
     */
    @Override
    protected void doUnexpectedFailure(@Nonnull final Throwable e) {
        throwIfRetryRequest(e);
        e.printStackTrace(System.err);
        log.error(e);
        super.doUnexpectedFailure(e);
    }

    @Override
    protected String readContent(@Nonnull final HttpServletRequest request) throws IOException, ServletException {
        if (supportsContinuations) {
            String payload = (String) request.getAttribute(PAYLOAD);
            if (payload == null) {
                payload = super.readContent(request);
                request.setAttribute(PAYLOAD, payload);
            }
            return payload;
        } else {
            return super.readContent(request);
        }


    }

    @Override
    protected void service(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp) throws ServletException, IOException {
        if (req.getHeader(X_VORTEX_SINCE) != null && "-1".equals(req.getHeader(X_VORTEX_SINCE))) {
            try {
                resp.sendError(304);
            } catch (IOException e) {
                log.error(e);
                return;
            }
        }
        try {
            super.service(req, resp);
        } catch (RuntimeException re) {
            log.warn(re, re.getMessage());
            throw re;
        } catch (EOFException eof) {
            log.debug("EOF");
        }
        log.debug("Returning from service method.");
    }

    public void logout(@Nonnull final SessionIdentifier identity) {
        clientSessionManager.expireSession(identity.session().toString());
    }

    @Nullable
    public SessionIdentifier login(@Nonnull final String username, final String password) {
        try {
            final Principal principal = securityProvider.doAuthentication(username, password);
            if (principal == null) {
                return null;
            }
            return LoginUtil.login(clientSessionManager, dataStore, new LURI(LiquidURIScheme.alias, "cazcade:"
                                                                                                         + username), getOrCreateSession(), pubSub);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    @Nullable @Override
    public SessionIdentifier loginQuick(final boolean anon) {
        final String sessionUsername = (String) getOrCreateSession().getAttribute("username");
        if (sessionUsername != null) {
            try {
                return LoginUtil.login(clientSessionManager, dataStore, new LURI(LiquidURIScheme.alias, "cazcade:"
                                                                                                             + sessionUsername), getOrCreateSession(), pubSub);
            } catch (Exception e) {
                log.error(e);
                return null;
            }
        }
        if (anon) {
            try {
                return LoginUtil.login(clientSessionManager, dataStore, new LURI(CommonConstants.ANONYMOUS_ALIAS), getOrCreateSession(), pubSub);
            } catch (Exception e) {
                log.error(e);
                return null;
            }
        } else {
            return null;
        }

    }

    @Nullable @Override
    public TransferEntity register(final String fullname, @Nonnull final String username, final String password, final String emailAddress) {
        final HttpSession session = getOrCreateSession();
        final TransferEntity entity = LoginUtil.register(session, dataStore, fullname, username, password, emailAddress, true);
        try {
            if (entity != null && entity.is(Types.T_USER)) {
                LoginUtil.login(clientSessionManager, dataStore, new LURI("alias:cazcade:" + username), session, pubSub);
            }
        } catch (Exception e) {
            log.error(e);
            return null;
        }
        return entity;

    }

    @Override
    public boolean checkUsernameAvailability(@Nonnull final String username) {
        try {
            final LiquidMessage message;
            message = dataStore.process(new RetrieveUserRequest(new SessionIdentifier("admin"), new LURI(LiquidURIScheme.user, username), true));
            //TODO: clean all this up, it's a hack looking for authorization denials for non-existent resources
            final Entity responseEntity = message.response();
            return responseEntity.is(Types.T_EMPTY_RESULT)
                   || responseEntity.is(Types.T_AUTHORIZATION_DENIAL)
                   || responseEntity.is(Types.T_RESOURCE_NOT_FOUND);
        } catch (Exception e) {
            log.error(e);
            return false;
        }

    }

    @Override @Nullable
    public ArrayList<SerializedRequest> collect(@Nullable final SessionIdentifier identity, @Nonnull final ArrayList<String> locations) throws Exception {
        getThreadLocalRequest().setAttribute("com.newrelic.agent.IGNORE", true);
        //        NewRelic.ignoreTransaction();
        log.debug("Collecting {0}", locations);
        final Continuation continuation = ContinuationSupport.getContinuation(getThreadLocalRequest());
        if (identity == null) {
            throw new LoggedOutException();
        }
        final ArrayList<String> queues = new ArrayList<String>();
        for (final String location : locations) {
            queues.add("location." + location);
        }
        queues.add("session." + identity.session());
        queues.add("user." + identity.userURL());
        queues.add("alias." + identity.aliasURI());
        try {
            final ClientSession clientSession = LoginUtil.createClientSession(clientSessionManager, identity, true, pubSub);
            final String sessionId = identity.session().toString();
            final FountainPubSub.Collector collector = clientSession.getCollector();
            collector.bind(queues);

            if (continuation.isInitial()) {
                Set<String> collectorKeys = new HashSet<String>(collector.getKeys());
                for (final String key : collectorKeys) {
                    if (key.startsWith("location.") && !queues.contains(key)) {
                        collector.unbind(key);
                    }
                }
            }

            int count = 0;
            while (count++ < 100) {
                final ArrayList<SerializedRequest> resultMessages = new ArrayList<SerializedRequest>();
                for (final LiquidMessage resultMessage : collector.readMany()) {
                    final String cacheKey = sessionId + ':' + resultMessage.deduplicationIdentifier();
                    //noinspection ConstantConditions
                    if (ALLOW_DUPLICATES || dedupCache.get(cacheKey) == null) {
                        resultMessages.add(resultMessage.asSerializedRequest());
                        dedupCache.putQuiet(new Element(cacheKey, ""));
                    } else {
                        log.debug("Deduplicated {0}", cacheKey);
                    }
                }
                if (resultMessages.isEmpty()) {
                    if (USE_CONTINUATIONS) {
                        continuation.suspend();
                        return null;
                    } else {
                        Thread.sleep(500);
                    }
                } else {
                    log.debug("Returning {0} messages.", resultMessages.size());
                    return resultMessages;
                }
            }
        } catch (LoggedOutException loe) {
            throw loe;
        } catch (Exception e) {
            log.error(e);

        }
        return new ArrayList<SerializedRequest>();

    }

    @Override @Nullable
    public SerializedRequest process(@Nonnull final SerializedRequest ser) throws Exception {

        log.info("Processing {0}", ser);
        final AbstractRequest request;
        try {
            request = (AbstractRequest) ser.getType().getRequestClass().getConstructor().newInstance();
            request.setEntity(ser.getEntity());
        } catch (InstantiationException e) {
            log.error(e);
            return null;
        } catch (IllegalAccessException e) {
            log.error(e);
            return null;
        } catch (NoSuchMethodException e) {
            log.error(e);
            return null;
        } catch (InvocationTargetException e) {
            log.error(e);
            return null;
        } catch (Exception e) {
            log.error(e);
            return null;
            //            throw new Exception(e.getMessage());
        }
        try {

            final SessionIdentifier serverSession = request.session();
            if (serverSession.session() == null) {
                throw new LoggedOutException();
            }
            ClientSession clientSession = LoginUtil.createClientSession(clientSessionManager, serverSession, false, pubSub);
            if (clientSession == null) {
                if (request.isSecureOperation()) {
                    throw new LoggedOutException();
                } else {
                    clientSession = LoginUtil.createClientSession(clientSessionManager, serverSession, true, pubSub);
                    //This basically synchronizes our two ways of being logged in, logged in on the client and logged
                    //in here on the web server.
                    if (!serverSession.anon()) {
                        LoginUtil.placeServerSessionInHttpSession(dataStore, getOrCreateSession(), serverSession);
                    }
                }
            }

            request.adjustTimeStampForServerTime();
            //            request.setIdentity(currentUser());
            request.origin(Origin.CLIENT);
            final LiquidMessage response = dataStore.process(request);
            log.debug("{0}", response.toString());
            getThreadLocalResponse().addHeader(X_VORTEX_CACHE_SCOPE, request.cachingScope().name());
            getThreadLocalResponse().addHeader(X_VORTEX_CACHE_EXPIRY, String.valueOf(request.cacheExpiry()));
            return response.asSerializedRequest();
        } catch (LoggedOutException loe) {
            throw loe;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    @Override
    public boolean checkBoardAvailability(final LURI board) {
        try {
            final LiquidMessage message;
            message = dataStore.process(new RetrievePoolRequest(new SessionIdentifier("admin"), board, false, false));
            //TODO: clean all this up, it's a hack looking for authorization denials for non-existent resources
            final Entity responseEntity = message.response();
            return (responseEntity.is(Types.T_EMPTY_RESULT)
                    || responseEntity.is(Types.T_AUTHORIZATION_DENIAL)
                    || responseEntity.is(Types.T_RESOURCE_NOT_FOUND));
        } catch (Exception e) {
            log.error(e);
            return false;
        }
    }

    private HttpSession getOrCreateSession() {
        return getThreadLocalRequest().getSession(true);
    }

    @Override
    public void destroy() {
        super.destroy();
        dataStore.stopIfNotStopped();
        serverContext.stop();
    }

    @Override
    public void init(@Nonnull final ServletConfig config) throws ServletException {
        super.init(config);
        //noinspection EmptyCatchBlock
        try {
            Class.forName("org.eclipse.jetty.continuation.Jetty6Continuation");
            supportsContinuations = true;
        } catch (ClassNotFoundException e) {

        }

        PropertyConfigurator.configure(getClass().getResource("/log4j.properties"));
        try {
            applicationContext = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());

            dataStore = (FountainDataStore) applicationContext.getBean("remoteDataStore");
            pubSub = (FountainPubSub) applicationContext.getBean("pubSub");
            clientSessionManager = (ClientSessionManager) applicationContext.getBean("clientSessionManager");
            securityProvider = new SecurityProvider(dataStore);
            if (!CacheManager.getInstance().cacheExists(DEDUPCACHE)) {
                CacheManager.getInstance().addCache(DEDUPCACHE);
            }
            dedupCache = CacheManager.getInstance().getCache(DEDUPCACHE);
        } catch (Exception e) {
            log.error(e);
            throw new ServletException(e);
        }
    }

}