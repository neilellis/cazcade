package cazcade.boardcast.servlet;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.messaging.session.ClientSessionManager;
import cazcade.fountain.security.SecurityProvider;
import cazcade.liquid.api.ClientApplicationIdentifier;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractHashboServlet extends HttpServlet {
    @Nonnull
    private final static Logger log = Logger.getLogger(AbstractHashboServlet.class);
    @Nonnull
    public static final String SESSION_KEY = "sessionId";

    private WebApplicationContext applicationContext;
    protected FountainDataStore dataStore;
    protected ClientSessionManager clientSessionManager;
    protected SecurityProvider securityProvider;
    @Nonnull
    protected static final String USERNAME_KEY = "username";

    @Override
    public void init(@Nonnull ServletConfig config) throws ServletException {
        super.init(config);
        //we use the same context as the main communications servlet
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
        dataStore = (FountainDataStore) applicationContext.getBean("syncRemoteDataStore");
        clientSessionManager = (ClientSessionManager) applicationContext.getBean("clientSessionManager");
        securityProvider = new SecurityProvider(dataStore);

        try {
            dataStore.startIfNotStarted();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @Nonnull
    protected LiquidSessionIdentifier getLiquidSessionId(@Nonnull HttpSession session) {
        return (LiquidSessionIdentifier) session.getAttribute(SESSION_KEY);
    }

    @Nonnull
    protected List<Map<String, String>> makeJSPFriendly(@Nonnull List<LSDEntity> entities) throws UnsupportedEncodingException {
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        for (LSDEntity entity : entities) {
            Map<String, String> map = entity.getCamelCaseMap();
            result.add(map);
            if (entity.getURI().toString().startsWith("pool")) {
                final String shortUrl = entity.getURI().asShortUrl().asUrlSafe();
                map.put("shortUrl", shortUrl);
                if (!entity.hasAttribute(LSDAttribute.ICON_URL)) {
                    map.put("iconUrl", "http://boardcast.it/_board-icon?ModPagespeed=off&board=" + URLEncoder.encode(shortUrl, "utf-8") + "&bid=" + entity.getAttribute(LSDAttribute.ID));
                }
            }
        }
        return result;
    }


    protected boolean loggedIn(@Nonnull HttpSession session) {
        return session.getAttribute(USERNAME_KEY) != null;
    }


    protected void logOut(@Nonnull HttpSession session) {
        session.removeAttribute(USERNAME_KEY);
    }

    @Nonnull
    protected LiquidSessionIdentifier createClientSession(@Nonnull HttpSession session, @Nonnull LiquidMessage createSessionResponse) {

        final String username = createSessionResponse.getResponse().getAttribute(LSDAttribute.NAME);
        LiquidSessionIdentifier serverSession = new LiquidSessionIdentifier(username, createSessionResponse.getResponse().getUUID());
        session.setAttribute(SESSION_KEY, serverSession);
        session.setAttribute(USERNAME_KEY, username);
        LoginUtil.createClientSession(clientSessionManager, serverSession, true);
        return serverSession;
    }

    @Nonnull
    protected LiquidMessage createSession(LiquidURI uri) throws Exception {
        return dataStore.process(new CreateSessionRequest(uri, new ClientApplicationIdentifier("GWT Client", LoginUtil.APP_KEY, "UNKNOWN")));
    }

    protected void forwardAfterLogin(@Nonnull HttpServletRequest req, @Nonnull HttpServletResponse resp) throws ServletException, IOException {
        final String next = req.getParameter("next");
        if (next != null) {
            resp.sendRedirect(next);
        } else {
            resp.sendRedirect("/_query-popular");
        }
    }


    protected boolean loggedInAs(String username, @Nonnull HttpSession session) {
        return session.getAttribute(USERNAME_KEY).equals(username);
    }


    @Override
    public void destroy() {
        dataStore.stopIfNotStopped();
        super.destroy();
    }
}
