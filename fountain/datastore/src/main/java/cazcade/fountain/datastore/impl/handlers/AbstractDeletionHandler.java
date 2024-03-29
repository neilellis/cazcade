/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.request.AbstractDeletionRequest;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractDeletionHandler<T extends AbstractDeletionRequest> extends AbstractDataStoreHandler<T> {
    @Nonnull
    public T handle(@Nonnull final T request) throws Exception {
        final LiquidUUID target = request.getTarget();
        return LiquidResponseHelper.forServerSuccess(request, neo.deleteEntityTx(target, true, request.internal(), request.detail()));
    }
}
