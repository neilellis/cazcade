package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.AbstractRetrievalRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractRetrievalHandler<T extends AbstractRetrievalRequest> extends AbstractDataStoreHandler<T> {
    public T handle(@Nonnull final T request) throws Exception {
        LSDEntity entity;
        if (request.getTarget() != null) {
            entity = fountainNeo.getEntityByUUID(request.getTarget(), request.isInternal(), request.getDetail());
        } else {
            final FountainNeo neo = fountainNeo;
            final Transaction transaction = neo.beginTx();
            try {
                final Node node = fountainNeo.findByURI(request.getUri());
                if (node == null) {
                    return LiquidResponseHelper.forEmptyResultResponse(request);
                }
                entity = node.convertNodeToLSD(request.getDetail(), request.isInternal());
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        }
        return LiquidResponseHelper.forServerSuccess(request, entity);
    }
}
