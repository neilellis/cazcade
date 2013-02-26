/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.UpdateAliasRequestHandler;
import cazcade.liquid.api.request.UpdateAliasRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdateAliasHandler extends AbstractUpdateHandler<UpdateAliasRequest> implements UpdateAliasRequestHandler {
    @Nonnull
    public UpdateAliasRequest handle(@Nonnull final UpdateAliasRequest request) throws Exception {
        if (request.hasUri()) {
            neo.updateEntityByURITx(request.session(), request.uri(), request.request(), request.internal(), request.detail(), null);
            return LiquidResponseHelper.forServerSuccess(request, socialDAO.getAliasAsProfileTx(request.session(), request.uri(), request
                    .internal(), request.detail()));
        } else {
            throw new UnsupportedOperationException("Only URIs supported for updateAlias");
            //            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.updateEntityByUUIDTx(request.session(), request
            //                    .getTarget(), request.request(), request.internal(), request.detail(), null));
        }
    }
}