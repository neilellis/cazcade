package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.CreatePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.CreatePoolObjectRequest;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author neilelliz@cazcade.com
 */
public class CreatePoolObjectHandler extends AbstractDataStoreHandler<CreatePoolObjectRequest> implements CreatePoolObjectRequestHandler {


    public CreatePoolObjectRequest handle(CreatePoolObjectRequest request) throws Exception {
        final FountainNeo neo = fountainNeo;
        final Transaction transaction = neo.beginTx();
        try {
            Node poolNode;
            if (request.getPoolURI() != null) {
                poolNode = fountainNeo.findByURI(request.getPoolURI());
                if (poolNode == null) {
                    throw new DataStoreException("No such parent pool " + request.getPoolURI());
                }
            } else {
                poolNode = fountainNeo.findByUUID(request.getPool());
                if (poolNode == null) {
                    throw new DataStoreException("No such parent pool " + request.getPool());
                }
            }
            LiquidURI owner = request.getAlias();
//            owner = defaultAndCheckOwner(request, owner);
            LiquidURI result;
            if (request.getAuthor() == null) {
                result = request.getAlias();
            } else {
                result = request.getAuthor();
            }
            final LSDEntity entity = poolDAO.createPoolObjectTx(poolNode, request.getSessionIdentifier(), owner, result, request.getEntity(), request.getDetail(), request.isInternal(), true);
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