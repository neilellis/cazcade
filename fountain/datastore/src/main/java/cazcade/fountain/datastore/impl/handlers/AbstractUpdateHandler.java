package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.request.AbstractUpdateRequest;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractUpdateHandler<T extends AbstractUpdateRequest> extends AbstractDataStoreHandler<T> {
    public T handle(T request) throws Exception {
        if (request.getUri() != null) {
            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.updateEntityByURITx(request.getSessionIdentifier(), request.getUri(), request.getEntity(), request.isInternal(), request.getDetail(), null));
        } else {
            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.updateEntityByUUIDTx(request.getSessionIdentifier(), request.getTarget(), request.getEntity(), request.isInternal(), request.getDetail(), null));
        }
    }
}
