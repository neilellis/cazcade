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

public class ClaimAliasRequest extends AbstractRequest {
    public ClaimAliasRequest(@Nullable final LiquidUUID id, final SessionIdentifier identity) {
        super();
        id(id);
        session(identity);
    }

    public ClaimAliasRequest(final SessionIdentifier identity) {
        this(null, identity);
    }

    public ClaimAliasRequest() {
        super();
    }

    public ClaimAliasRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new ClaimAliasRequest(getEntity());
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
        return RequestType.R_CLAIM_ALIAS;
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