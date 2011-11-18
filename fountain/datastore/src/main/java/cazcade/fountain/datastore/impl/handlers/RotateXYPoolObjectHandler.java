package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.RotateXYPoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.request.RotateXYPoolObjectRequest;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author neilelliz@cazcade.com
 */
public class RotateXYPoolObjectHandler extends AbstractDataStoreHandler<RotateXYPoolObjectRequest> implements RotateXYPoolObjectRequestHandler {

    public RotateXYPoolObjectRequest handle(RotateXYPoolObjectRequest request) throws InterruptedException {
        Transaction transaction = fountainNeo.beginTx();
        try {
            Node node = fountainNeo.findByUUID(request.getObjectUUID());
            Node viewNode = node.getSingleRelationship(FountainRelationships.VIEW, Direction.OUTGOING).getOtherNode(node);

            if (request.getAngle() != null) {

                node.setProperty(LSDAttribute.VIEW_ROTATE_XY.getKeyName(), request.getAngle().toString());
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