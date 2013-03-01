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

public class RotateXYPoolObjectRequest extends AbstractRequest {
    public RotateXYPoolObjectRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LURI objectURI, final LiquidUUID poolId, final LiquidUUID object, final Double angle) {
        super();
        id(id);
        session(identity);
        setObjectUUID(object);
        setPoolUUID(poolId);
        setAngle(angle);
        setUri(objectURI);
    }

    public RotateXYPoolObjectRequest(final SessionIdentifier identity, final LiquidUUID poolId, final LiquidUUID object, final Double angle, final LURI objectURI) {
        this(null, identity, objectURI, poolId, object, angle);
    }


    public RotateXYPoolObjectRequest() {
        super();
    }

    public RotateXYPoolObjectRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RotateXYPoolObjectRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(session(), getPoolUUID(), Permission.P_MODIFY));
    }

    public List<String> notificationLocations() {
        return Arrays.asList(getPoolUUID().toString());
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_ROTATE_XY_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }
}