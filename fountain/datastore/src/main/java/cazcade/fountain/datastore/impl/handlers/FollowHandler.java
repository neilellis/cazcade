/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.FollowRequestHandler;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.FollowRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class FollowHandler extends AbstractDataStoreHandler<FollowRequest> implements FollowRequestHandler {
    @Nonnull
    public FollowRequest handle(@Nonnull final FollowRequest request) throws Exception {
        final LSDTransferEntity result;
        if (request.isFollow()) {
            result = socialDAO.followResourceTX(request.getSessionIdentifier(), request.getUri(), request.getDetail(), request.isInternal());
        }
        else {
            result = socialDAO.unfollowResourceTX(request.getSessionIdentifier(), request.getUri(), request.getDetail(), request.isInternal());
        }
        return LiquidResponseHelper.forServerSuccess(request, result);
    }
}