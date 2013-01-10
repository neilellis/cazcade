/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.CreatePoolRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.CreatePoolRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CreatePoolHandler extends AbstractDataStoreHandler<CreatePoolRequest> implements CreatePoolRequestHandler {
    @Nonnull
    public CreatePoolRequest handle(@Nonnull final CreatePoolRequest request) throws InterruptedException {
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

            final LSDPersistedEntity parentPersistedEntity = neo.findByURI(request.getParent());

            if (parentPersistedEntity == null) {
                throw new DataStoreException("No such parent pool " + request.getParent());
            }
            LiquidURI owner = request.getAlias();
            owner = defaultAndCheckOwner(request, owner);

            final LSDPersistedEntity pool = poolDAO.createPoolNoTx(request.getSessionIdentifier(), owner, parentPersistedEntity, request
                    .getType(), request.getName(), request.getX(), request.getY(), request.getTitle(), request.isListed());
            pool.setAttribute(LSDAttribute.DESCRIPTION, request.getDescription());
            final LSDTransferEntity entity = poolDAO.convertNodeToEntityWithRelatedEntitiesNoTX(request.getSessionIdentifier(), pool, null, request
                    .getDetail(), request.isInternal(), false);
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