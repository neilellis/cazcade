package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.Arrays;
import java.util.List;

public class MovePoolObjectRequest extends AbstractRequest {
    private LiquidUUID object;
    private LiquidUUID pool;
    private Double x;
    private Double y;
    private Double z;

    public MovePoolObjectRequest() {
    }

    public MovePoolObjectRequest(LiquidURI objectURI, Double x, Double y, Double z) {
        this(null, null, objectURI, null, null, x, y, z);
    }

    @Deprecated
    public MovePoolObjectRequest(LiquidSessionIdentifier identity, LiquidURI objectURI, LiquidUUID pool, LiquidUUID object, Double x, Double y, Double z) {
        throw new UnsupportedOperationException();
//        this(null, identity, objectURI, pool, object, x, y, z);
    }

    public MovePoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI objectURI, LiquidUUID pool, LiquidUUID object, Double x, Double y, Double z) {
        this.id = id;
        this.identity = identity;
        this.object = object;
        this.pool = pool;
        this.x = x;
        this.y = y;
        this.z = z;
        this.uri = objectURI;
    }


    public LiquidUUID getObject() {
        return object;
    }

    public LiquidUUID getPool() {
        return pool;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }

    @Override
    public LiquidMessage copy() {
        return new MovePoolObjectRequest(id, identity, uri, pool, object, x, y, z);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (uri == null) {
            return Arrays.asList(new AuthorizationRequest(pool, LiquidPermission.MODIFY));
        } else {
            return Arrays.asList(new AuthorizationRequest(uri.getParentURI(), LiquidPermission.MODIFY));
        }
    }

    public LSDEntity getEntity() {
        return null;
    }

    public List<String> getNotificationLocations() {
        if (uri == null) {
            return Arrays.asList(pool.toString(), object.toString());
        } else {
            return Arrays.asList(uri.getWithoutFragment().asReverseDNSString(), uri.asReverseDNSString());
        }
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.MOVE_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }

    @Override
    public boolean shouldSendProvisional() {
        return true;
    }
}