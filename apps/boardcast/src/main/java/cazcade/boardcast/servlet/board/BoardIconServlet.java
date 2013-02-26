/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.servlet.board;

import cazcade.common.Logger;
import cazcade.liquid.api.BoardURL;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.request.RetrievePoolRequest;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author neilellis@cazcade.com
 */
public class BoardIconServlet extends AbstractBoardListServlet {

    @Nonnull
    private static final Logger log = Logger.getLogger(BoardIconServlet.class);

    @Override
    protected void doGet(@Nonnull final HttpServletRequest req, @Nonnull final HttpServletResponse resp) throws ServletException, IOException {
        try {
            final String board = req.getParameter("board");
            if (board == null) {
                resp.sendError(400, "No board specified.");
                return;
            }
            final RetrievePoolRequest response = dataStore.process(new RetrievePoolRequest(new SessionIdentifier("anon"), new BoardURL(board)
                    .asURI(), false, false));
            req.setAttribute("board", response.response().getCamelCaseMap());
            final String jsp = req.getParameter("gwt.codesvr") == null
                               ? "_pages/iconmaker.jsp"
                               : "_pages/iconmaker.jsp?gwt.codesvr=" + req.getParameter("gwt.codesvr");
            req.getRequestDispatcher(jsp).forward(req, resp);
        } catch (Exception e) {
            log.error(e);
        }
    }


}
