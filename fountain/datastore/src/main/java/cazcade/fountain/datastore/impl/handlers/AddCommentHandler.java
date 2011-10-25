package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.AddCommentRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.AddCommentRequest;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author neilelliz@cazcade.com
 */
public class AddCommentHandler extends AbstractUpdateHandler<AddCommentRequest> implements AddCommentRequestHandler {
    @Override
    public AddCommentRequest handle(AddCommentRequest request) throws InterruptedException {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final Node commentTargetNode = request.getTarget() != null ? fountainNeo.findByUUID(request.getTarget()) : fountainNeo.findByURI(request.getUri());
            final LSDEntity response = poolDAO.convertNodeToEntityWithRelatedEntitiesNoTX(request.getSessionIdentifier(), poolDAO.addCommentNoTX(commentTargetNode, request.getEntity(), request.getAlias()), null, request.getDetail(), request.isInternal(), false);

            //This is an iPad app hack//
            // removed by Neil, we'll need to go back and fix a lot in the iPad application
            //request.getEntity().addSubEntity(LSDAttribute.AUTHOR, fountainNeo.convertNodeToLSD(fountainNeo.findByURI(request.getAlias()), request.getDetail(), request.isInternal()));
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, response);
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}