/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.UpdateSessionRequestHandler;
import cazcade.liquid.api.request.UpdateSessionRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdateSessionHandler extends AbstractUpdateHandler<UpdateSessionRequest> implements UpdateSessionRequestHandler {
    @Nonnull
    public UpdateSessionRequest handle(@Nonnull final UpdateSessionRequest request) throws InterruptedException {
        return LiquidResponseHelper.forServerSuccess(request, neo.updateUnversionedEntityByUUIDTx(request.getTarget(), request.request(), request
                .internal(), request.detail(), null));
    }
}