package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.ResizePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.request.ResizePoolObjectRequest;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class ResizePoolObjectHandler extends AbstractDataStoreHandler<ResizePoolObjectRequest> implements ResizePoolObjectRequestHandler {
    @Nonnull
    public ResizePoolObjectRequest handle(@Nonnull ResizePoolObjectRequest request) throws InterruptedException {
        Transaction transaction = fountainNeo.beginTx();
        try {
            Node node = fountainNeo.findByUUID(request.getObjectUUID());
            Node viewNode = node.getSingleRelationship(FountainRelationships.VIEW, Direction.OUTGOING).getOtherNode(node);
            if (request.getWidth() != null) {
                viewNode.setAttribute(LSDAttribute.VIEW_WIDTH, request.getWidth());
            }
            if (request.getHeight() != null) {
                viewNode.setAttribute(LSDAttribute.VIEW_HEIGHT, request.getHeight());
            }
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