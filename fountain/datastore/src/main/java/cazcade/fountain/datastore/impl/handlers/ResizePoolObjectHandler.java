package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.ResizePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.request.ResizePoolObjectRequest;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class ResizePoolObjectHandler extends AbstractDataStoreHandler<ResizePoolObjectRequest> implements ResizePoolObjectRequestHandler {
    @Nonnull
    public ResizePoolObjectRequest handle(@Nonnull final ResizePoolObjectRequest request) throws InterruptedException {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final LSDPersistedEntity persistedEntity = fountainNeo.findByUUID(request.getObjectUUID());
            final LSDPersistedEntity viewPersistedEntity = persistedEntity.getSingleRelationship(FountainRelationships.VIEW, Direction.OUTGOING).getOtherNode(persistedEntity);
            if (request.getWidth() != null) {
                viewPersistedEntity.setAttribute(LSDAttribute.VIEW_WIDTH, request.getWidth());
            }
            if (request.getHeight() != null) {
                viewPersistedEntity.setAttribute(LSDAttribute.VIEW_HEIGHT, request.getHeight());
            }
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, viewPersistedEntity.convertNodeToLSD(request.getDetail(), request.isInternal()));
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}