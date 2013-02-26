/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrieveSessionRequestHandler;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.RetrieveSessionRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrieveSessionHandler extends AbstractDataStoreHandler<RetrieveSessionRequest> implements RetrieveSessionRequestHandler {
    @Nonnull
    public RetrieveSessionRequest handle(@Nonnull final RetrieveSessionRequest request) throws InterruptedException {
        final TransferEntity entity = neo.getEntityByUUID(request.getTarget(), request.internal(), request.detail());
        if (entity == null) {
            return LiquidResponseHelper.forEmptyResultResponse(request);
        }
        return LiquidResponseHelper.forServerSuccess(request, entity);
    }
}