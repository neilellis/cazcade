package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.UpdateSessionRequestHandler;
import cazcade.liquid.api.request.UpdateSessionRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdateSessionHandler extends AbstractUpdateHandler<UpdateSessionRequest> implements UpdateSessionRequestHandler {
    @Nonnull
    public UpdateSessionRequest handle(@Nonnull UpdateSessionRequest request) throws InterruptedException {
        return LiquidResponseHelper.forServerSuccess(request, fountainNeo.updateUnversionedEntityByUUIDTx(request.getTarget(), request.getRequestEntity(), request.isInternal(), request.getDetail(), null));
    }
}