/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.DeletePoolRequestHandler;
import cazcade.liquid.api.request.DeletePoolRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class DeletePoolHandler extends AbstractDataStoreHandler<DeletePoolRequest> implements DeletePoolRequestHandler {
    @Nonnull
    public DeletePoolRequest handle(@Nonnull final DeletePoolRequest request) throws InterruptedException {
        if (request.hasUri()) {
            return LiquidResponseHelper.forServerSuccess(request, neo.deleteEntityTx(request.uri(), true, request.internal(), request
                    .detail()));
        } else {
            return LiquidResponseHelper.forServerSuccess(request, neo.deleteEntityTx(request.getTarget(), true, request.internal(), request
                    .detail()));
        }
    }
}