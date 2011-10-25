package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.MovePoolObjectRequestHandler;
import cazcade.liquid.api.request.MovePoolObjectRequest;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author neilelliz@cazcade.com
 */
public class MovePoolObjectHandler extends AbstractDataStoreHandler<MovePoolObjectRequest> implements MovePoolObjectRequestHandler {
    public MovePoolObjectRequest handle(MovePoolObjectRequest request) throws Exception {
        Transaction transaction = fountainNeo.beginTx();
        try {
            Node viewNode = poolDAO.movePoolObjectNoTx(request.getUri(), request.getX(), request.getY(), request.getZ());
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, fountainNeo.convertNodeToLSD(viewNode, request.getDetail(), request.isInternal()));
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}