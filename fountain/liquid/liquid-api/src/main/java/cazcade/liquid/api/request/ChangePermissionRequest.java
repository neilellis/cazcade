/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ChangePermissionRequest extends AbstractRequest {
    public ChangePermissionRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidURI objectURI, @Nullable final LiquidPermissionChangeType change) {
        super();
        setPermission(change);
        setId(id);
        setSessionId(identity);
        setUri(objectURI);
    }

    public ChangePermissionRequest(final LiquidURI objectURI, final LiquidPermissionChangeType change) {
        this(null, LiquidSessionIdentifier.ANON, objectURI, change);
    }

    public ChangePermissionRequest() {
        super();
    }

    public ChangePermissionRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new ChangePermissionRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getUri(), LiquidPermission.SYSTEM));
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList(getUri().getWithoutFragment().asReverseDNSString(), getUri().asReverseDNSString());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CHANGE_PERMISSION;
    }

    public boolean isMutationRequest() {
        return true;
    }
}