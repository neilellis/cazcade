package cazcade.boardcast.servlet.board;

import cazcade.common.Logger;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.BoardQueryRequest;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class BoardQueryServlet extends AbstractBoardListServlet {

    private Map<String, BoardQueryRequest.QueryType> queryLookup = new HashMap<String, BoardQueryRequest.QueryType>();
    private Map<String, String> titleLookup = new HashMap<String, String>();
    private final static Logger log = Logger.getLogger(BoardQueryServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String username = req.getParameter("user");
//            final String queryName = req.getServletPath().substring(1, req.getServletPath().indexOf('.'));
            String queryName = req.getParameter("query");
            final BoardQueryRequest.QueryType type = queryLookup.get(queryName);
            final LiquidURI alias = username == null ? null : new LiquidURI("alias:cazcade:" + username);
            LiquidSessionIdentifier liquidSessionId = getLiquidSessionId(req.getSession(true));
            if (liquidSessionId == null) {
                liquidSessionId = LiquidSessionIdentifier.ANON;
            }
            BoardQueryRequest response = dataStore.process(new BoardQueryRequest(liquidSessionId, type, alias));
//            RetrievePoolRequest response = dataStore.process(new RetrievePoolRequest(getLiquidSessionId(), new LiquidURI("pool:///people/hashbo/public"), ChildSortOrder.POPULARITY, false));
            List<LSDEntity> boards = response.getResponse().getSubEntities(LSDAttribute.CHILD);
            req.setAttribute("boards", makeJSPFriendly(boards));
            req.setAttribute("title", titleLookup.get(queryName));
            req.getRequestDispatcher("_pages/boards.jsp").forward(req, resp);
        } catch (Exception e) {
            log.error(e);
        }
    }


}
