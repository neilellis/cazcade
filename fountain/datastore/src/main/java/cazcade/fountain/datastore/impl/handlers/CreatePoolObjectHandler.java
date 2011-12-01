package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.FountainEntity;
import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.CreatePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.CreatePoolObjectRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CreatePoolObjectHandler extends AbstractDataStoreHandler<CreatePoolObjectRequest> implements CreatePoolObjectRequestHandler {


    @Nonnull
    public CreatePoolObjectRequest handle(@Nonnull final CreatePoolObjectRequest request) throws Exception {
        final FountainNeo neo = fountainNeo;
        final Transaction transaction = neo.beginTx();
        try {
            final FountainEntity poolFountainEntity;
            if (request.getPoolURI() != null) {
                poolFountainEntity = fountainNeo.findByURI(request.getPoolURI());
                if (poolFountainEntity == null) {
                    throw new DataStoreException("No such parent pool " + request.getPoolURI());
                }
            } else {
                poolFountainEntity = fountainNeo.findByUUID(request.getPool());
                if (poolFountainEntity == null) {
                    throw new DataStoreException("No such parent pool " + request.getPool());
                }
            }
            final LiquidURI owner = request.getAlias();
//            owner = defaultAndCheckOwner(request, owner);
            final LiquidURI result;
            if (request.getAuthor() == null) {
                result = request.getAlias();
            } else {
                result = request.getAuthor();
            }
            final LSDEntity entity = poolDAO.createPoolObjectTx(poolFountainEntity, request.getSessionIdentifier(), owner, result, request.getRequestEntity(), request.getDetail(), request.isInternal(), true);
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