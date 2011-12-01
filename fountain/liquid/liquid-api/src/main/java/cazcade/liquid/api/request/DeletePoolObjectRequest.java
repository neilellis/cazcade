package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class DeletePoolObjectRequest extends AbstractDeletionRequest {

    public DeletePoolObjectRequest() {
        super();
    }


    public DeletePoolObjectRequest(final LiquidURI uri) {
        this(null, null, null, null, uri);
    }

    public DeletePoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID pool, final LiquidUUID target) {
        this(null, identity, pool, target, null);
    }

    public DeletePoolObjectRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, @Nullable final LiquidUUID pool, @Nullable final LiquidUUID target, @Nullable final LiquidURI uri) {
        super();
        setId(id);
        setSessionId(identity);
        setPoolUUID(pool);
        setTarget(target);
        setUri(uri);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new DeletePoolObjectRequest(getId(), getSessionIdentifier(), getPoolUUID(), getTarget(), getUri());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri() != null) {
            return Arrays.asList(new AuthorizationRequest(getUri(), LiquidPermission.DELETE).or(new AuthorizationRequest(getUri().getWithoutFragment(), LiquidPermission.EDIT)), new AuthorizationRequest(getUri().getWithoutFragment(), LiquidPermission.MODIFY));
        } else {
            return Arrays.asList(new AuthorizationRequest(getTarget(), LiquidPermission.DELETE).or(new AuthorizationRequest(getPoolUUID(), LiquidPermission.EDIT)), new AuthorizationRequest(getPoolUUID(), LiquidPermission.MODIFY));
        }
    }


    public List<String> getNotificationLocations() {
        if (getUri() != null) {
            return Arrays.asList(getUri().asReverseDNSString(), getUri().getWithoutFragment().asReverseDNSString());
        } else {
            return Arrays.asList(getPoolUUID().toString(), getTarget().toString());
        }
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_POOL_OBJECT;
    }


}
