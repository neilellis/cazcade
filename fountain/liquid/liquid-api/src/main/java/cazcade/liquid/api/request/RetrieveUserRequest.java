package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

public class RetrieveUserRequest extends AbstractRetrievalRequest {


    public RetrieveUserRequest() {
    }

    public RetrieveUserRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public RetrieveUserRequest(LiquidURI uri) {
        this(null, null, uri, false);
    }

    public RetrieveUserRequest(LiquidSessionIdentifier identity,LiquidURI uri) {
        this(null, identity, uri, false);
    }

    public RetrieveUserRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveUserRequest(LiquidSessionIdentifier identity, LiquidURI uri, boolean internal) {
        this(null, identity, uri, internal);
    }

    public RetrieveUserRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target) {
        this.id = id;
        this.identity = identity;
        this.target = target;
    }

    public RetrieveUserRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI uri, boolean internal) {
        this.id = id;
        this.identity = identity;
        this.uri = uri;
        this.internal = internal;
    }

    private RetrieveUserRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri) {
        this.target = target;
        this.id = id;
        this.identity = identity;
        this.uri = uri;
    }

    @Override
    public LiquidMessage copy() {
        return new RetrieveUserRequest(id, identity, target, uri);
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_USER;
    }
}
