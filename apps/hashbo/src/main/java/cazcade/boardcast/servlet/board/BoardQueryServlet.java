package cazcade.boardcast.servlet.board;

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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

    public static final int MAX = 24;
    @Nonnull
    private final Map<String, BoardQueryRequest.QueryType> queryLookup = new HashMap<String, BoardQueryRequest.QueryType>();
    @Nonnull
    private final Map<String, String> titleLookup = new HashMap<String, String>();
    @Nonnull
    private static final Logger log = Logger.getLogger(BoardQueryServlet.class);

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
        queryLookup.put("profile", BoardQueryRequest.QueryType.USERS_BOARDS);
//        titleLookup.put("profile", BoardQueryRequest.QueryType.USERS_BOARDS);
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        try {
            super.service(req, resp);
        } catch (EOFException e) {
            log.debug("EOF");
        }
    }

    @Override
    protected void doPost(@Nonnull final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(@Nonnull final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            final String username = req.getParameter("user");
//            final String queryName = req.getServletPath().substring(1, req.getServletPath().indexOf('.'));
            final String queryName = req.getParameter("query");
            final BoardQueryRequest.QueryType type = queryLookup.get(queryName);
            final LiquidURI alias = username == null ? null : new LiquidURI("alias:cazcade:" + username);
            LiquidSessionIdentifier liquidSessionId = getLiquidSessionId(req.getSession(true));
            if (liquidSessionId == null) {
                liquidSessionId = LiquidSessionIdentifier.ANON;
            }
            final BoardQueryRequest request = new BoardQueryRequest(liquidSessionId, type, alias);
            request.setMax(MAX);
            final BoardQueryRequest response = dataStore.process(request);
//            RetrievePoolRequest response = dataStore.process(new RetrievePoolRequest(getLiquidSessionId(), new LiquidURI("pool:///people/hashbo/public"), ChildSortOrder.POPULARITY, false));
            final List<LSDTransferEntity> boards = response.getResponse().getSubEntities(LSDAttribute.CHILD);
            req.setAttribute("boards", makeJSPFriendly(boards));
            req.setAttribute("title", titleLookup.get(queryName));
            req.getRequestDispatcher("_pages/boards.jsp").forward(req, resp);
        } catch (Exception e) {
            log.error(e);
        }
    }


}
