package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.graph.LatestContentFinder;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.RetrieveUpdatesRequestHandler;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.RetrieveUpdatesRequest;
import cazcade.liquid.impl.UUIDFactory;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrieveUpdatesHandler extends AbstractDataStoreHandler<RetrieveUpdatesRequest>
        implements RetrieveUpdatesRequestHandler {
    @Nonnull
    public RetrieveUpdatesRequest handle(@Nonnull final RetrieveUpdatesRequest request) throws InterruptedException {
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final LSDTransferEntity entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.ENTITY_LIST);
            entity.timestamp();
            entity.setID(UUIDFactory.randomUUID());
            final LiquidURI initialURI = request.getSessionIdentifier().getAlias();
            final LSDPersistedEntity startPersistedEntity = fountainNeo.findByURI(initialURI);
            if (startPersistedEntity == null) {
                throw new EntityNotFoundException("Could not find start point at " + initialURI);
            }
            //todo:tune parameters and make them part of the request too...
            final List<LSDBaseEntity> entities = new ArrayList<LSDBaseEntity>(new LatestContentFinder(
                    request.getSessionIdentifier(), fountainNeo, startPersistedEntity, request.getSince(), 20, 50000,
                    request.getDetail(), 100, userDAO
            ).getNodes()
            );
            Collections.sort(entities, new LSDBaseEntity.EntityPublishedComparator());
            transaction.success();
            if (entities.isEmpty()) {
                return LiquidResponseHelper.forEmptyResultResponse(request);
            }
            else {
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
