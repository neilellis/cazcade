/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.handler.RetrievePoolRosterRequestHandler;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.request.RetrievePoolRosterRequest;
import cazcade.liquid.impl.UUIDFactory;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrievePoolRosterHandler extends AbstractDataStoreHandler<RetrievePoolRosterRequest> implements RetrievePoolRosterRequestHandler {
    @Nonnull
    public RetrievePoolRosterRequest handle(@Nonnull final RetrievePoolRosterRequest request) throws InterruptedException {
        PersistedEntity persistedEntity;
        final Transaction transaction = neo.beginTx();
        try {
            final Collection<Entity> entities;
            final TransferEntity entity = SimpleEntity.create(Types.T_ALIAS_LIST);
            entity.timestamp();
            entity.id(UUIDFactory.randomUUID());

            if (request.hasUri()) {
                entities = socialDAO.getRosterNoTX(request.uri(), request.internal(), request.session(), request.detail());
            } else {
                entities = socialDAO.getRosterNoTX(request.getTarget(), request.internal(), request.session(), request.detail());
            }
            transaction.success();
            if (entities == null || entities.isEmpty()) {
                return LiquidResponseHelper.forEmptyResultResponse(request);
            } else {
                entity.children(Dictionary.CHILD_A, entities);
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