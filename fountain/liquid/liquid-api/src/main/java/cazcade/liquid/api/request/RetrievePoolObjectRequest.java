package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RetrievePoolObjectRequest extends AbstractRetrievalRequest {

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
        this.setId(id);
        this.setSessionId(identity);
        this.setUri(uri);
        this.setHistorical(historical);
    }

    public RetrievePoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID pool, LiquidUUID target, boolean historical) {
        this(null, identity, pool, target, historical);
    }

    public RetrievePoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier authenticatedUser, LiquidUUID pool, LiquidUUID target, boolean historical) {
        this.setSessionId(authenticatedUser);
        this.setPoolUUID(pool);
        this.setTarget(target);
        this.setHistorical(historical);
    }

    private RetrievePoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier authenticatedUser, LiquidUUID pool, LiquidUUID target, LiquidURI uri) {
        this.setId(id);
        this.setSessionId(authenticatedUser);
        this.setPoolUUID(pool);
        this.setTarget(target);
        this.setUri(uri);
    }


    @Override
    public LiquidMessage copy() {
        return new RetrievePoolObjectRequest(getId(), getSessionIdentifier(), getPoolUUID(), super.getTarget(), getUri());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getPoolUUID() != null) {
            return Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.VIEW), new AuthorizationRequest(getPoolUUID(), LiquidPermission.VIEW));
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_POOL_OBJECT;
    }

}
