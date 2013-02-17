/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import cazcade.common.Logger;
import cazcade.fountain.common.service.AbstractServiceStateMachine;
import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.handler.AuthorizationRequestHandler;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.request.AuthorizationRequest;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author Neil Ellis
 */

public class FountainLocalDataStore extends AbstractServiceStateMachine implements FountainDataStore {
    @Nonnull
    private static final Logger log = Logger.getLogger(FountainLocalDataStore.class);

    private FountainNeo                 fountainNeo;
    private FountainRequestMap          requestMap;
    private AuthorizationRequestHandler authHandler;

    @Nonnull @Override
    public <T extends LiquidRequest> T process(@Nonnull final T request) throws Exception {
        try {
            final List<AuthorizationRequest> authorizationRequests = request.getAuthorizationRequests();
            for (final AuthorizationRequest authorizationRequest : authorizationRequests) {
                authorizationRequest.setSessionId(request.getSessionIdentifier());
                final AuthorizationRequest result = authHandler.handle(authorizationRequest);
                final LSDBaseEntity responseEntity = result.getResponse();
                if (!responseEntity.isA(LSDDictionaryTypes.AUTHORIZATION_ACCEPTANCE)) {
                    LiquidResponseHelper.forFailure(request, result);
                }
            }

            final FountainRequestConfiguration config = requestMap.getConfiguration(request.getRequestType().getRequestClass());
            if (config == null) {
                throw new Error("No configuration for " + request.getClass());
            }
            if (request.getSessionIdentifier().getSession() != null) {
                fountainNeo.updateSessionTx(request.getSessionIdentifier().getSession());
            }
            return (T) config.getHandler().handle(request);
        } catch (EntityNotFoundException enfe) {
            log.debug(enfe.getMessage(), enfe);
            return LiquidResponseHelper.forEmptyResultResponse(request);
        } catch (DataStoreException de) {
            if (de.isClientException()) {
                return LiquidResponseHelper.forException(de, request);
            } else {
                log.error(de);
                throw de;
            }
        }
    }

    public void start() throws Exception {
        super.start();
        requestMap.injectNeo(fountainNeo);
    }

    public FountainRequestMap getRequestMap() {
        return requestMap;
    }

    public void setRequestMap(final FountainRequestMap requestMap) {
        this.requestMap = requestMap;
    }

    public void setAuthHandler(final AuthorizationRequestHandler authHandler) {
        this.authHandler = authHandler;
    }

    public void setFountainNeo(final FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }
}
