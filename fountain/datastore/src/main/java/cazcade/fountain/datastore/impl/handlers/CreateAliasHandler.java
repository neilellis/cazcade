package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidPermissionChangeType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
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


    public CreateAliasRequest handle(CreateAliasRequest request) throws Exception {
        final FountainNeo neo = fountainNeo;
        final Transaction transaction = neo.beginTx();
        try {
            LiquidSessionIdentifier session = request.getSessionIdentifier();
            Node userNode = fountainNeo.findByURI(session.getUserURL());
            Node aliasNode = userDAO.createAlias(userNode, request.getRequestEntity(), request.isMe(), request.isOrCreate(), request.isClaim(), false);
            final LSDEntity entity = fountainNeo.convertNodeToLSD(aliasNode, request.getDetail(), request.isInternal());
            poolDAO.createPoolsForAliasNoTx(entity.getURI(), entity.getAttribute(LSDAttribute.NAME), entity.getAttribute(LSDAttribute.FULL_NAME), false);
            //we reserve boards with user's name to avoid confusion with their profile boards.
            final Node reservedPool = poolDAO.createPoolNoTx(request.getSessionIdentifier(), request.getAlias(), fountainNeo.findByURI(new LiquidURI(FountainNeo.BOARDS_URI)), entity.getAttribute(LSDAttribute.NAME), 0, 0, entity.getAttribute(LSDAttribute.FULL_NAME), true);
//            fountainNeo.removeAllPermissions(reservedPool);
            fountainNeo.changeNodePermissionNoTx(reservedPool, request.getSessionIdentifier(), LiquidPermissionChangeType.MAKE_PUBLIC_READONLY);
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (Exception e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}