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

public class SelectPoolObjectRequest extends AbstractUpdateRequest {
    public SelectPoolObjectRequest(@Nullable final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target, final boolean selected) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setSelected(selected);
    }

    public SelectPoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final boolean selected) {
        this(null, identity, target, selected);
    }

    public SelectPoolObjectRequest() {
        super();
    }

    public SelectPoolObjectRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new SelectPoolObjectRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getTarget(), LiquidPermission.MODIFY));
    }

    @Nullable
    public List<String> getNotificationLocations() {
        return null;
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.SELECT_POOL_OBJECT;
    }
}
