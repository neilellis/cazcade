package cazcade.hashbo.servlet.board;

import cazcade.common.Logger;
import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.BoardQueryRequest;
import cazcade.liquid.api.request.RetrievePoolRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class BoardServlet extends AbstractBoardListServlet {

    private final static Logger log = Logger.getLogger(BoardServlet.class);


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            final String[] parts = req.getServletPath().substring(1).split("\\.");

            req.setAttribute("board", parts[1].equals("profile") ? "@"+parts[0] : parts[0]);
            req.getRequestDispatcher("_pages/board.jsp").forward(req, resp);
        } catch (Exception e) {
            log.error(e);
        }
    }


}
