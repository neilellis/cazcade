package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeleteSessionRequest extends AbstractDeletionRequest {

    public DeleteSessionRequest() {
    }

    public DeleteSessionRequest(LiquidUUID target) {
        this(null, null, target);
    }

    public DeleteSessionRequest(LiquidSessionIdentifier identity, LiquidUUID target) {
        this(null, identity, target);
    }

    public DeleteSessionRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidUUID target) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new DeleteSessionRequest(getId(), getSessionIdentifier(), super.getTarget());
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_SESSION;
    }
}