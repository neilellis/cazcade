/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.handler.UpdatePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdatePoolObjectHandler extends AbstractUpdateHandler<UpdatePoolObjectRequest> implements UpdatePoolObjectRequestHandler {
    @Nonnull @Override
    public UpdatePoolObjectRequest handle(@Nonnull final UpdatePoolObjectRequest request) throws InterruptedException {
        final PersistedEntity persistedEntityImpl;
        final Transaction transaction = neo.beginTx();
        try {
            final TransferEntity entity;
            PersistedEntity pool = null;
            if (request.hasUri()) {
                persistedEntityImpl = neo.findForWrite(request.uri());
                pool = neo.findOrFail(request.uri().withoutFragment());
            } else {
                persistedEntityImpl = neo.find(request.getTarget());
                throw new UnsupportedOperationException("Only updating by URI is supported now.");
            }

            entity = poolDAO.updatePoolObjectNoTx(request.session(), request.session(), request.request(), pool, persistedEntityImpl, request
                    .internal(), request.detail());
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (Exception e) {
            transaction.failure();
            return LiquidResponseHelper.forException(e, request);
        } finally {
            transaction.finish();
        }
    }
}