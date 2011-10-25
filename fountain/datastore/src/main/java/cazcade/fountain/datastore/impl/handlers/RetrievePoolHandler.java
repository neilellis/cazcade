package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrievePoolRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.RetrievePoolRequest;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrievePoolHandler extends AbstractDataStoreHandler<RetrievePoolRequest> implements RetrievePoolRequestHandler {

    public RetrievePoolRequest handle(final RetrievePoolRequest request) throws Exception {
        Node node;
        final Transaction transaction = fountainNeo.beginTx();
        try {
            LSDEntity entity;
            if (request.getUri() != null) {
                entity = poolDAO.getPoolAndContentsNoTx(request.getUri(), request.getDetail(), request.getOrder(), request.isContents(), request.isInternal(), request.getSessionIdentifier(), 0, request.getMax(), request.isHistorical());
            } else {
                throw new UnsupportedOperationException("Only URI retrieval supported now.");
//                entity = poolDAO.getPoolAndContentsNoTx(request.getTarget(), request.getDetail(), request.getOrder(), request.isContents(), request.isInternal(), request.getSessionIdentifier(), 0, request.getMax(), request.isHistorical());
            }
            transaction.success();
            if (entity == null) {
                if (request.isOrCreate()) {
                    Node parentNode = fountainNeo.findByURI(request.getUri().getParentURI());

                    Node pool = poolDAO.createPoolNoTx(request.getSessionIdentifier(), request.getAlias(), parentNode, request.getUri().getLastPathElement(), 0.0, 0.0, request.getUri().getLastPathElement(), request.isListed());
                    final LSDEntity newPoolEntity = poolDAO.convertNodeToEntityWithRelatedEntitiesNoTX(request.getSessionIdentifier(), pool, parentNode, request.getDetail(), request.isInternal(), false);
                    transaction.success();
                    return LiquidResponseHelper.forServerSuccess(request, newPoolEntity);

                } else {
                    return LiquidResponseHelper.forEmptyResultResponse(request);
                }

            } else {
                return LiquidResponseHelper.forServerSuccess(request, entity);
            }
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }

    }
}