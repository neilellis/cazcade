package cazcade.hashbo.servlet;

import cazcade.common.Logger;
import cazcade.hashbo.servlet.board.AbstractBoardListServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author neilellis@cazcade.com
 */
public class ProfileServlet extends AbstractBoardListServlet {

    private final static Logger log = Logger.getLogger(ProfileServlet.class);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.getRequestDispatcher("_pages/profile.jsp").forward(req, resp);
        } catch (Exception e) {
            log.error(e);
        }
    }


}
