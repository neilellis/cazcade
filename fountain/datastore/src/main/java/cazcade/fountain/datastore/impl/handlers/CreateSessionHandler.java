package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.CreateSessionRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.CreateSessionRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CreateSessionHandler extends AbstractDataStoreHandler<CreateSessionRequest> implements CreateSessionRequestHandler {


    @Nonnull
    public CreateSessionRequest handle(@Nonnull final CreateSessionRequest request) throws InterruptedException {
        final FountainNeo neo = fountainNeo;
        final Transaction transaction = neo.beginTx();
        try {
            final Node sessionNode = userDAO.createSession(request.getUri(), request.getClient());
            final LSDEntity entity = sessionNode.convertNodeToLSD(request.getDetail(), request.isInternal());
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