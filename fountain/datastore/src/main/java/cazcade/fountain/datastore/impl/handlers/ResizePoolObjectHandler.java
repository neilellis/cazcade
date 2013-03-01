/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.handler.ResizePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.Dictionary;
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
        final Transaction transaction = neo.beginTx();
        try {
            final PersistedEntity persistedEntity = neo.find(request.getObjectUUID());
            final FountainRelationship relationship = persistedEntity.relationship(FountainRelationships.VIEW, Direction.OUTGOING);
            assert relationship != null;
            final PersistedEntity viewPersistedEntity = relationship.other(persistedEntity);
            if (request.hasWidth()) {
                viewPersistedEntity.$(Dictionary.VIEW_WIDTH, request.width());
            }
            if (request.hasHeight()) {
                viewPersistedEntity.$(Dictionary.VIEW_HEIGHT, request.height());
            }
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, viewPersistedEntity.toTransfer(request.detail(), request.internal()));
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}