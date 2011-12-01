package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.DeletePoolRequestHandler;
import cazcade.liquid.api.request.DeletePoolRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class DeletePoolHandler extends AbstractDataStoreHandler<DeletePoolRequest> implements DeletePoolRequestHandler {
    @Nonnull
    public DeletePoolRequest handle(@Nonnull final DeletePoolRequest request) throws InterruptedException {
        if (request.getUri() != null) {
            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.deleteEntityTx(request.getUri(), true, request.isInternal(), request.getDetail()));
        } else {
            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.deleteEntityTx(request.getTarget(), true, request.isInternal(), request.getDetail()));
        }
    }
}