package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;

public class DeleteUserRequest extends AbstractDeletionRequest {

    public DeleteUserRequest() {
    }

    public DeleteUserRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public DeleteUserRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public DeleteUserRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target) {
        this.setId(id);
        this.setIdentity(identity);
        this.setTarget(target);
    }


    @Override
    public LiquidMessage copy() {
        return new DeleteUserRequest(getId(), getSessionIdentifier(), super.getTarget());
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_USER;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}
