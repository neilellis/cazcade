package cazcade.boardcast.servlet.board;

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidBoardURL;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.request.RetrievePoolRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author neilellis@cazcade.com
 */
public class BoardIconServlet extends AbstractBoardListServlet {

    private final static Logger log = Logger.getLogger(BoardIconServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            final RetrievePoolRequest response = dataStore.process(new RetrievePoolRequest(new LiquidSessionIdentifier("anon"), new LiquidBoardURL(req.getParameter("board")).asURI(), false, false));
            req.setAttribute("board", response.getResponse().getCamelCaseMap());
            final String jsp = req.getParameter("gwt.codesvr") == null ? "_pages/iconmaker.jsp" : "_pages/iconmaker.jsp?gwt.codesvr=" + req.getParameter("gwt.codesvr");
            req.getRequestDispatcher(jsp).forward(req, resp);
        } catch (Exception e) {
            log.error(e);
        }
    }


}
