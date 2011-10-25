package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.graph.LatestContentFinder;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidURIScheme;
import cazcade.liquid.api.handler.RetrieveUpdatesRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.request.RetrieveUpdatesRequest;
import cazcade.liquid.impl.UUIDFactory;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrieveUpdatesHandler extends AbstractDataStoreHandler<RetrieveUpdatesRequest> implements RetrieveUpdatesRequestHandler {

    public RetrieveUpdatesRequest handle(final RetrieveUpdatesRequest request) throws InterruptedException {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            LSDSimpleEntity entity = LSDSimpleEntity.createEmpty();
            entity.timestamp();
            entity.setID(UUIDFactory.randomUUID());
            entity.setType(LSDDictionaryTypes.ENTITY_LIST);
            final LiquidURI initialURI = request.getSessionIdentifier().getAlias();
            Node startNode = fountainNeo.findByURI(initialURI);
            if (startNode == null) {
                throw new EntityNotFoundException("Could not find start point at " + initialURI);
            }
            //todo:tune parameters and make them part of the request too...
            final List<LSDEntity> entities = new ArrayList<LSDEntity>(new LatestContentFinder(request.getSessionIdentifier(), fountainNeo, startNode, request.getSince(), 20, 5000, request.getDetail(), 100, userDAO).getNodes());
            Collections.sort(entities, new LSDEntity.EntityPublishedComparator());
            transaction.success();
            if (entities == null || entities.size() == 0) {
                return LiquidResponseHelper.forEmptyResultResponse(request);

            } else {
                entity.addSubEntities(LSDAttribute.CHILD, entities);
                return LiquidResponseHelper.forServerSuccess(request, entity);
            }
        } catch (RuntimeException e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }

    }

}
