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
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
    }


    @Override
    public LiquidMessage copy() {
        return new RetrieveSessionRequest(getId(), getSessionIdentifier(), super.getTarget());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(super.getTarget(), LiquidPermission.VIEW));
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_SESSION;
    }

}