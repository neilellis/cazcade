package cazcade.boardcast.servlet.login;

import cazcade.boardcast.servlet.AbstractBoardcastServlet;
import cazcade.common.Logger;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author neilellis@cazcade.com
 */
public class LogoutServlet extends AbstractBoardcastServlet {
    @Nonnull
    private static final Logger log = Logger.getLogger(LogoutServlet.class);


    @Override
    protected void service(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp)
            throws ServletException, IOException {
        logOut(req.getSession(true));
        resp.sendRedirect(req.getParameter("next"));
    }
}
