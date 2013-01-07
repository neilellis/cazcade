package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class ResizePoolObjectRequest extends AbstractRequest {
    public ResizePoolObjectRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity,
                                   final LiquidURI objectURI, @Nullable final LiquidUUID pool, @Nullable final LiquidUUID object,
                                   final Integer width, final Integer height) {
        super();
        setWidth(width);
        setHeight(height);
        setId(id);
        setSessionId(identity);
        setObjectUUID(object);
        setPoolUUID(pool);
        setUri(objectURI);
    }

    public ResizePoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID pool, final LiquidUUID object,
                                   final Integer width, final Integer height, final LiquidURI objectURI) {
        this(null, identity, objectURI, pool, object, width, height);
    }



    public ResizePoolObjectRequest(final LiquidURI objectURI, final Integer width, final Integer height) {
        this(null, LiquidSessionIdentifier.ANON, objectURI, null, null, width, height);
    }

    public ResizePoolObjectRequest() {
        super();
    }

    public ResizePoolObjectRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new ResizePoolObjectRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (!hasUri()) {
            return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getPoolUUID(), LiquidPermission.MODIFY));
        }
        else {
            return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getUri().getParentURI(), LiquidPermission.MODIFY));
        }
    }

    public List<String> getNotificationLocations() {
        if (!hasUri()) {
            return Arrays.asList(getPoolUUID().toString(), getObjectUUID().toString());
        }
        else {
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