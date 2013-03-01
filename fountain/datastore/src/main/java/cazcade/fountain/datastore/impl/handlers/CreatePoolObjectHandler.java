/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.handler.CreatePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.CreatePoolObjectRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CreatePoolObjectHandler extends AbstractDataStoreHandler<CreatePoolObjectRequest> implements CreatePoolObjectRequestHandler {
    @Nonnull
    public CreatePoolObjectRequest handle(@Nonnull final CreatePoolObjectRequest request) throws Exception {
        final FountainNeo neo = this.neo;
        final Transaction transaction = neo.beginTx();
        try {
            final PersistedEntity poolPersistedEntity;
            if (request.hasUri()) {
                final LURI poolURI = request.getPoolURI();
                poolPersistedEntity = this.neo.find(poolURI);
                if (poolPersistedEntity == null) {
                    throw new DataStoreException("No such parent pool " + poolURI);
                }
            } else {
                final LiquidUUID pool = request.getPool();
                assert pool != null;
                poolPersistedEntity = this.neo.find(pool);
            }
            final LURI owner = request.alias();
            //            owner = defaultAndCheckOwner(request, owner);
            final LURI result;
            if (request.hasAuthor()) {
                result = request.getAuthor();
            } else {
                result = request.alias();
            }
            final TransferEntity entity = poolDAO.createPoolObjectTx(poolPersistedEntity, request.session(), owner, result, request.request(), request
                    .detail(), request.internal(), true);
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