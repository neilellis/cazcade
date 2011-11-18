package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Collections;
import java.util.List;

public class RetrieveAliasRequest extends AbstractRetrievalRequest {

    public RetrieveAliasRequest() {
    }

    public RetrieveAliasRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public RetrieveAliasRequest(LiquidURI uri) {
        this(null, null, uri);
    }

    public RetrieveAliasRequest(LiquidSessionIdentifier identity) {
        this.setIdentity(identity);
    }

    public RetrieveAliasRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveAliasRequest(LiquidSessionIdentifier identity, LiquidURI uri) {
        this(null, identity, uri);
    }

    public RetrieveAliasRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target) {
        this.setId(id);
        this.setIdentity(identity);
        this.setTarget(target);
    }

    public RetrieveAliasRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI uri) {
        this.setId(id);
        this.setIdentity(identity);
        this.setUri(uri);
    }

    private RetrieveAliasRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri) {
        this.setTarget(target);
        this.setId(id);
        this.setIdentity(identity);
        this.setUri(uri);
    }


    @Override
    public LiquidMessage copy() {
        return new RetrieveAliasRequest(getId(), getSessionIdentifier(), super.getTarget(), getUri());
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_ALIAS;
    }


    @Override
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.emptyList();
    }
}