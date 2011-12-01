package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeletePoolRequest extends AbstractDeletionRequest {

    public DeletePoolRequest() {
    }

    public DeletePoolRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public DeletePoolRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public DeletePoolRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidUUID target) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
    }

    public DeletePoolRequest(LiquidSessionIdentifier identity, LiquidURI poolURI) {
        this.setSessionId(identity);
        this.setUri(poolURI);
    }

    public DeletePoolRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
        this.setUri(uri);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new DeletePoolRequest(getId(), getSessionIdentifier(), super.getTarget(), super.getUri());
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_POOL;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}
