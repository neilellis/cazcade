package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RetrievePoolObjectRequest extends AbstractRetrievalRequest {
    private LiquidUUID pool;

    public RetrievePoolObjectRequest() {
    }

    public RetrievePoolObjectRequest(LiquidURI uri, boolean historical) {
        this(null, null, uri, historical);
    }

    public RetrievePoolObjectRequest(LiquidSessionIdentifier identity, LiquidURI uri, boolean historical) {
        this(null, identity, uri, historical);
    }

    public RetrievePoolObjectRequest(LiquidUUID pool, LiquidUUID target, boolean historical) {
        this(null, null, pool, target, historical);
    }

    public RetrievePoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI uri, boolean historical) {
        this.id = id;
        this.identity = identity;
        this.uri = uri;
        this.historical = historical;
    }

    public RetrievePoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID pool, LiquidUUID target, boolean historical) {
        this(null, identity, pool, target, historical);
    }

    public RetrievePoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier authenticatedUser, LiquidUUID pool, LiquidUUID target, boolean historical) {
        this.identity = authenticatedUser;
        this.pool = pool;
        this.target = target;
        this.historical = historical;
    }

    private RetrievePoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier authenticatedUser, LiquidUUID pool, LiquidUUID target, LiquidURI uri) {
        this.id = id;
        this.identity = authenticatedUser;
        this.pool = pool;
        this.target = target;
        this.uri = uri;
    }



    @Override
    public LiquidMessage copy() {
        return new RetrievePoolObjectRequest(id, identity, pool, target, uri);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (pool != null) {
            return Arrays.asList(new AuthorizationRequest(target, LiquidPermission.VIEW), new AuthorizationRequest(pool, LiquidPermission.VIEW));
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_POOL_OBJECT;
    }

}
