/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ChangePasswordRequest extends AbstractRequest {
    public ChangePasswordRequest(final SessionIdentifier identity, final String password, final String hash) {
        super();
        session(identity);
        setPassword(password);
        setChangePasswordSecurityHash(hash);
    }

    public ChangePasswordRequest(final LiquidUUID id, final SessionIdentifier identity, final String password) {
        super();
        setPassword(password);
        id(id);
        session(identity);
    }

    public ChangePasswordRequest(final SessionIdentifier identity, final String password) {
        super();
        session(identity);
        setPassword(password);
    }

    public ChangePasswordRequest(final SessionIdentifier sessionIdentifier) {
        super();
        session(sessionIdentifier);
    }

    public ChangePasswordRequest(@Nonnull final TransferEntity entity) {
        super(entity);
    }

    public ChangePasswordRequest(final String password) {
        super();
        setPassword(password);
    }

    public ChangePasswordRequest() {
        super();
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new ChangePasswordRequest(getEntity());
    }

    public Collection<LURI> affectedEntities() {
        return Arrays.asList(session().aliasURI());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Collections.EMPTY_LIST;
    }

    @Nullable
    public List<String> notificationLocations() {
        return null;
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_CHANGE_PASSWORD;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }

    public boolean isMutationRequest() {
        return true;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}