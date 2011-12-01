package cazcade.boardcast.servlet.login;

import cazcade.boardcast.servlet.AbstractHashboServlet;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidURI;
import cazcade.vortex.comms.datastore.server.LoginUtil;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

/**
 * @author neilellis@cazcade.com
 */
public class LoginServlet extends AbstractHashboServlet {
    @Nonnull
    private static final Logger log = Logger.getLogger(LoginServlet.class);


    @Override
    protected void doGet(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp) throws ServletException, IOException {
        if (loggedIn(req.getSession(true))) {
            forwardAfterLogin(req, resp);
        } else {
            req.getRequestDispatcher("/_pages/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp) throws ServletException, IOException {
        final String username = req.getParameter("username");
        final String password = req.getParameter("password");
        try {
            final HttpSession session = req.getSession(true);
            if (loggedIn(session) && loggedInAs(username, session)) {
                forwardAfterLogin(req, resp);
            } else {
                if (username == null) {
                    req.getRequestDispatcher("/_pages/login.jsp").forward(req, resp);
                }
                final Principal principal = securityProvider.doAuthentication(username, password);
                if (principal != null) {
                    LoginUtil.login(clientSessionManager, dataStore, new LiquidURI("alias:cazcade:" + username), session);
                    forwardAfterLogin(req, resp);
                } else {
                    req.setAttribute("message", "Could not log you in.");
                    req.getRequestDispatcher("/_pages/login.jsp").forward(req, resp);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }


}
