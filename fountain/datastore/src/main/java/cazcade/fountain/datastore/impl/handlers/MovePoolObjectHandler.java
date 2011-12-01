package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.MovePoolObjectRequestHandler;
import cazcade.liquid.api.request.MovePoolObjectRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class MovePoolObjectHandler extends AbstractDataStoreHandler<MovePoolObjectRequest> implements MovePoolObjectRequestHandler {
    @Nonnull
    public MovePoolObjectRequest handle(@Nonnull final MovePoolObjectRequest request) throws Exception {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final Node viewNode = poolDAO.movePoolObjectNoTx(request.getUri(), request.getX(), request.getY(), request.getZ());
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, viewNode.convertNodeToLSD(request.getDetail(), request.isInternal()));
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}