package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

public class DeletePoolRequest extends AbstractDeletionRequest {

    public DeletePoolRequest() {
    }

    public DeletePoolRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public DeletePoolRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public DeletePoolRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target) {
        this.id = id;
        this.identity = identity;
        this.target = target;
    }

    public DeletePoolRequest(LiquidSessionIdentifier identity, LiquidURI poolURI) {
        this.identity = identity;
        this.uri = poolURI;
    }

    public DeletePoolRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri) {
        this.id = id;
        this.identity = identity;
        this.target = target;
        this.uri= uri;
    }


    @Override
    public LiquidMessage copy() {
        return new DeletePoolRequest(id, identity, target, uri);
    }

    @Override
    public LSDEntity getEntity() {
        return null;
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_POOL;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}
