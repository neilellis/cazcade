package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.handler.CreateAliasRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.CreateAliasRequest;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author neilelliz@cazcade.com
 */
public class CreateAliasHandler extends AbstractDataStoreHandler<CreateAliasRequest> implements CreateAliasRequestHandler {


    public CreateAliasRequest handle(CreateAliasRequest request) throws InterruptedException {
        final FountainNeo neo = fountainNeo;
        final Transaction transaction = neo.beginTx();
        try {
            LiquidSessionIdentifier session = request.getSessionIdentifier();
            Node userNode = fountainNeo.findByURI(session.getUserURL());
            Node aliasNode = userDAO.createAlias(userNode, request.getEntity(), request.isMe(), request.isOrupdate(), request.isClaim(), false);
            final LSDEntity entity = fountainNeo.convertNodeToLSD(aliasNode, request.getDetail(), request.isInternal());
            poolDAO.createPoolsForAliasNoTx(entity.getURI(), entity.getAttribute(LSDAttribute.NAME), entity.getAttribute(LSDAttribute.FULL_NAME), false);
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}