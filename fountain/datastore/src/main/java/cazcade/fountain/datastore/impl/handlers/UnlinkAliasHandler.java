package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.UnlinkAliasRequestHandler;
import cazcade.liquid.api.request.UnlinkAliasRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class UnlinkAliasHandler extends AbstractDataStoreHandler<UnlinkAliasRequest> implements UnlinkAliasRequestHandler {
    public UnlinkAliasRequest handle(UnlinkAliasRequest request) throws InterruptedException {
        return LiquidResponseHelper.forServerSuccess(request, userDAO.unlinkAliasTX(request.getSessionIdentifier(), request.getTarget(), request.isInternal(), request.getDetail()));
    }
}