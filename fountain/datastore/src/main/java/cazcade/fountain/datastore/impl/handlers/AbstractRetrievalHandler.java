/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.AbstractRetrievalRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractRetrievalHandler<T extends AbstractRetrievalRequest> extends AbstractDataStoreHandler<T> {
    @Nonnull
    public T handle(@Nonnull final T request) throws Exception {
        TransferEntity entity;
        if (request.hasTarget()) {
            final LiquidUUID target = request.getTarget();
            entity = neo.getEntityByUUID(target, request.internal(), request.detail());
        } else {
            final FountainNeo neo = this.neo;
            final Transaction transaction = neo.beginTx();
            try {
                final PersistedEntity persistedEntity = this.neo.find(request.uri());
                if (persistedEntity == null) {
                    return LiquidResponseHelper.forEmptyResultResponse(request);
                }
                entity = persistedEntity.toTransfer(request.detail(), request.internal());
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
