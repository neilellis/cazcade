package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrievePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.RetrievePoolObjectRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrievePoolObjectHandler extends AbstractRetrievalHandler<RetrievePoolObjectRequest> implements RetrievePoolObjectRequestHandler {

    @Nonnull
    public RetrievePoolObjectRequest handle(@Nonnull final RetrievePoolObjectRequest request) throws InterruptedException {
        final LSDEntity entity;
        if (request.getTarget() == null) {
            entity = poolDAO.getPoolObjectTx(request.getSessionIdentifier(), request.getUri(), request.isInternal(), request.isHistorical(), request.getDetail());
            if (entity == null) {
                return LiquidResponseHelper.forEmptyResultResponse(request);
            }
        } else {
            throw new UnsupportedOperationException("Only URI retrieval supported now.");
//            entity = poolDAO.getPoolObjectTx(request.getSessionIdentifier(), request.getTarget(), request.isInternal(), request.isHistorical(), request.getDetail());
        }
        return LiquidResponseHelper.forServerSuccess(request, entity);
    }
}