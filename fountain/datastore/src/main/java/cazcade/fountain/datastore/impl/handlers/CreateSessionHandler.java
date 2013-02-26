/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.handler.CreateSessionRequestHandler;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.CreateSessionRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CreateSessionHandler extends AbstractDataStoreHandler<CreateSessionRequest> implements CreateSessionRequestHandler {
    @Nonnull
    public CreateSessionRequest handle(@Nonnull final CreateSessionRequest request) throws InterruptedException {
        final FountainNeo neo = this.neo;
        final Transaction transaction = neo.beginTx();
        try {
            final PersistedEntity sessionPersistedEntity = userDAO.createSession(request.uri(), request.getClient());
            final TransferEntity entity = sessionPersistedEntity.toTransfer(request.detail(), request.internal());
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (RuntimeException e) {
            transaction.failure();
            return LiquidResponseHelper.forException(e, request);
        } finally {
            transaction.finish();
        }
    }
}