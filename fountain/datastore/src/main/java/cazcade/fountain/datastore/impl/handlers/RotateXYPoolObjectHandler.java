/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RotateXYPoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.request.RotateXYPoolObjectRequest;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class RotateXYPoolObjectHandler extends AbstractDataStoreHandler<RotateXYPoolObjectRequest> implements RotateXYPoolObjectRequestHandler {
    @Nonnull
    public RotateXYPoolObjectRequest handle(@Nonnull final RotateXYPoolObjectRequest request) throws InterruptedException {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final LSDPersistedEntity persistedEntity = fountainNeo.findByUUID(request.getObjectUUID());
            final FountainRelationship relationship = persistedEntity.getSingleRelationship(FountainRelationships.VIEW, Direction.OUTGOING);
            assert relationship != null;
            final LSDPersistedEntity viewPersistedEntity = relationship.getOtherNode(persistedEntity);

            if (request.hasAngle()) {
                persistedEntity.setAttribute(LSDAttribute.VIEW_ROTATE_XY, request.getAngle());
            }
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, viewPersistedEntity.toLSD(request.getDetail(), request.isInternal()));
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}