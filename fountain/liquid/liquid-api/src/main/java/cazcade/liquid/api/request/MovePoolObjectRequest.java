package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class MovePoolObjectRequest extends AbstractRequest {

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

    public MovePoolObjectRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidURI objectURI, @Nullable LiquidUUID pool, @Nullable LiquidUUID object, Double x, Double y, Double z) {
        this.setId(id);
        this.setSessionId(identity);
        this.setObjectUUID(object);
        this.setPoolUUID(pool);
        this.setX(x);
        this.setY(y);
        this.setZ(z);
        this.setUri(objectURI);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new MovePoolObjectRequest(getId(), getSessionIdentifier(), getUri(), super.getPoolUUID(), super.getObjectUUID(), super.getX(), super.getY(), super.getZ());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri() == null) {
            return Arrays.asList(new AuthorizationRequest(super.getPoolUUID(), LiquidPermission.MODIFY));
        } else {
            return Arrays.asList(new AuthorizationRequest(getUri().getParentURI(), LiquidPermission.MODIFY));
        }
    }


    public List<String> getNotificationLocations() {
        if (getUri() == null) {
            return Arrays.asList(super.getPoolUUID().toString(), super.getObjectUUID().toString());
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