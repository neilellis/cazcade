package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;

public class DeleteSessionRequest extends AbstractDeletionRequest {

    public DeleteSessionRequest() {
    }

    public DeleteSessionRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public DeleteSessionRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public DeleteSessionRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target) {
        this.setId(id);
        this.setIdentity(identity);
        this.setTarget(target);
    }


    @Override
    public LiquidMessage copy() {
        return new DeleteSessionRequest(getId(), getSessionIdentifier(), super.getTarget());
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_SESSION;
    }
}