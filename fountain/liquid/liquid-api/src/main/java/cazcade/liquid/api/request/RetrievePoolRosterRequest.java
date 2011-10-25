package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class RetrievePoolRosterRequest extends AbstractRetrievalRequest {

    public RetrievePoolRosterRequest() {
    }

    public RetrievePoolRosterRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public RetrievePoolRosterRequest(LiquidURI uri) {
        this(null, null, uri);
    }

    public RetrievePoolRosterRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrievePoolRosterRequest(LiquidSessionIdentifier identity, LiquidURI uri) {
        this(null, identity, uri);
    }

    public RetrievePoolRosterRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target) {
        this.target = target;
        this.id = id;
        this.identity = identity;
    }

    public RetrievePoolRosterRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI uri) {
        this.id = id;
        this.identity = identity;
        this.uri = uri;
    }

    private RetrievePoolRosterRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri) {
        this.target = target;
        this.id = id;
        this.identity = identity;
        this.uri = uri;
    }


    @Override
    public LiquidMessage copy() {
        return new RetrievePoolRosterRequest(id, identity, target, uri);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (target != null) {
            return Arrays.asList(new AuthorizationRequest(target, LiquidPermission.VIEW));
        } else {
            return Arrays.asList(new AuthorizationRequest(uri, LiquidPermission.VIEW));
        }
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_POOL_ROSTER;
    }


}
