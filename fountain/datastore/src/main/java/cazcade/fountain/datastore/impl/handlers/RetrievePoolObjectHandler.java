/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrievePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.RetrievePoolObjectRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrievePoolObjectHandler extends AbstractRetrievalHandler<RetrievePoolObjectRequest> implements RetrievePoolObjectRequestHandler {
    @Nonnull
    public RetrievePoolObjectRequest handle(@Nonnull final RetrievePoolObjectRequest request) throws Exception {
        final TransferEntity entity;
        if (request.hasTarget()) {
            throw new UnsupportedOperationException("Only URI retrieval supported now.");
            //            entity = poolDAO.getPoolObjectTx(request.session(), request.getTarget(), request.internal(), request.historical(), request.detail());
        } else {
            entity = poolDAO.getPoolObjectTx(request.session(), request.uri(), request.internal(), request.historical(), request.detail());
            if (entity == null) {
                return LiquidResponseHelper.forEmptyResultResponse(request);
            }
        }
        return LiquidResponseHelper.forServerSuccess(request, entity);
    }
}