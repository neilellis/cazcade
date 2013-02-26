/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.ChangePermissionRequestHandler;
import cazcade.liquid.api.request.ChangePermissionRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class ChangePermissionHandler extends AbstractDataStoreHandler<ChangePermissionRequest> implements ChangePermissionRequestHandler {
    @Nonnull
    public ChangePermissionRequest handle(@Nonnull final ChangePermissionRequest request) throws Exception {
        final Transaction transaction = neo.beginTx();
        try {
            final ChangePermissionRequest message = LiquidResponseHelper.forServerSuccess(request, neo.changePermissionNoTx(request.session(), request
                    .uri(), request.permission(), request.detail(), request.internal()));
            transaction.success();
            return message;
        } catch (RuntimeException e) {
            transaction.failure();
            return LiquidResponseHelper.forException(e, request);
        } finally {
            transaction.finish();
        }
    }
}