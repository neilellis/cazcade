package cazcade.boardcast.servlet.board;

import cazcade.common.Logger;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author neilellis@cazcade.com
 */
public class BoardServlet extends AbstractBoardListServlet {

    @Nonnull
    private static final Logger log = Logger.getLogger(BoardServlet.class);


    @Override
    protected void doGet(@Nonnull final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {

//            RetrieveAliasRequest response = dataStore.process(new RetrieveAliasRequest(getLiquidSessionId(), new LiquidURI("alias:cazcade:"+parts[0])));
//            if(response.getResponse().isA(LSDDictionaryTypes.ALIAS)) {
//                req.setAttribute("board", "@"+parts[0]);
//            } else {
//                req.setAttribute("board", parts[0]);
//            }

//            final String[] parts = req.getServletPath().substring(1).split("\\.");
//            req.setAttribute("board", parts[1].equals("profile") ? "@"+parts[0] : parts[0]);
            req.setAttribute("board", req.getParameter("board"));
            final String jsp = req.getParameter("gwt.codesvr") == null ? "_pages/board.jsp" : "_pages/board.jsp?gwt.codesvr=" + req.getParameter("gwt.codesvr");
            req.getRequestDispatcher(jsp).forward(req, resp);
        } catch (Exception e) {
            log.error(e);
        }
    }


}
