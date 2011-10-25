package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrieveSessionRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.RetrieveSessionRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrieveSessionHandler extends AbstractDataStoreHandler<RetrieveSessionRequest> implements RetrieveSessionRequestHandler {
    public RetrieveSessionRequest handle(RetrieveSessionRequest request) throws InterruptedException {
        LSDEntity entity = fountainNeo.getEntityByUUID(request.getTarget(), request.isInternal(), request.getDetail());
        if (entity == null) {
            return LiquidResponseHelper.forEmptyResultResponse(request);

        }
        return LiquidResponseHelper.forServerSuccess(request, entity);
    }

}