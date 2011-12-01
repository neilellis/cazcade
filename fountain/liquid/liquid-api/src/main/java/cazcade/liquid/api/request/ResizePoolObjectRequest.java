package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ResizePoolObjectRequest extends AbstractRequest {

    public ResizePoolObjectRequest() {
        super();
    }

    public ResizePoolObjectRequest(final LiquidUUID pool, final LiquidUUID object, final Integer width, final Integer height, final LiquidURI objectURI) {
        this(null, null, objectURI, pool, object, width, height);
    }

    public ResizePoolObjectRequest(final LiquidURI objectURI, final Integer width, final Integer height) {
        this(null, null, objectURI, null, null, width, height);
    }

    public ResizePoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID pool, final LiquidUUID object, final Integer width, final Integer height, final LiquidURI objectURI) {
        this(null, identity, objectURI, pool, object, width, height);
    }

    public ResizePoolObjectRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidURI objectURI, @Nullable final LiquidUUID pool, @Nullable final LiquidUUID object, final Integer width, final Integer height) {
        super();
        setWidth(width);
        setHeight(height);
        setId(id);
        setSessionId(identity);
        setObjectUUID(object);
        setPoolUUID(pool);
        setUri(objectURI);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new ResizePoolObjectRequest(getId(), getSessionIdentifier(), getUri(), getPoolUUID(), getObjectUUID(), getWidth(), getHeight());
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
            return Arrays.asList(getPoolUUID().toString(), getObjectUUID().toString());
        } else {
            return Arrays.asList(getUri().getParentURI().asReverseDNSString(), getUri().asReverseDNSString());
        }
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RESIZE_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }

}