package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeleteUserRequest extends AbstractDeletionRequest {

    public DeleteUserRequest() {
    }

    public DeleteUserRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public DeleteUserRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public DeleteUserRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidUUID target) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new DeleteUserRequest(getId(), getSessionIdentifier(), super.getTarget());
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_USER;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}
