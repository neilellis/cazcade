package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class MovePoolObjectRequest extends AbstractRequest {

    public MovePoolObjectRequest() {
        super();
    }

    public MovePoolObjectRequest(final LiquidURI objectURI, final Double x, final Double y, final Double z) {
        this(null, null, objectURI, null, null, x, y, z);
    }

    @Deprecated
    public MovePoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidURI objectURI, final LiquidUUID pool, final LiquidUUID object, final Double x, final Double y, final Double z) {
        super();
        throw new UnsupportedOperationException();
//        this(null, identity, objectURI, pool, object, x, y, z);
    }

    public MovePoolObjectRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidURI objectURI, @Nullable final LiquidUUID pool, @Nullable final LiquidUUID object, final Double x, final Double y, final Double z) {
        super();
        setId(id);
        setSessionId(identity);
        setObjectUUID(object);
        setPoolUUID(pool);
        setX(x);
        setY(y);
        setZ(z);
        setUri(objectURI);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new MovePoolObjectRequest(getId(), getSessionIdentifier(), getUri(), getPoolUUID(), getObjectUUID(), getX(), getY(), getZ());
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
            return Arrays.asList(getUri().getWithoutFragment().asReverseDNSString(), getUri().asReverseDNSString());
        }
    }

    @Nonnull
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