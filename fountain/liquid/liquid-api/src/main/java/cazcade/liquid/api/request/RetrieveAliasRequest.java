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
        this.identity = identity;
    }

    public RetrieveAliasRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveAliasRequest(LiquidSessionIdentifier identity, LiquidURI uri) {
        this(null, identity, uri);
    }

    public RetrieveAliasRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target) {
        this.id = id;
        this.identity = identity;
        this.target = target;
    }

    public RetrieveAliasRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI uri) {
        this.id = id;
        this.identity = identity;
        this.uri = uri;
    }

    private RetrieveAliasRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri) {
        this.target = target;
        this.id = id;
        this.identity = identity;
        this.uri = uri;
    }


    @Override
    public LiquidMessage copy() {
        return new RetrieveAliasRequest(id, identity, target, uri);
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_ALIAS;
    }


    @Override
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.emptyList();
    }
}