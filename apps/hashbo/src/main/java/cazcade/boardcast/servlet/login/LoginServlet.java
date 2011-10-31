package cazcade.boardcast.servlet.login;

import cazcade.boardcast.servlet.AbstractHashboServlet;
import cazcade.common.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

/**
 * @author neilellis@cazcade.com
 */
public class LoginServlet extends AbstractHashboServlet {
    private final static Logger log = Logger.getLogger(LoginServlet.class);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (loggedIn(req.getSession(true))) {
            forwardAfterLogin(req, resp);
        } else {
            req.getRequestDispatcher("/_pages/login.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (loggedIn(req.getSession(true))) {
                forwardAfterLogin(req, resp);
            } else {
                final String username = req.getParameter("username");
                final String password = req.getParameter("password");
                if (username == null) {
                    req.getRequestDispatcher("/_pages/login.jsp").forward(req, resp);
                }
                final Principal principal = securityProvider.doAuthentication(username, password);
                if (principal != null) {
                    req.getSession(true).setAttribute(USERNAME_KEY, username);
                    forwardAfterLogin(req, resp);
                } else {
                    req.setAttribute("error", "Could not log you in.");
                    req.getRequestDispatcher("/_pages/login.jsp").forward(req, resp);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServletException(e);
        }
    }

}
