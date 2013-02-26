/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.CreatePoolRequestHandler;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.CreatePoolRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CreatePoolHandler extends AbstractDataStoreHandler<CreatePoolRequest> implements CreatePoolRequestHandler {
    @Nonnull
    public CreatePoolRequest handle(@Nonnull final CreatePoolRequest request) throws Exception {
        if (!request.getName().matches("[a-z0-9A-Z._-]+")) {
            throw new DataStoreException("Invalid poolName, should be alphanumeric.");
        }
        final FountainNeo neo = this.neo;
        final Transaction transaction = neo.beginTx();
        try {
            String parentString = request.getParent().toString();

            if (!parentString.endsWith("/")) { parentString += "/"; }

            final LiquidURI newLiquidURI = new LiquidURI(parentString + request.getName());
            if (neo.find(newLiquidURI) != null) {
                return LiquidResponseHelper.forDuplicateResource("Pool already exists.", request);
            }

            final PersistedEntity parentPersistedEntity = neo.find(request.getParent());

            if (parentPersistedEntity == null) {throw new DataStoreException("No such parent pool " + request.getParent());}

            LiquidURI owner = request.alias();
            owner = defaultAndCheckOwner(request, owner);

            final PersistedEntity pool = poolDAO.createPoolNoTx(request.session(), owner, parentPersistedEntity, request.type(), request
                    .getName(), request.getX(), request.getY(), request.getTitle(), request.listed());

            if (request.hasDescription()) { pool.$(Dictionary.DESCRIPTION, request.description()); }
            if (request.hasImageUrl()) { pool.$(Dictionary.IMAGE_URL, request.imageUrl()); }

            final TransferEntity entity = poolDAO.convertNodeToEntityWithRelatedEntitiesNoTX(request.session(), pool, null, request.detail(), request
                    .internal(), false);
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (Exception e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}