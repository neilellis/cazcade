package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.Arrays;
import java.util.List;

public class ResizePoolObjectRequest extends AbstractRequest {
    private LiquidUUID object;
    private LiquidUUID pool;
    private Integer width;
    private Integer height;

    public ResizePoolObjectRequest() {
    }

    public ResizePoolObjectRequest(LiquidUUID pool, LiquidUUID object, Integer width, Integer height, LiquidURI objectURI) {
        this(null, null, objectURI, pool, object, width, height);
    }

    public ResizePoolObjectRequest(LiquidURI objectURI, Integer width, Integer height) {
        this(null, null, objectURI, null, null, width, height);
    }

    public ResizePoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID pool, LiquidUUID object, Integer width, Integer height, LiquidURI objectURI) {
        this(null, identity, objectURI, pool, object, width, height);
    }

    public ResizePoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI objectURI, LiquidUUID pool, LiquidUUID object, Integer width, Integer height) {
        this.width = width;
        this.height = height;
        this.id = id;
        this.identity = identity;
        this.object = object;
        this.pool = pool;
        this.uri = objectURI;
    }



    public Integer getWidth() {
        return width;
    }

    public LiquidUUID getObject() {
        return object;
    }




    @Override
    public LiquidMessage copy() {
        return new ResizePoolObjectRequest(id, identity, uri, pool, object, width, height);
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
            return Arrays.asList(uri.getParentURI().asReverseDNSString(), uri.asReverseDNSString());
        }
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RESIZE_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }


    public Integer getHeight() {
        return height;
    }
}