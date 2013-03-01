/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.client.validation;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.AuthorizationService;
import cazcade.fountain.datastore.api.AuthorizationStatus;
import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.Type;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.AbstractRetrievalRequest;
import cazcade.liquid.api.request.AuthorizationRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class AuthorizationServiceImpl implements AuthorizationService {
    @Nonnull
    private static final Logger log = Logger.getLogger(AuthorizationServiceImpl.class);

    private FountainDataStore dataStore;

    @Nonnull
    public AuthorizationStatus authorize(final SessionIdentifier identity, final LiquidUUID resource, final Permission permission) throws Exception {
        final LiquidMessage message = dataStore.process(new AuthorizationRequest(identity, resource, permission));
        final Entity response = message.response();
        final Type type = response.type().getPrimaryType();
        if (Types.T_AUTHORIZATION_ACCEPTANCE.equals(type)) {
            return AuthorizationStatus.ACCEPTED;
        } else if (Types.T_AUTHORIZATION_DENIAL.equals(type)) {
            return AuthorizationStatus.DENIED;
        } else if (Types.T_AUTHORIZATION_NOT_REQUIRED.equals(type)) {
            return AuthorizationStatus.NOT_REQUIRED;
        } else if (Types.T_AUTHORIZATION_INVALID.equals(type)) {
            return AuthorizationStatus.INVALID;
        } else if (Types.T_EXCEPTION.equals(type.getClassOnlyType())) {
            log.error(message.response().$(Dictionary.TITLE));
            log.error(message.response().$(Dictionary.TEXT));
            return AuthorizationStatus.INVALID;
        } else {
            log.warn("Authorization denied due to the following reason:");
            log.warn(message.response().$(Dictionary.TITLE));
            log.warn(message.response().$(Dictionary.TEXT));
            return AuthorizationStatus.INVALID;
        }
    }

    @Nullable
    public LiquidRequest authorize(@Nonnull final LiquidRequest liquidRequest) throws Exception {
        final List<AuthorizationRequest> authRequests = liquidRequest.authorizationRequests();
        for (final AuthorizationRequest authRequest : authRequests) {
            log.debug("Authorization request {0} for {1} being processed.", authRequest.getActions(), authRequest.hasTarget()
                                                                                                      ? authRequest.getTarget()
                                                                                                      : "<no target>");
            authRequest.session(liquidRequest.session());
            final LiquidMessage message = dataStore.process(authRequest);
            //            //noinspection ConstantConditions
            //            if (message == null) {
            //                throw new NullPointerException("Received a null message back from the data store during authorization.");
            //            }
            //            //noinspection ConstantConditions
            //            if (message.response() == null) {
            //                throw new NullPointerException("Received a null response back from the data store during authorization.");
            //            }
            final Type type = message.response().type().getPrimaryType();
            if (Types.T_AUTHORIZATION_ACCEPTANCE.equals(type)) {
                log.debug("SUCCESS for authorization.");
            } else {
                log.debug("FAILED authorization for actions {0}", authRequest.getActions());
                message.state(MessageState.FAIL);
                return (LiquidRequest) message;
            }
        }
        return null;
    }

    @Nonnull
    public LiquidMessage postAuthorize(final SessionIdentifier identity, @Nonnull final AbstractRetrievalRequest message, final Permission permission) throws Exception {
        log.debug("Post authorizing: {0}", message.getClass().getSimpleName());
        final Entity response = message.response();
        if (response.type().getPrimaryType().isSystemType()) {
            return message;
        }
        final LiquidUUID resource = response.id();
        final LiquidMessage authMessage = dataStore.process(new AuthorizationRequest(identity, resource, permission));
        final Type type = authMessage.response().type().getPrimaryType();
        if (Types.T_AUTHORIZATION_ACCEPTANCE.equals(type)) {
            log.debug("SUCCESS authorizing: {0}", message.getClass().getSimpleName());
            return message;
        } else {
            log.debug("FAILED authorizing: {0}", message.getClass().getSimpleName());
            return authMessage;
        }
    }

    public void setDataStore(final FountainDataStore dataStore) {
        this.dataStore = dataStore;
    }
}
