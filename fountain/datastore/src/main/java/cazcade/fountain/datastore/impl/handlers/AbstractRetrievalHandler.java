package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.AbstractRetrievalRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractRetrievalHandler<T extends AbstractRetrievalRequest> extends AbstractDataStoreHandler<T> {
    @Nonnull
    public T handle(@Nonnull final T request) throws Exception {
        LSDTransferEntity entity;
        if (request.hasTarget()) {
            final LiquidUUID target = request.getTarget();
            entity = fountainNeo.getEntityByUUID(target, request.isInternal(), request.getDetail());
        }
        else {
            final FountainNeo neo = fountainNeo;
            final Transaction transaction = neo.beginTx();
            try {
                final LSDPersistedEntity persistedEntity = fountainNeo.findByURI(request.getUri());
                if (persistedEntity == null) {
                    return LiquidResponseHelper.forEmptyResultResponse(request);
                }
                entity = persistedEntity.toLSD(request.getDetail(), request.isInternal());
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
