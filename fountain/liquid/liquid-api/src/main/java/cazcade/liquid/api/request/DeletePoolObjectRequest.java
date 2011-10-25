package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class DeletePoolObjectRequest extends AbstractDeletionRequest {
    private LiquidUUID pool;

    public DeletePoolObjectRequest() {
    }


    public DeletePoolObjectRequest(LiquidURI uri) {
        this(null, null, null, null, uri);
    }

    public DeletePoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID pool, LiquidUUID target) {
        this(null, identity, pool, target, null);
    }

    public DeletePoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID pool, LiquidUUID target, LiquidURI uri) {
        this.id = id;
        this.identity = identity;
        this.pool = pool;
        this.target = target;
        this.uri = uri;
    }


    @Override
    public LiquidMessage copy() {
        return new DeletePoolObjectRequest(id, identity, pool, target, uri);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (uri != null) {
            return Arrays.asList(new AuthorizationRequest(uri, LiquidPermission.DELETE).or(new AuthorizationRequest(uri.getWithoutFragment(), LiquidPermission.EDIT)), new AuthorizationRequest(uri.getWithoutFragment(), LiquidPermission.MODIFY));
        } else {
            return Arrays.asList(new AuthorizationRequest(target, LiquidPermission.DELETE).or(new AuthorizationRequest(pool, LiquidPermission.EDIT)), new AuthorizationRequest(pool, LiquidPermission.MODIFY));
        }
    }


    public List<String> getNotificationLocations() {
        if (uri != null) {
            return Arrays.asList(uri.asReverseDNSString(), uri.getWithoutFragment().asReverseDNSString());
        } else {
            return Arrays.asList(pool.toString(), target.toString());
        }
    }

    public LiquidUUID getPoolId() {
        return pool;
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_POOL_OBJECT;
    }


}
