/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.fountain.datastore.impl.services.persistence.FountainNeoImpl;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.PermissionChangeType;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.handler.CreateAliasRequestHandler;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.CreateAliasRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CreateAliasHandler extends AbstractDataStoreHandler<CreateAliasRequest> implements CreateAliasRequestHandler {
    @Nonnull
    public CreateAliasRequest handle(@Nonnull final CreateAliasRequest request) throws Exception {
        final FountainNeo neo = this.neo;
        final Transaction transaction = neo.beginTx();
        try {
            final SessionIdentifier session = request.session();
            final PersistedEntity userPersistedEntityImpl = this.neo.find(session.userURL());
            assert userPersistedEntityImpl != null;
            final PersistedEntity aliasPersistedEntity = userDAO.createAlias(userPersistedEntityImpl, request.request(), request.isMe(), request
                    .isOrCreate(), request.isClaim(), false);
            final TransferEntity entity = aliasPersistedEntity.toTransfer(request.detail(), request.internal());
            final String fullName = entity.has$(Dictionary.FULL_NAME) ? entity.$(Dictionary.FULL_NAME) : null;
            final String name = entity.$(Dictionary.NAME);
            if (aliasPersistedEntity.uri().asString().startsWith("alias:cazcade")) {
                if (!aliasPersistedEntity.has$(Dictionary.ROLE_TITLE)) {
                    aliasPersistedEntity.$(Dictionary.ROLE_TITLE, "Early Adopter");
                }
            }
            poolDAO.createPoolsForAliasNoTx(entity.uri(), name, fullName, false);
            //we reserve boards with user's name to avoid confusion with their profile boards.
            final PersistedEntity boardsPoolEntity = this.neo.find(new LiquidURI(FountainNeoImpl.BOARDS_URI));
            assert boardsPoolEntity != null;
            final PersistedEntity reservedPool = poolDAO.createPoolNoTx(request.session(), request.alias(), boardsPoolEntity, name, 0, 0, fullName, true);
            //            fountainNeo.removeAllPermissions(reservedPool);
            this.neo.changeNodePermissionNoTx(reservedPool, request.session(), PermissionChangeType.MAKE_PUBLIC_READONLY);
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