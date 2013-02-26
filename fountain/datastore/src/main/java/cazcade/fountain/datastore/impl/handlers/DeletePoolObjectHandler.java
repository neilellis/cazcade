/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.DeletePoolObjectRequestHandler;
import cazcade.liquid.api.request.DeletePoolObjectRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class DeletePoolObjectHandler extends AbstractDeletionHandler<DeletePoolObjectRequest> implements DeletePoolObjectRequestHandler {
    @Nonnull
    public DeletePoolObjectRequest handle(@Nonnull final DeletePoolObjectRequest request) throws Exception {
        if (request.hasUri()) {
            return LiquidResponseHelper.forServerSuccess(request, poolDAO.deletePoolObjectTx(request.uri(), request.internal(), request
                    .detail()));
        } else {
            throw new UnsupportedOperationException("Only URI deletions supported now.");
            //            return LiquidResponseHelper.forServerSuccess(request, poolDAO.deletePoolObjectTx(request.getTarget(), request.internal(), request.detail()));
        }
    }
}