package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.SelectPoolObjectRequestHandler;
import cazcade.liquid.api.request.SelectPoolObjectRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class SelectPoolObjectHandler extends AbstractUpdateHandler<SelectPoolObjectRequest> implements SelectPoolObjectRequestHandler {
    @Override
    public SelectPoolObjectRequest handle(SelectPoolObjectRequest request) throws InterruptedException {
        return LiquidResponseHelper.forServerSuccess(request, poolDAO.selectPoolObjectTx(request.getSessionIdentifier(), request.isSelected(), request.getUri(), request.isInternal(), request.getDetail()));

    }

}