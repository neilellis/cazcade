package cazcade.fountain.datastore.impl.handlers;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.impl.FountainBoardQueryDAO;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.BoardQueryRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.BoardQueryRequest;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @author neilelliz@cazcade.com
 */
public class BoardQueryHandler extends AbstractDataStoreHandler<BoardQueryRequest> implements BoardQueryRequestHandler {

    private final static Logger log = Logger.getLogger(BoardQueryHandler.class);

    @Autowired
    FountainBoardQueryDAO queryDAO;

    public BoardQueryRequest handle(BoardQueryRequest request) throws InterruptedException {
        Transaction transaction = fountainNeo.beginTx();
        try {
            log.debug("Processing Pool Query of type {0}", request.getQueryType());

            LSDEntity searchResultEntity;
            if (request.getQueryType() == BoardQueryRequest.QueryType.POPULAR) {
                searchResultEntity = queryDAO.getPopularBoards(request.getStart(), request.getMax(), request.getSessionIdentifier());
            } else if (request.getQueryType() == BoardQueryRequest.QueryType.HISTORY) {
                searchResultEntity = queryDAO.getMyVisitedBoards(request.getStart(), request.getMax(), request.getSessionIdentifier());
            } else if (request.getQueryType() == BoardQueryRequest.QueryType.MY) {
                searchResultEntity = queryDAO.getMyBoards(request.getStart(), request.getMax(), request.getSessionIdentifier());
            } else if (request.getQueryType() == BoardQueryRequest.QueryType.RECENT) {
                searchResultEntity = queryDAO.getRecentPublicBoards(request.getStart(), request.getMax(), request.getSessionIdentifier());
            } else if (request.getQueryType() == BoardQueryRequest.QueryType.USERS_BOARDS) {
                searchResultEntity = queryDAO.getUserPublicBoards(request.getStart(), request.getMax(), request.getSessionIdentifier(), request.getAlias());
            } else {
                throw new DataStoreException("Unsupported query type " + request.getType());
            }
            return LiquidResponseHelper.forServerSuccess(request, searchResultEntity);
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}