/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.servlet.board;

import cazcade.common.Logger;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.BoardQueryRequest;

import javax.annotation.Nonnull;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class BoardQueryServlet extends AbstractBoardListServlet {

    public static final  int                                      MAX         = 24;
    @Nonnull
    private static final Logger                                   log         = Logger.getLogger(BoardQueryServlet.class);
    @Nonnull
    private final        Map<String, BoardQueryRequest.QueryType> queryLookup = new HashMap<String, BoardQueryRequest.QueryType>();
    @Nonnull
    private final        Map<String, String>                      titleLookup = new HashMap<String, String>();

    @Override
    public void init(@Nonnull final ServletConfig config) throws ServletException {
        super.init(config);
        queryLookup.put("history", BoardQueryRequest.QueryType.HISTORY);
        titleLookup.put("history", "Recently Visited");
        queryLookup.put("my", BoardQueryRequest.QueryType.MY);
        titleLookup.put("my", "Your Boards");
        queryLookup.put("popular", BoardQueryRequest.QueryType.POPULAR);
        titleLookup.put("popular", "Popular Boards");
        queryLookup.put("new", BoardQueryRequest.QueryType.RECENT);
        titleLookup.put("new", "New Boards");
        queryLookup.put("profile", BoardQueryRequest.QueryType.PROFILE);
        titleLookup.put("profile", "User Boards");
    }

    @Override
    protected void doGet(@Nonnull final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            final String username = req.getParameter("user");
            //            final String queryName = req.getServletPath().substring(1, req.getServletPath().indexOf('.'));
            final String queryName = req.getParameter("query");
            final BoardQueryRequest.QueryType type = queryLookup.get(queryName);
            final LURI alias = username == null ? null : new LURI("alias:cazcade:" + username);
            final SessionIdentifier liquidSessionId = getLiquidSessionId(req.getSession(true));
            final BoardQueryRequest request = new BoardQueryRequest(liquidSessionId, type, alias, 0, 100);
            request.setMax(MAX);
            final BoardQueryRequest response = dataStore.process(request);
            //            RetrievePoolRequest response = dataStore.process(new RetrievePoolRequest(getLiquidSessionId(), new LURI("pool:///people/boardcast/public"), ChildSortOrder.POPULARITY, false));
            final List<TransferEntity> boards = response.response().children(Dictionary.CHILD_A);
            req.setAttribute("boards", makeJSPFriendly(req, boards));
            req.setAttribute("title", titleLookup.get(queryName));
            req.getRequestDispatcher("_pages/boards.jsp").forward(req, resp);
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    protected void doPost(@Nonnull final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            super.service(req, resp);
        } catch (EOFException e) {
            log.debug("EOF");
        }
    }


}
