package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrieveSessionRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.RetrieveSessionRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrieveSessionHandler extends AbstractDataStoreHandler<RetrieveSessionRequest> implements RetrieveSessionRequestHandler {
    @Nonnull
    public RetrieveSessionRequest handle(@Nonnull RetrieveSessionRequest request) throws InterruptedException {
        LSDEntity entity = fountainNeo.getEntityByUUID(request.getTarget(), request.isInternal(), request.getDetail());
        if (entity == null) {
            return LiquidResponseHelper.forEmptyResultResponse(request);

        }
        return LiquidResponseHelper.forServerSuccess(request, entity);
    }

}