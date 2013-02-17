/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.UpdatePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdatePoolObjectHandler extends AbstractUpdateHandler<UpdatePoolObjectRequest> implements UpdatePoolObjectRequestHandler {
    @Nonnull @Override
    public UpdatePoolObjectRequest handle(@Nonnull final UpdatePoolObjectRequest request) throws InterruptedException {
        final LSDPersistedEntity persistedEntityImpl;
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final LSDTransferEntity entity;
            LSDPersistedEntity pool = null;
            if (request.hasUri()) {
                persistedEntityImpl = fountainNeo.findByURIAndLockForWrite(request.getUri());
                pool = fountainNeo.findByURIOrFail(request.getUri().getWithoutFragment());
            } else {
                persistedEntityImpl = fountainNeo.findByUUID(request.getTarget());
                throw new UnsupportedOperationException("Only updating by URI is supported now.");
            }

            entity = poolDAO.updatePoolObjectNoTx(request.getSessionIdentifier(), request.getSessionIdentifier(), request.getRequestEntity(), pool, persistedEntityImpl, request
                    .isInternal(), request.getDetail());
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