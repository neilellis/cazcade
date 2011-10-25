package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.request.AbstractDeletionRequest;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractDeletionHandler<T extends AbstractDeletionRequest> extends AbstractDataStoreHandler<T> {
    public T handle(T request) throws Exception {
        return LiquidResponseHelper.forServerSuccess(request, fountainNeo.deleteEntityTx(request.getTarget(), true, request.isInternal(), request.getDetail()));
    }
}
