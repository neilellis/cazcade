package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.Arrays;
import java.util.List;

public class RotateXYPoolObjectRequest extends AbstractRequest {
    private LiquidUUID object;
    private LiquidUUID pool;
    private Double angle;

    public RotateXYPoolObjectRequest() {
    }

    public RotateXYPoolObjectRequest(LiquidUUID poolId, LiquidUUID object, Double angle, LiquidURI objectURI) {
        this(null, null, objectURI, poolId, object, angle);
    }

    public RotateXYPoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID poolId, LiquidUUID object, Double angle, LiquidURI objectURI) {
        this(null, identity, objectURI, poolId, object, angle);
    }

    public RotateXYPoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI objectURI, LiquidUUID poolId, LiquidUUID object, Double angle) {
        this.id = id;
        this.identity = identity;
        this.object = object;
        this.pool = poolId;
        this.angle = angle;
        this.uri= objectURI;
    }



    public Double getAngle() {
        return angle;
    }

    public LiquidUUID getObject() {
        return object;
    }

    public LiquidUUID getPool() {
        return pool;
    }

    @Override
    public LiquidMessage copy() {
        return new RotateXYPoolObjectRequest(id, identity, uri, pool, object, angle);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(pool, LiquidPermission.MODIFY));
    }

    public LSDEntity getEntity() {
        return null;
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList(pool.toString());
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.ROTATE_XY_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }


}