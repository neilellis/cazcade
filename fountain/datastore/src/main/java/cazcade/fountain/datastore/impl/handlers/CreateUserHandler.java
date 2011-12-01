package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.CreateUserRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.CreateUserRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CreateUserHandler extends AbstractDataStoreHandler<CreateUserRequest> implements CreateUserRequestHandler {


    public CreateUserHandler() {

    }

    @Nonnull
    public CreateUserRequest handle(@Nonnull CreateUserRequest request) throws Exception {
        final FountainNeo neo = fountainNeo;
        Transaction transaction = neo.beginTx();
        Node userNode;
        try {

            userNode = userDAO.createUser(request.getRequestEntity(), false);

            LSDEntity entity = userNode.convertNodeToLSD(request.getDetail(), request.isInternal());
            String username = request.getRequestEntity().getAttribute(LSDAttribute.NAME);
            if (username == null) {
                throw new DataStoreException("The name attribute was null on the entity passed in to create user.");
            }
            poolDAO.createPoolsForUserNoTx(username);
            poolDAO.createPoolsForCazcadeAliasNoTx(username, entity.getAttribute(LSDAttribute.FULL_NAME), false);
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
