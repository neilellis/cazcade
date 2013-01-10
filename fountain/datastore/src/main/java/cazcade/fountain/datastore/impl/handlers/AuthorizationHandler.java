/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.AuthorizationException;
import cazcade.fountain.datastore.api.DeletedEntityException;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.handler.AuthorizationRequestHandler;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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
        final LSDTransferEntity entity = LSDSimpleEntity.createEmpty();
        final Transaction transaction = fountainNeo.beginTx();
        try {
            final LSDPersistedEntity persistedEntity;
            if (request.hasTarget()) {
                final LiquidUUID requestTarget = request.getTarget();

                persistedEntity = fountainNeo.findByUUID(requestTarget);
            }
            else {
                final LiquidURI uri = request.getUri();
                if (!request.hasUri()) {
                    throw new AuthorizationException("Both target and URI were null");
                }
                persistedEntity = fountainNeo.findByURI(uri);
                if (persistedEntity == null) {
                    log.warn("Client asked for authorization on  " + uri + " which could not be found.");
                    entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_DENIAL.getValue());
                    return LiquidResponseHelper.forServerSuccess(request, entity);
                }
            }
            if (persistedEntity.hasAttribute(LSDAttribute.PERMISSIONS)) {
                final boolean auth = isAuthorized(request, persistedEntity);
                if (auth) {
                    entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_ACCEPTANCE.getValue());
                }
                else {
                    entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_DENIAL.getValue());
                }
            }
            else {
                entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_NOT_REQUIRED.getValue());
            }
            transaction.success();
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (DeletedEntityException dee) {
            log.warn("Client asked for authorization on  " + request.getTarget() + " which has been deleted.");
            entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_INVALID.getValue());
            entity.setAttribute(LSDAttribute.DESCRIPTION, request.getTarget() + " has been deleted.");
            return LiquidResponseHelper.forServerSuccess(request, entity);
        } catch (EntityNotFoundException enfe) {
            log.warn("Client asked for authorization on  " + request.getTarget() + " which could not be found.");
            //            entity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.AUTHORIZATION_INVALID.getAttribute());
            return LiquidResponseHelper.forResourceNotFound(enfe.getMessage(), request);
        } catch (RuntimeException exception) {
            transaction.failure();
            throw exception;
        } finally {
            transaction.finish();
        }
    }

    private static boolean isAuthorized(@Nonnull final AuthorizationRequest request, @Nonnull final LSDPersistedEntity persistedEntity) throws InterruptedException {
        boolean auth;
        final LiquidSessionIdentifier sessionIdentifier = request.getSessionIdentifier();
        auth = persistedEntity.isAuthorized(sessionIdentifier, request.getActions());
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