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
            fountainNeo.updateEntityByURITx(request.getSessionIdentifier(), request.getUri(), request.getRequestEntity(), request.isInternal(), request
                    .getDetail(), null);
            return LiquidResponseHelper.forServerSuccess(request, socialDAO.getAliasAsProfileTx(request.getSessionIdentifier(), request
                    .getUri(), request.isInternal(), request.getDetail()));
        } else {
            throw new UnsupportedOperationException("Only URIs supported for updateAlias");
            //            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.updateEntityByUUIDTx(request.getSessionIdentifier(), request
            //                    .getTarget(), request.getRequestEntity(), request.isInternal(), request.getDetail(), null));
        }
    }
}