package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RotateXYPoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.request.RotateXYPoolObjectRequest;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class RotateXYPoolObjectHandler extends AbstractDataStoreHandler<RotateXYPoolObjectRequest> implements RotateXYPoolObjectRequestHandler {

    @Nonnull
    public RotateXYPoolObjectRequest handle(@Nonnull RotateXYPoolObjectRequest request) throws InterruptedException {
        Transaction transaction = fountainNeo.beginTx();
        try {
            Node node = fountainNeo.findByUUID(request.getObjectUUID());
            Node viewNode = node.getSingleRelationship(FountainRelationships.VIEW, Direction.OUTGOING).getOtherNode(node);

            if (request.getAngle() != null) {

                node.setAttribute(LSDAttribute.VIEW_ROTATE_XY, request.getAngle());
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