package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.UpdatePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.UpdatePoolObjectRequest;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author neilelliz@cazcade.com
 */
public class UpdatePoolObjectHandler extends AbstractUpdateHandler<UpdatePoolObjectRequest> implements UpdatePoolObjectRequestHandler {
    @Override
    public UpdatePoolObjectRequest handle(UpdatePoolObjectRequest request) throws InterruptedException {
        Node node;
        final Transaction transaction = fountainNeo.beginTx();
        try {
            LSDEntity entity;
            Node pool = null;
            if (request.getUri() != null) {
                node = fountainNeo.findByURI(request.getUri());
                pool = fountainNeo.findByURI(request.getUri().getWithoutFragment());
            } else {
                node = fountainNeo.findByUUID(request.getTarget());
            }

            entity = poolDAO.updatePoolObjectNoTx(request.getSessionIdentifier(), request.getSessionIdentifier(), request.getRequestEntity(), pool, node, request.isInternal(), request.getDetail());
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (RuntimeException e) {
            transaction.failure();
            return LiquidResponseHelper.forException(e, request);
        } finally {
            transaction.finish();
        }


    }

}