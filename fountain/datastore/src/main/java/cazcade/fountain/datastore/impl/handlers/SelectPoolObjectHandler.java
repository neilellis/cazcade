/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.SelectPoolObjectRequestHandler;
import cazcade.liquid.api.request.SelectPoolObjectRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class SelectPoolObjectHandler extends AbstractUpdateHandler<SelectPoolObjectRequest> implements SelectPoolObjectRequestHandler {
    @Nonnull @Override
    public SelectPoolObjectRequest handle(@Nonnull final SelectPoolObjectRequest request) throws InterruptedException {
        return LiquidResponseHelper.forServerSuccess(request, poolDAO.selectPoolObjectTx(request.getSessionIdentifier(), request.isSelected(), request
                .getUri(), request.isInternal(), request.getDetail()));
    }
}