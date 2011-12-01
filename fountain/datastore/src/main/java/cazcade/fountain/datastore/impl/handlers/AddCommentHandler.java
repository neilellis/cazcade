package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.AddCommentRequestHandler;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.AddCommentRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class AddCommentHandler extends AbstractUpdateHandler<AddCommentRequest> implements AddCommentRequestHandler {
    @Nonnull
    @Override
    public AddCommentRequest handle(@Nonnull final AddCommentRequest request) throws InterruptedException {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final LSDPersistedEntity commentTargetPersistedEntity = request.getTarget() != null ? fountainNeo.findByUUID(request.getTarget()) : fountainNeo.findByURI(request.getUri());
            final LSDTransferEntity response = poolDAO.convertNodeToEntityWithRelatedEntitiesNoTX(request.getSessionIdentifier(), poolDAO.addCommentNoTX(commentTargetPersistedEntity, request.getRequestEntity(), request.getAlias()), null, request.getDetail(), request.isInternal(), false);

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