package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class RotateXYPoolObjectRequest extends AbstractRequest {
    public RotateXYPoolObjectRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity,
                                     final LiquidURI objectURI, final LiquidUUID poolId, final LiquidUUID object,
                                     final Double angle) {
        super();
        setId(id);
        setSessionId(identity);
        setObjectUUID(object);
        setPoolUUID(poolId);
        setAngle(angle);
        setUri(objectURI);
    }

    public RotateXYPoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID poolId, final LiquidUUID object,
                                     final Double angle, final LiquidURI objectURI) {
        this(null, identity, objectURI, poolId, object, angle);
    }


    public RotateXYPoolObjectRequest() {
        super();
    }

    public RotateXYPoolObjectRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RotateXYPoolObjectRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getPoolUUID(), LiquidPermission.MODIFY));
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList(getPoolUUID().toString());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.ROTATE_XY_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }
}