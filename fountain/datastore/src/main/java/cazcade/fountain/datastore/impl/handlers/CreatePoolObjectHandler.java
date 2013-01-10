/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.handler.CreatePoolObjectRequestHandler;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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
            final LSDPersistedEntity poolPersistedEntity;
            if (request.hasUri()) {
                final LiquidURI poolURI = request.getPoolURI();
                poolPersistedEntity = fountainNeo.findByURI(poolURI);
                if (poolPersistedEntity == null) {
                    throw new DataStoreException("No such parent pool " + poolURI);
                }
            }
            else {
                final LiquidUUID pool = request.getPool();
                assert pool != null;
                poolPersistedEntity = fountainNeo.findByUUID(pool);
            }
            final LiquidURI owner = request.getAlias();
            //            owner = defaultAndCheckOwner(request, owner);
            final LiquidURI result;
            if (request.hasAuthor()) {
                result = request.getAuthor();
            }
            else {
                result = request.getAlias();
            }
            final LSDTransferEntity entity = poolDAO.createPoolObjectTx(poolPersistedEntity, request.getSessionIdentifier(), owner, result, request
                    .getRequestEntity(), request.getDetail(), request.isInternal(), true);
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