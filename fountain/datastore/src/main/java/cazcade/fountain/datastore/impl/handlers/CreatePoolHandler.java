package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.CreatePoolRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.CreatePoolRequest;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

/**
 * @author neilelliz@cazcade.com
 */
public class CreatePoolHandler extends AbstractDataStoreHandler<CreatePoolRequest> implements CreatePoolRequestHandler {

    public CreatePoolRequest handle(CreatePoolRequest request) throws InterruptedException {
        if (!request.getName().matches("[a-z0-9A-Z._-]+")) {
            throw new DataStoreException("Invalid poolName, should be alphanumeric.");
        }
        final FountainNeo neo = fountainNeo;
        final Transaction transaction = neo.beginTx();
        try {

            String parentString = request.getParent().toString();
            if (!parentString.endsWith("/")) {
                parentString += "/";
            }
            final LiquidURI newLiquidURI = new LiquidURI(parentString + request.getName());
            if (neo.findByURI(newLiquidURI) != null) {
                return LiquidResponseHelper.forDuplicateResource("Pool already exists.", request);
            }

            final Node parentNode = neo.findByURI(request.getParent());

            if (parentNode == null) {
                throw new DataStoreException("No such parent pool " + request.getParent());
            }
            LiquidURI owner = request.getAlias();
            owner = defaultAndCheckOwner(request, owner);

            Node pool = poolDAO.createPoolNoTx(request.getSessionIdentifier(), owner, parentNode, request.getType(), request.getName(), request.getX(), request.getY(), request.getTitle(), request.isListed());
            pool.setProperty(LSDAttribute.DESCRIPTION.getKeyName(), request.getDescription());
            final LSDEntity entity = poolDAO.convertNodeToEntityWithRelatedEntitiesNoTX(request.getSessionIdentifier(), pool, null, request.getDetail(), request.isInternal(), false);
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