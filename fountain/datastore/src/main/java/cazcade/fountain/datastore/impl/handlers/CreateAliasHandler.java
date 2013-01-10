/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.services.persistence.FountainNeoImpl;
import cazcade.liquid.api.LiquidPermissionChangeType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.handler.CreateAliasRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.CreateAliasRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CreateAliasHandler extends AbstractDataStoreHandler<CreateAliasRequest> implements CreateAliasRequestHandler {
    @Nonnull
    public CreateAliasRequest handle(@Nonnull final CreateAliasRequest request) throws Exception {
        final FountainNeo neo = fountainNeo;
        final Transaction transaction = neo.beginTx();
        try {
            final LiquidSessionIdentifier session = request.getSessionIdentifier();
            final LSDPersistedEntity userPersistedEntityImpl = fountainNeo.findByURI(session.getUserURL());
            assert userPersistedEntityImpl != null;
            final LSDPersistedEntity aliasPersistedEntity = userDAO.createAlias(userPersistedEntityImpl, request.getRequestEntity(), request
                    .isMe(), request.isOrCreate(), request.isClaim(), false);
            final LSDTransferEntity entity = aliasPersistedEntity.toLSD(request.getDetail(), request.isInternal());
            final String fullName = entity.hasAttribute(LSDAttribute.FULL_NAME)
                                    ? entity.getAttribute(LSDAttribute.FULL_NAME)
                                    : null;
            final String name = entity.getAttribute(LSDAttribute.NAME);
            poolDAO.createPoolsForAliasNoTx(entity.getURI(), name, fullName, false);
            //we reserve boards with user's name to avoid confusion with their profile boards.
            final LSDPersistedEntity boardsPoolEntity = fountainNeo.findByURI(new LiquidURI(FountainNeoImpl.BOARDS_URI));
            assert boardsPoolEntity != null;
            final LSDPersistedEntity reservedPool = poolDAO.createPoolNoTx(request.getSessionIdentifier(), request.getAlias(), boardsPoolEntity, name, 0, 0, fullName, true);
            //            fountainNeo.removeAllPermissions(reservedPool);
            fountainNeo.changeNodePermissionNoTx(reservedPool, request.getSessionIdentifier(), LiquidPermissionChangeType.MAKE_PUBLIC_READONLY);
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