/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.handler.MovePoolObjectRequestHandler;
import cazcade.liquid.api.request.MovePoolObjectRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class MovePoolObjectHandler extends AbstractDataStoreHandler<MovePoolObjectRequest> implements MovePoolObjectRequestHandler {
    @Nonnull
    public MovePoolObjectRequest handle(@Nonnull final MovePoolObjectRequest request) throws Exception {
        final Transaction transaction = neo.beginTx();
        try {
            final PersistedEntity viewPersistedEntity = poolDAO.movePoolObjectNoTx(request.uri(), request.x(), request.y(), request
                    .getZ());
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