package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class DeletePoolObjectRequest extends AbstractDeletionRequest {

    public DeletePoolObjectRequest() {
    }


    public DeletePoolObjectRequest(LiquidURI uri) {
        this(null, null, null, null, uri);
    }

    public DeletePoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID pool, LiquidUUID target) {
        this(null, identity, pool, target, null);
    }

    public DeletePoolObjectRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, @Nullable LiquidUUID pool, @Nullable LiquidUUID target, @Nullable LiquidURI uri) {
        this.setId(id);
        this.setSessionId(identity);
        this.setPoolUUID(pool);
        this.setTarget(target);
        this.setUri(uri);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new DeletePoolObjectRequest(getId(), getSessionIdentifier(), getPoolUUID(), super.getTarget(), super.getUri());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (super.getUri() != null) {
            return Arrays.asList(new AuthorizationRequest(super.getUri(), LiquidPermission.DELETE).or(new AuthorizationRequest(super.getUri().getWithoutFragment(), LiquidPermission.EDIT)), new AuthorizationRequest(super.getUri().getWithoutFragment(), LiquidPermission.MODIFY));
        } else {
            return Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.DELETE).or(new AuthorizationRequest(getPoolUUID(), LiquidPermission.EDIT)), new AuthorizationRequest(getPoolUUID(), LiquidPermission.MODIFY));
        }
    }


    public List<String> getNotificationLocations() {
        if (super.getUri() != null) {
            return Arrays.asList(super.getUri().asReverseDNSString(), super.getUri().getWithoutFragment().asReverseDNSString());
        } else {
            return Arrays.asList(getPoolUUID().toString(), super.getTarget().toString());
        }
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_POOL_OBJECT;
    }


}
