package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.AbstractUpdateRequest;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AbstractUpdateHandler<T extends AbstractUpdateRequest> extends AbstractDataStoreHandler<T> {
    public T handle(@Nonnull final T request) throws Exception {
        final LiquidURI uri = request.getUri();
        final LiquidSessionIdentifier sessionIdentifier = request.getSessionIdentifier();
        final LSDTransferEntity requestEntity = request.getRequestEntity();
        if (sessionIdentifier == null) {
            throw new NullPointerException("Attempted to call an update handler with a null session identifier.");
        }
        if (requestEntity == null) {
            throw new NullPointerException("Attempted to pass a null request entity to an update handler.");
        }
        if (uri != null) {
            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.updateEntityByURITx(sessionIdentifier, uri,
                                                                                                  requestEntity,
                                                                                                  request.isInternal(),
                                                                                                  request.getDetail(), null
                                                                                                 )
                                                        );
        }
        else {
            final LiquidUUID target = request.getTarget();
            if (target == null) {
                throw new NullPointerException("Attempted to pass a null target to an update handler.");
            }
            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.updateEntityByUUIDTx(sessionIdentifier, target,
                                                                                                   requestEntity,
                                                                                                   request.isInternal(),
                                                                                                   request.getDetail(), null
                                                                                                  )
                                                        );
        }
    }
}
