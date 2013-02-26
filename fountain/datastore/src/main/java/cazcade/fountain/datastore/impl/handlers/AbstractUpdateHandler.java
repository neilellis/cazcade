/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.AbstractUpdateRequest;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractUpdateHandler<T extends AbstractUpdateRequest> extends AbstractDataStoreHandler<T> {
    @Nonnull
    public T handle(@Nonnull final T request) throws Exception {
        final LiquidURI uri = request.uri();
        final SessionIdentifier sessionIdentifier = request.session();
        final TransferEntity requestEntity = request.request();
        if (!request.hasRequestEntity()) {
            throw new NullPointerException("Attempted to pass a null request entity to an update handler.");
        } else {
            return LiquidResponseHelper.forServerSuccess(request, neo.updateEntityByURITx(sessionIdentifier, uri, requestEntity, request
                    .internal(), request.detail(), null));
        }
    }
}
