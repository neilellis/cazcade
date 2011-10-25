package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class RetrieveSessionRequest extends AbstractRetrievalRequest {

    public RetrieveSessionRequest() {
    }

    public RetrieveSessionRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public RetrieveSessionRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveSessionRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target) {
        this.id = id;
        this.identity = identity;
        this.target = target;
    }


    @Override
    public LiquidMessage copy() {
        return new RetrieveSessionRequest(id, identity, target);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(target, LiquidPermission.VIEW));
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_SESSION;
    }

}