package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RetrievePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.RetrievePoolObjectRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrievePoolObjectHandler extends AbstractRetrievalHandler<RetrievePoolObjectRequest>
        implements RetrievePoolObjectRequestHandler {
    @Nonnull
    public RetrievePoolObjectRequest handle(@Nonnull final RetrievePoolObjectRequest request) throws InterruptedException {
        final LSDTransferEntity entity;
        if (request.hasTarget()) {
            throw new UnsupportedOperationException("Only URI retrieval supported now.");
//            entity = poolDAO.getPoolObjectTx(request.getSessionIdentifier(), request.getTarget(), request.isInternal(), request.isHistorical(), request.getDetail());
        } else {
            entity = poolDAO.getPoolObjectTx(request.getSessionIdentifier(), request.getUri(), request.isInternal(),
                                             request.isHistorical(), request.getDetail()
                                            );
            if (entity == null) {
                return LiquidResponseHelper.forEmptyResultResponse(request);
            }
        }
        return LiquidResponseHelper.forServerSuccess(request, entity);
    }
}