package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.ResizePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.request.ResizePoolObjectRequest;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author neilelliz@cazcade.com
 */
public class ResizePoolObjectHandler extends AbstractDataStoreHandler<ResizePoolObjectRequest> implements ResizePoolObjectRequestHandler {
    public ResizePoolObjectRequest handle(ResizePoolObjectRequest request) throws InterruptedException {
        Transaction transaction = fountainNeo.beginTx();
        try {
            Node node = fountainNeo.findByUUID(request.getObject());
            Node viewNode = node.getSingleRelationship(FountainRelationships.VIEW, Direction.OUTGOING).getOtherNode(node);
            if (request.getWidth() != null) {
                viewNode.setProperty(LSDAttribute.VIEW_WIDTH.getKeyName(), request.getWidth().toString());
            }
            if (request.getHeight() != null) {
                viewNode.setProperty(LSDAttribute.VIEW_HEIGHT.getKeyName(), request.getHeight().toString());
            }
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