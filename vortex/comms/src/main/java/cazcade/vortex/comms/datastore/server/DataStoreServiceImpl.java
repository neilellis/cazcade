package cazcade.vortex.comms.datastore.server;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.messaging.session.ClientSession;
import cazcade.fountain.messaging.session.ClientSessionManager;
import cazcade.fountain.security.SecurityProvider;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.RetrievePoolRequest;
import cazcade.liquid.api.request.RetrieveUserRequest;
import cazcade.liquid.impl.xstream.LiquidXStreamFactory;
import cazcade.vortex.comms.datastore.client.DataStoreService;
import cazcade.vortex.comms.datastore.client.LoggedOutException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.UnexpectedException;
import com.newrelic.api.agent.NewRelic;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.springframework.amqp.AmqpIOException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.UUID;

//todo: backport the notification parts to the notification servlet.

public class DataStoreServiceImpl extends RemoteServiceServlet implements DataStoreService {
    private final static Logger log = Logger.getLogger(DataStoreServiceImpl.class);

    //TODO: make this retrieved from the web.xml so that client applications can change the web.xml at build time.
    public static final String APPLICATION_VERSION = UUID.randomUUID().toString();
    public static final boolean USE_CONTINUATIONS = false;
    public static final String DEDUPCACHE = "dedupcache";
    public static final boolean ALLOW_DUPLICATES = true;

    private WebApplicationContext applicationContext;
    private FountainDataStore dataStore;
    private SecurityProvider securityProvider;
    private static final String PAYLOAD = "com.google.gwt.payload";

    private static final String JETTY_RETRY_REQUEST_EXCEPTION = "org.mortbay.jetty.RetryRequest";
    public static final String NOTIFICATION_SESSION_ATTRIBUTE = "notificationSession";
    public static final ArrayList<LiquidMessage> EMPTY_MESSAGE_LIST = new ArrayList<LiquidMessage>();
    private boolean supportsContinuations;
    private RabbitTemplate rabbitTemplate;
    private RabbitAdmin rabbitAdmin;
    private TopicExchange exchange;
    private ClientSessionManager clientSessionManager;
    private Cache dedupCache;


    /**
     * Overridden to really throw Jetty RetryRequest Exception (as opposed to sending failure to client).
     *
     * @param e the exception
     */
    protected void doUnexpectedFailure(Throwable e) {
        throwIfRetryRequest(e);
        e.printStackTrace(System.err);
        log.error(e);
        super.doUnexpectedFailure(e);
    }

    /**
     * Throws the Jetty RetryRequest if found.
     *
     * @param caught the exception
     */
    protected void throwIfRetryRequest(Throwable caught) {
        if (caught instanceof UnexpectedException) {
            caught = caught.getCause();
        }

        if (JETTY_RETRY_REQUEST_EXCEPTION.equals(caught.getClass().getName())) {
            throw (RuntimeException) caught;
        }
    }


    @Override
    protected String readContent(HttpServletRequest request)
            throws IOException, ServletException {
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
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            Class.forName("org.eclipse.jetty.continuation.Jetty6Continuation");
            supportsContinuations = true;
        } catch (ClassNotFoundException e) {

        }


        PropertyConfigurator.configure("WEB-INF/classes/log4j.properties");
        try {
            applicationContext = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
            dataStore = (FountainDataStore) applicationContext.getBean("remoteDataStore");
            rabbitTemplate = (RabbitTemplate) applicationContext.getBean("rabbitTemplate");
            rabbitAdmin = (RabbitAdmin) applicationContext.getBean("rabbitAdmin");
            exchange = (TopicExchange) applicationContext.getBean("mainExchange");
            clientSessionManager = (ClientSessionManager) applicationContext.getBean("clientSessionManager");
            securityProvider = new SecurityProvider(dataStore);
            if (!CacheManager.getInstance().cacheExists(DEDUPCACHE)) {
                CacheManager.getInstance().addCache(DEDUPCACHE);
            }
            dedupCache = CacheManager.getInstance().getCache(DEDUPCACHE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getHeader(X_VORTEX_SINCE) != null && req.getHeader(X_VORTEX_SINCE).equals("-1")) {
            try {
                resp.sendError(304);
            } catch (IOException e) {
                log.error(e);
                return;
            }
        }
        super.service(req, resp);
        log.debug("Returning from service method.");
    }

    public void logout(LiquidSessionIdentifier identity) {
        clientSessionManager.expireSession(identity.getSession().toString());
    }

    @Override
    public String getApplicationIdentifier() {
        return APPLICATION_VERSION;
    }

    public LiquidSessionIdentifier login(String username, String password) {
        try {
            Principal principal = securityProvider.doAuthentication(username, password);
            if (principal == null) {
                return null;
            }
            getThreadLocalRequest().getSession(true).setAttribute("username", username);
            return LoginUtil.login(clientSessionManager, dataStore, new LiquidURI(LiquidURIScheme.alias, "cazcade:" + username));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public LiquidSessionIdentifier loginQuick(boolean anon) {
        final String sessionUsername = (String) getThreadLocalRequest().getSession(true).getAttribute("username");
        if(sessionUsername != null) {
            try {
                return LoginUtil.login(clientSessionManager, dataStore, new LiquidURI(LiquidURIScheme.alias, "cazcade:" + sessionUsername));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }
        if(anon) {
            try {
                return LoginUtil.login(clientSessionManager, dataStore, new LiquidURI("alias:cazcade:anon"));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
        } else {
            return null;
        }

    }


    @Override
    public LSDEntity register(String fullname, String username, String password, String emailAddress) {
        final HttpSession session = getThreadLocalRequest().getSession(true);
        return LoginUtil.register(session, dataStore, fullname, username, password, emailAddress, true);

    }

    @Override
    public boolean checkUsernameAvailability(String username) {
        try {
            LiquidMessage message;
            message = dataStore.process(new RetrieveUserRequest(new LiquidSessionIdentifier("admin"), new LiquidURI(LiquidURIScheme.user, username), true));
            //TODO: clean all this up, it's a hack looking for authorization denials for non-existent resources
            return message.getResponse().isA(LSDDictionaryTypes.EMPTY_RESULT) || message.getResponse().isA(LSDDictionaryTypes.AUTHORIZATION_DENIAL) || message.getResponse().isA(LSDDictionaryTypes.RESOURCE_NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }

    }

    public LiquidMessage process(LiquidRequest request) {
        log.debug("{0}", LiquidXStreamFactory.getXstream().toXML(request));
        final LiquidSessionIdentifier serverSession = request.getSessionIdentifier();
        if (serverSession == null) {
            throw new LoggedOutException();
        }
        ClientSession clientSession = LoginUtil.createClientSession(clientSessionManager, serverSession, false);
        if (clientSession == null) {
            if (request.isSecureOperation()) {
                throw new LoggedOutException();
            } else {
                clientSession = LoginUtil.createClientSession(clientSessionManager, serverSession, true);
            }
        }
        try {
            request.adjustTimeStampForServerTime();
//            request.setIdentity(currentUser());
            request.setOrigin(LiquidMessageOrigin.CLIENT);
            LiquidMessage response = dataStore.process(request);
            log.debug(LiquidXStreamFactory.getXstream().toXML(response));
            getThreadLocalResponse().addHeader(X_VORTEX_CACHE_SCOPE, request.getCachingScope().name());
            getThreadLocalResponse().addHeader(X_VORTEX_CACHE_EXPIRY, String.valueOf(request.getCacheExpiry()));
            return response;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
    }

    @Override
    public boolean checkBoardAvailability(LiquidURI board) {
        try {
            LiquidMessage message;
            message = dataStore.process(new RetrievePoolRequest(new LiquidSessionIdentifier("admin"), board, false, false));
            //TODO: clean all this up, it's a hack looking for authorization denials for non-existent resources
            return message.getResponse().isA(LSDDictionaryTypes.EMPTY_RESULT) || message.getResponse().isA(LSDDictionaryTypes.AUTHORIZATION_DENIAL) || message.getResponse().isA(LSDDictionaryTypes.RESOURCE_NOT_FOUND);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    public ArrayList<LiquidMessage> collect(LiquidSessionIdentifier serverSession, ArrayList<String> locations) throws Exception {
        getThreadLocalRequest().setAttribute("com.newrelic.agent.IGNORE", true);
        NewRelic.ignoreTransaction();


//        checkLoggedIn(identity);
        log.debug("Collecting " + locations);
//        final LiquidSessionIdentifier identity = currentUser();
        final Continuation continuation = ContinuationSupport.getContinuation(getThreadLocalRequest());
        if (serverSession == null) {
            throw new LoggedOutException();
        }
        try {
            final ClientSession clientSession = LoginUtil.createClientSession(clientSessionManager, serverSession, true);
            final String sessionId = serverSession.getSession().toString();
            clientSession.setContinuation(continuation);

            final Queue sessionQueue = (Queue) clientSession.getSpringContext().getBean("sessionQueue");

            if (continuation.isInitial()) {
                if (clientSession.getPreviousLocations() != null) {
                    for (String previousLocation : clientSession.getPreviousLocations()) {
                        if (!locations.contains(previousLocation)) {
                            try {
                                rabbitAdmin.removeBinding(new Binding(sessionQueue, exchange, "location." + previousLocation));
                            } catch (AmqpIOException amqpIOException) {
                                log.warn("Exception while removing binding of queue->exchange {0}->{1}", sessionQueue.getName(), exchange.getName());
                            }
                        }
                    }
                }
                for (String newLocation : locations) {
                    if (!clientSession.getPreviousLocations().contains(newLocation)) {
                        rabbitAdmin.declareBinding(new Binding(sessionQueue, exchange, "location." + newLocation));
                    }
                }

                clientSession.setPreviousLocations(locations);
            }

            int count = 0;
            while (count++ < 100) {
                final ArrayList<LiquidMessage> resultMessages = new ArrayList<LiquidMessage>();
                for (LiquidMessage resultMessage : clientSession.removeMessages()) {
                    final String cacheKey = sessionId + ":" + resultMessage.getDeduplicationIdentifier();
                    if (ALLOW_DUPLICATES || dedupCache.get(cacheKey) == null) {
                        resultMessages.add(resultMessage);
                        dedupCache.putQuiet(new Element(cacheKey, ""));
                    } else {
                        log.debug("Deduplicated {0}", cacheKey);
                    }
                }
                if (resultMessages.size() == 0) {
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
        return new ArrayList<LiquidMessage>();

    }


    @Override
    public void destroy() {
        super.destroy();
        dataStore.stopIfNotStopped();
    }

    public void setRabbitAdmin(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }
}