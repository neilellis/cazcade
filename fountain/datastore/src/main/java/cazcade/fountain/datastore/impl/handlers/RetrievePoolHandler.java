/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.handler.RetrievePoolRequestHandler;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.RetrievePoolRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class RetrievePoolHandler extends AbstractDataStoreHandler<RetrievePoolRequest> implements RetrievePoolRequestHandler {
    @Nonnull
    public RetrievePoolRequest handle(@Nonnull final RetrievePoolRequest request) throws Exception {
        PersistedEntity persistedEntity;
        final Transaction transaction = neo.beginTx();
        try {
            final TransferEntity entity;
            if (request.hasUri()) {
                entity = poolDAO.getPoolAndContentsNoTx(request.uri(), request.detail(), request.getOrder(), request.isContents(), request
                        .internal(), request.session(), 0, request.getMax(), request.historical());
            } else {
                throw new UnsupportedOperationException("Only URI retrieval supported now.");
                //                entity = poolDAO.getPoolAndContentsNoTx(request.getTarget(), request.detail(), request.getOrder(), request.isContents(), request.internal(), request.session(), 0, request.getMax(), request.historical());
            }
            transaction.success();
            if (entity == null) {
                if (request.isOrCreate()) {
                    final PersistedEntity parentPersistedEntity = neo.find(request.uri().parent());

                    final PersistedEntity pool = poolDAO.createPoolNoTx(request.session(), request.alias(), parentPersistedEntity, request
                            .uri()
                            .lastPath(), 0.0, 0.0, request.uri().lastPath(), request.listed());
                    final TransferEntity newPoolEntity = poolDAO.convertNodeToEntityWithRelatedEntitiesNoTX(request.session(), pool, parentPersistedEntity, request
                            .detail(), request.internal(), false);
                    transaction.success();
                    return LiquidResponseHelper.forServerSuccess(request, newPoolEntity);
                } else {
                    return LiquidResponseHelper.forEmptyResultResponse(request);
                }
            } else {
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