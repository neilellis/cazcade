package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.CreateUserRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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
        final LSDPersistedEntity userPersistedEntity;
        try {
            final LSDTransferEntity requestEntity = request.getRequestEntity();
            userPersistedEntity = userDAO.createUser(requestEntity, false);

            final LSDTransferEntity entity = userPersistedEntity.toLSD(request.getDetail(), request.isInternal());
            if (!requestEntity.hasAttribute(LSDAttribute.NAME)) {
                throw new DataStoreException("The name attribute was null on the entity passed in to create user.");
            }
            final String username = requestEntity.getAttribute(LSDAttribute.NAME);
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
