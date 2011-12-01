package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.impl.FountainEntity;
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
        super();

    }

    @Nonnull
    public CreateUserRequest handle(@Nonnull final CreateUserRequest request) throws Exception {
        final FountainNeo neo = fountainNeo;
        final Transaction transaction = neo.beginTx();
        final FountainEntity userFountainEntity;
        try {

            userFountainEntity = userDAO.createUser(request.getRequestEntity(), false);

            final LSDEntity entity = userFountainEntity.convertNodeToLSD(request.getDetail(), request.isInternal());
            final String username = request.getRequestEntity().getAttribute(LSDAttribute.NAME);
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
