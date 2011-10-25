package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
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
        this.id = id;
        this.identity = identity;
        this.target = target;
    }


    @Override
    public LiquidMessage copy() {
        return new DeleteUserRequest(id, identity, target);
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_USER;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}
