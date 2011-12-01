package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.handler.UpdatePoolRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.UpdatePoolRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdatePoolHandler extends AbstractUpdateHandler<UpdatePoolRequest> implements UpdatePoolRequestHandler {

    @Nonnull
    public UpdatePoolRequest handle(@Nonnull final UpdatePoolRequest request) throws Exception {

        final Transaction transaction = fountainNeo.beginTx();
        try {
            LSDEntity entity;
            final Node node;

            if (request.getUri() == null) {
                throw new UnsupportedOperationException("Only URI based updates of pools supported");
            } else {
            }

            final LiquidRequestDetailLevel detail = request.getDetail();
            final boolean internal = request.isInternal();
            final boolean historical = false;
            final Integer end = null;
            final int start = 0;
            final ChildSortOrder order = null;
            final boolean contents = true;


            node = fountainNeo.findByURI(request.getUri());
            LiquidSessionIdentifier sessionIdentifier = request.getSessionIdentifier();
            final LSDEntity requestEntity = request.getRequestEntity();

            Runnable onRenameAction = new Runnable() {
                @Override
                public void run() {
                    try {
                        poolDAO.recalculatePoolURIs(node);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            };

            entity = poolDAO.updatePool(sessionIdentifier, node, detail, internal, historical, end, start, order, contents, requestEntity, onRenameAction);

            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }

    }

}