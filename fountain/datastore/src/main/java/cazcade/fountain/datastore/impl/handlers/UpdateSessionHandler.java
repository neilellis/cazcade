package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.UpdateSessionRequestHandler;
import cazcade.liquid.api.request.UpdateSessionRequest;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdateSessionHandler extends AbstractUpdateHandler<UpdateSessionRequest> implements UpdateSessionRequestHandler {
    public UpdateSessionRequest handle(UpdateSessionRequest request) throws InterruptedException {
        return LiquidResponseHelper.forServerSuccess(request, fountainNeo.updateUnversionedEntityByUUIDTx(request.getTarget(), request.getEntity(), request.isInternal(), request.getDetail(), null));
    }
}