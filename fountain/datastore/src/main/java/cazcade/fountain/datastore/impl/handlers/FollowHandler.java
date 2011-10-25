package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.FollowRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.FollowRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class FollowHandler extends AbstractDataStoreHandler<FollowRequest> implements FollowRequestHandler {
    public FollowRequest handle(FollowRequest request) throws Exception {
        LSDEntity result;
        if (request.isFollow()) {
            result = socialDAO.followResourceTX(request.getSessionIdentifier(), request.getUri(), request.getDetail(), request.isInternal());
        } else {
            result = socialDAO.unfollowResourceTX(request.getSessionIdentifier(), request.getUri(), request.getDetail(), request.isInternal());
        }
        return LiquidResponseHelper.forServerSuccess(request, result);
    }

}