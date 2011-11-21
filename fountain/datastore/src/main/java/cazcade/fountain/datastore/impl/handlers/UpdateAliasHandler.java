package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.UpdateAliasRequestHandler;
import cazcade.liquid.api.request.UpdateAliasRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdateAliasHandler extends AbstractUpdateHandler<UpdateAliasRequest> implements UpdateAliasRequestHandler {
    public UpdateAliasRequest handle(UpdateAliasRequest request) throws Exception {
        if (request.getUri() != null) {
            fountainNeo.updateEntityByURITx(request.getSessionIdentifier(), request.getUri(), request.getRequestEntity(), request.isInternal(), request.getDetail(), null);
            return LiquidResponseHelper.forServerSuccess(request, socialDAO.getAliasAsProfileTx(request.getSessionIdentifier(), request.getUri(), request.isInternal(), request.getDetail()));
        } else {
            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.updateEntityByUUIDTx(request.getSessionIdentifier(), request.getTarget(), request.getRequestEntity(), request.isInternal(), request.getDetail(), null));
        }
    }

}