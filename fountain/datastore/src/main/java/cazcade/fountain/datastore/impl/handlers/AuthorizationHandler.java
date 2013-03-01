/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.AuthorizationException;
import cazcade.fountain.datastore.api.DeletedEntityException;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.handler.AuthorizationRequestHandler;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.AuthorizationRequest;
import org.neo4j.graphdb.Transaction;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class AuthorizationHandler extends AbstractDataStoreHandler<AuthorizationRequest> implements AuthorizationRequestHandler {
    @Nonnull
    private static final Logger log = Logger.getLogger(AuthorizationHandler.class);

    /**
     * Authorizes a user to
     *
     * @param request
     * @return
     */
    @Override @Nonnull
    public AuthorizationRequest handle(@Nonnull final AuthorizationRequest request) throws InterruptedException {
        final TransferEntity entity = SimpleEntity.createEmpty();
        final Transaction transaction = neo.beginTx();
        try {
            final PersistedEntity persistedEntity;
            if (request.hasTarget()) {
                final LiquidUUID requestTarget = request.getTarget();

                persistedEntity = neo.find(requestTarget);
            } else {
                final LURI uri = request.uri();
                if (!request.hasUri()) {
                    throw new AuthorizationException("Both target and URI were null");
                }
                persistedEntity = neo.find(uri);
                if (persistedEntity == null) {
                    log.warn("Client asked for authorization on  " + uri + " which could not be found.");
                    entity.$(Dictionary.TYPE, Types.T_AUTHORIZATION_DENIAL.getValue());
                    return LiquidResponseHelper.forServerSuccess(request, entity);
                }
            }
            if (persistedEntity.has(Dictionary.PERMISSIONS)) {
                final boolean auth = isAuthorized(request, persistedEntity);
                if (auth) {
                    entity.$(Dictionary.TYPE, Types.T_AUTHORIZATION_ACCEPTANCE.getValue());
                } else {
                    entity.$(Dictionary.TYPE, Types.T_AUTHORIZATION_DENIAL.getValue());
                }
            } else {
                entity.$(Dictionary.TYPE, Types.T_AUTHORIZATION_NOT_REQUIRED.getValue());
            }
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (DeletedEntityException dee) {
            log.warn("Client asked for authorization on  " + request.getTarget() + " which has been deleted.");
            entity.$(Dictionary.TYPE, Types.T_AUTHORIZATION_INVALID.getValue());
            entity.$(Dictionary.DESCRIPTION, request.getTarget() + " has been deleted.");
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (EntityNotFoundException enfe) {
            log.warn("Client asked for authorization on  " + request.getTarget() + " which could not be found.");
            //            entity.$(Attribute.TYPE, Types.AUTHORIZATION_INVALID.$());
            return LiquidResponseHelper.forResourceNotFound(enfe.getMessage(), request);
        } catch (RuntimeException exception) {
            transaction.failure();
            throw exception;
        } finally {
            transaction.finish();
        }
    }

    private static boolean isAuthorized(@Nonnull final AuthorizationRequest request, @Nonnull final PersistedEntity persistedEntity) throws InterruptedException {
        boolean auth;
        final SessionIdentifier sessionIdentifier = request.session();
        auth = persistedEntity.allowed(sessionIdentifier, request.getActions());
        final List<AuthorizationRequest> and = request.getAnd();
        for (final AuthorizationRequest andRequest : and) {
            if (isAuthorized(andRequest, persistedEntity)) {
                auth = false;
            }
        }
        if (!auth) {
            final List<AuthorizationRequest> alternates = request.getOr();
            for (final AuthorizationRequest orRequest : alternates) {
                if (isAuthorized(orRequest, persistedEntity)) {
                    auth = true;
                    break;
                }
            }
        }
        return auth;
    }
}