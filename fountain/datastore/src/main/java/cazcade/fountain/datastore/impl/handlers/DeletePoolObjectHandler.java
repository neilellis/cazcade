package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.DeletePoolObjectRequestHandler;
import cazcade.liquid.api.request.DeletePoolObjectRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class DeletePoolObjectHandler extends AbstractDeletionHandler<DeletePoolObjectRequest>
        implements DeletePoolObjectRequestHandler {
    @Nonnull
    public DeletePoolObjectRequest handle(@Nonnull final DeletePoolObjectRequest request) throws Exception {
        if (request.hasUri()) {
            return LiquidResponseHelper.forServerSuccess(request, poolDAO.deletePoolObjectTx(request.getUri(), request.isInternal(),
                                                                                             request.getDetail()
                                                                                            )
                                                        );
        }
        else {
            throw new UnsupportedOperationException("Only URI deletions supported now.");
//            return LiquidResponseHelper.forServerSuccess(request, poolDAO.deletePoolObjectTx(request.getTarget(), request.isInternal(), request.getDetail()));
        }
    }
}