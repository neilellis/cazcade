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

public class SelectPoolObjectRequest extends AbstractUpdateRequest {
    public SelectPoolObjectRequest(@Nullable final LiquidUUID id, final SessionIdentifier identity, final LiquidUUID target, final boolean selected) {
        super();
        id(id);
        session(identity);
        setTarget(target);
        setSelected(selected);
    }

    public SelectPoolObjectRequest(final SessionIdentifier identity, final LiquidUUID target, final boolean selected) {
        this(null, identity, target, selected);
    }

    public SelectPoolObjectRequest() {
        super();
    }

    public SelectPoolObjectRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new SelectPoolObjectRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(session(), getTarget(), Permission.P_MODIFY));
    }

    @Nullable
    public List<String> notificationLocations() {
        return null;
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_SELECT_POOL_OBJECT;
    }
}
