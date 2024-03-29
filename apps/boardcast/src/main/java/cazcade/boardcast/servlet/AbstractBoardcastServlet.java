/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.servlet;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.messaging.FountainPubSub;
import cazcade.fountain.messaging.session.ClientSessionManager;
import cazcade.fountain.security.SecurityProvider;
import cazcade.liquid.api.ClientApplicationIdentifier;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.CreateSessionRequest;
import cazcade.vortex.comms.datastore.server.LoginUtil;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Nonnull;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractBoardcastServlet extends HttpServlet {
    @Nonnull
    private static final Logger log                                = Logger.getLogger(AbstractBoardcastServlet.class);
    @Nonnull
    public static final  String SESSION_KEY                        = "sessionId";
    public static final  String VERSION                            = "25";
    public static final  long   FORCE_IMAGE_REFRESH_TIME_IN_MILLIS = (1000 * 36000 * 24 * 7);

    private   WebApplicationContext applicationContext;
    protected FountainDataStore     dataStore;
    protected ClientSessionManager  clientSessionManager;
    protected SecurityProvider      securityProvider;
    @Nonnull
    protected static final String USERNAME_KEY = "username";
    protected FountainPubSub pubSub;

    @Override
    public void init(@Nonnull final ServletConfig config) throws ServletException {
        super.init(config);
        //we use the same context as the main communications servlet
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
        dataStore = (FountainDataStore) applicationContext.getBean("syncRemoteDataStore");
        clientSessionManager = (ClientSessionManager) applicationContext.getBean("clientSessionManager");
        pubSub = (FountainPubSub) applicationContext.getBean("pubSub");
        securityProvider = new SecurityProvider(dataStore);

        try {
            dataStore.startIfNotStarted();
        } catch (Exception e) {
            log.error(e);
        }

    }

    @Nonnull
    protected SessionIdentifier getLiquidSessionId(@Nonnull final HttpSession session) {
        SessionIdentifier sessionIdentifier = (SessionIdentifier) session.getAttribute(SESSION_KEY);
        if (sessionIdentifier == null) {
            return SessionIdentifier.ANON;
        }
        return sessionIdentifier;
    }

    @Nonnull
    protected List<Map<String, String>> makeJSPFriendly(HttpServletRequest req, @Nonnull final List<TransferEntity> entities) throws UnsupportedEncodingException {
        final List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        for (final TransferEntity entity : entities) {
            final Map<String, String> map = entity.getCamelCaseMap();
            result.add(map);
            final LURI uri = entity.uri();
            final String uriString = uri.toString();
            if (uriString.startsWith("pool")) {
                final String shortUrl = uri.board().safe();
                map.put("shortUrl", shortUrl);

                final String url = "http://"
                                   + ("127.0.0.1".equals(req.getServerName()) ? "boardcast.it" : req.getServerName())
                                   + "/_snapshot-"
                                   + shortUrl
                                   + "?bid="
                                   + entity.default$(Dictionary.MODIFIED, "")
                                   +
                                   "-v"
                                   + VERSION
                                   + (System.currentTimeMillis() / FORCE_IMAGE_REFRESH_TIME_IN_MILLIS);
                map.put("snapshotUrl", url);

            }
        }
        return result;
    }


    protected boolean loggedIn(@Nonnull final HttpSession session) {
        return session.getAttribute(USERNAME_KEY) != null;
    }


    protected void logOut(@Nonnull final HttpSession session) {
        session.removeAttribute(USERNAME_KEY);
    }

    @Nonnull
    protected SessionIdentifier createClientSession(@Nonnull final HttpSession session, @Nonnull final LiquidMessage createSessionResponse) {

        final TransferEntity createSessionResponseResponse = createSessionResponse.response();
        final String username = createSessionResponseResponse.$(Dictionary.NAME);
        final SessionIdentifier serverSession = new SessionIdentifier(username, createSessionResponseResponse.id());
        session.setAttribute(SESSION_KEY, serverSession);
        session.setAttribute(USERNAME_KEY, username);
        LoginUtil.createClientSession(clientSessionManager, serverSession, true, pubSub);
        return serverSession;
    }

    @Nonnull
    protected LiquidMessage createSession(final LURI uri) throws Exception {
        return dataStore.process(new CreateSessionRequest(uri, new ClientApplicationIdentifier("GWT Client", LoginUtil.APP_KEY, "UNKNOWN")));
    }

    protected void forwardAfterLogin(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp) throws ServletException, IOException {
        final String next = req.getParameter("next");
        if (next != null) {
            resp.sendRedirect(next);
        } else {
            resp.sendRedirect("/welcome");
        }
    }


    protected boolean loggedInAs(final String username, @Nonnull final HttpSession session) {
        return session.getAttribute(USERNAME_KEY).equals(username);
    }


    @Override
    public void destroy() {
        dataStore.stopIfNotStopped();
        super.destroy();
    }
}
