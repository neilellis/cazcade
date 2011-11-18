package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

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
        this.setId(id);
        this.setIdentity(identity);
        this.setTarget(target);
    }

    public DeletePoolRequest(LiquidSessionIdentifier identity, LiquidURI poolURI) {
        this.setIdentity(identity);
        this.setUri(poolURI);
    }

    public DeletePoolRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri) {
        this.setId(id);
        this.setIdentity(identity);
        this.setTarget(target);
        this.setUri(uri);
    }


    @Override
    public LiquidMessage copy() {
        return new DeletePoolRequest(getId(), getSessionIdentifier(), super.getTarget(), super.getUri());
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_POOL;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}
