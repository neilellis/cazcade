/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.fountain.datastore.impl.graph.LatestContentFinder;
import cazcade.liquid.api.LURI;
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
public class RetrieveUpdatesHandler extends AbstractDataStoreHandler<RetrieveUpdatesRequest> implements RetrieveUpdatesRequestHandler {
    @Nonnull
    public RetrieveUpdatesRequest handle(@Nonnull final RetrieveUpdatesRequest request) throws Exception {
        final Transaction transaction = neo.beginTx();
        try {
            final TransferEntity entity = SimpleEntity.create(Types.T_ENTITY_LIST);
            entity.timestamp();
            entity.id(UUIDFactory.randomUUID());
            final LURI initialURI = request.session().alias();
            final PersistedEntity startPersistedEntity = neo.find(initialURI);
            if (startPersistedEntity == null) {
                throw new EntityNotFoundException("Could not find start point at " + initialURI);
            }
            //todo:tune parameters and make them part of the request too...
            final List<Entity> entities = new ArrayList<Entity>(new LatestContentFinder(request.session(), neo, startPersistedEntity, request
                    .getSince(), 20, 50000, request.detail(), 100, userDAO).getNodes());
            Collections.sort(entities, new Entity.EntityPublishedComparator());
            transaction.success();
            if (entities.isEmpty()) {
                return LiquidResponseHelper.forEmptyResultResponse(request);
            } else {
                entity.children(Dictionary.CHILD_A, entities);
                return LiquidResponseHelper.forServerSuccess(request, entity);
            }
        } catch (Exception e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}
