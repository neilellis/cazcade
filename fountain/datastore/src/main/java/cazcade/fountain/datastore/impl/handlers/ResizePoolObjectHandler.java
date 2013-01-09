package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainRelationship;
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
public class ResizePoolObjectHandler extends AbstractDataStoreHandler<ResizePoolObjectRequest>
        implements ResizePoolObjectRequestHandler {
    @Nonnull
    public ResizePoolObjectRequest handle(@Nonnull final ResizePoolObjectRequest request) throws InterruptedException {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final LSDPersistedEntity persistedEntity = fountainNeo.findByUUID(request.getObjectUUID());
            final FountainRelationship relationship = persistedEntity.getSingleRelationship(FountainRelationships.VIEW,
                    Direction.OUTGOING
            );
            assert relationship != null;
            final LSDPersistedEntity viewPersistedEntity = relationship.getOtherNode(persistedEntity);
            if (request.hasWidth()) {
                viewPersistedEntity.setAttribute(LSDAttribute.VIEW_WIDTH, request.getWidth());
            }
            if (request.hasHeight()) {
                viewPersistedEntity.setAttribute(LSDAttribute.VIEW_HEIGHT, request.getHeight());
            }
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, viewPersistedEntity.toLSD(request.getDetail(), request.isInternal())
                                                        );
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}