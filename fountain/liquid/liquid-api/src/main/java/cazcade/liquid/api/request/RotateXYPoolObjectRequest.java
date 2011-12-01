package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class RotateXYPoolObjectRequest extends AbstractRequest {

    public RotateXYPoolObjectRequest() {
    }

    public RotateXYPoolObjectRequest(LiquidUUID poolId, LiquidUUID object, Double angle, LiquidURI objectURI) {
        this(null, null, objectURI, poolId, object, angle);
    }

    public RotateXYPoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID poolId, LiquidUUID object, Double angle, LiquidURI objectURI) {
        this(null, identity, objectURI, poolId, object, angle);
    }

    public RotateXYPoolObjectRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidURI objectURI, LiquidUUID poolId, LiquidUUID object, Double angle) {
        this.setId(id);
        this.setSessionId(identity);
        this.setObjectUUID(object);
        this.setPoolUUID(poolId);
        this.setAngle(angle);
        this.setUri(objectURI);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RotateXYPoolObjectRequest(getId(), getSessionIdentifier(), getUri(), super.getPoolUUID(), super.getObjectUUID(), super.getAngle());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(super.getPoolUUID(), LiquidPermission.MODIFY));
    }


    public List<String> getNotificationLocations() {
        return Arrays.asList(super.getPoolUUID().toString());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.ROTATE_XY_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }


}