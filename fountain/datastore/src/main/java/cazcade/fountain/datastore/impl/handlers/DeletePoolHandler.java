package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.DeletePoolRequestHandler;
import cazcade.liquid.api.request.DeletePoolRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class DeletePoolHandler extends AbstractDataStoreHandler<DeletePoolRequest> implements DeletePoolRequestHandler {
    public DeletePoolRequest handle(DeletePoolRequest request) throws InterruptedException {
        if (request.getUri() != null) {
            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.deleteEntityTx(request.getUri(), true, request.isInternal(), request.getDetail()));
        } else {
            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.deleteEntityTx(request.getTarget(), true, request.isInternal(), request.getDetail()));
        }
    }
}