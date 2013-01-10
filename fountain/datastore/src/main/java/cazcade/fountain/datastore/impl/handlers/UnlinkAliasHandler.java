/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.UnlinkAliasRequestHandler;
import cazcade.liquid.api.request.UnlinkAliasRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UnlinkAliasHandler extends AbstractDataStoreHandler<UnlinkAliasRequest> implements UnlinkAliasRequestHandler {
    @Nonnull
    public UnlinkAliasRequest handle(@Nonnull final UnlinkAliasRequest request) throws InterruptedException {
        return LiquidResponseHelper.forServerSuccess(request, userDAO.unlinkAliasTX(request.getSessionIdentifier(), request.getTarget(), request
                .isInternal(), request.getDetail()));
    }
}