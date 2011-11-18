package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class ResizePoolObjectRequest extends AbstractRequest {

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
        this.setWidth(width);
        this.setHeight(height);
        this.setId(id);
        this.setIdentity(identity);
        this.setObjectUUID(object);
        this.setPoolUUID(pool);
        this.setUri(objectURI);
    }


    @Override
    public LiquidMessage copy() {
        return new ResizePoolObjectRequest(getId(), getSessionIdentifier(), getUri(), getPoolUUID(), super.getObjectUUID(), super.getWidth(), super.getHeight());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri() == null) {
            return Arrays.asList(new AuthorizationRequest(getPoolUUID(), LiquidPermission.MODIFY));
        } else {
            return Arrays.asList(new AuthorizationRequest(getUri().getParentURI(), LiquidPermission.MODIFY));
        }
    }


    public List<String> getNotificationLocations() {
        if (getUri() == null) {
            return Arrays.asList(getPoolUUID().toString(), super.getObjectUUID().toString());
        } else {
            return Arrays.asList(getUri().getParentURI().asReverseDNSString(), getUri().asReverseDNSString());
        }
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RESIZE_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }

}