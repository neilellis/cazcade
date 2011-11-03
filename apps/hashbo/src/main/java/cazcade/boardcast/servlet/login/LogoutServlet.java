package cazcade.boardcast.servlet.login;

import cazcade.boardcast.servlet.AbstractHashboServlet;
import cazcade.common.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author neilellis@cazcade.com
 */
public class LogoutServlet extends AbstractHashboServlet {
    private final static Logger log = Logger.getLogger(LogoutServlet.class);


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logOut(req.getSession(true));
        resp.sendRedirect(req.getParameter("next"));
    }
}