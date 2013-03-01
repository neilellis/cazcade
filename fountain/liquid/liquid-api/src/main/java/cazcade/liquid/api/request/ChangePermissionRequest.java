/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ChangePermissionRequest extends AbstractRequest {
    public ChangePermissionRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LURI objectURI, @Nullable final PermissionChangeType change) {
        super();
        setPermission(change);
        id(id);
        session(identity);
        setUri(objectURI);
    }

    public ChangePermissionRequest(final LURI objectURI, final PermissionChangeType change) {
        this(null, SessionIdentifier.ANON, objectURI, change);
    }

    public ChangePermissionRequest() {
        super();
    }

    public ChangePermissionRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new ChangePermissionRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(session(), uri(), Permission.P_SYSTEM));
    }

    public List<String> notificationLocations() {
        return Arrays.asList(uri().withoutFragment().asReverseDNSString(), uri().asReverseDNSString());
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_CHANGE_PERMISSION;
    }

    public boolean isMutationRequest() {
        return true;
    }
}