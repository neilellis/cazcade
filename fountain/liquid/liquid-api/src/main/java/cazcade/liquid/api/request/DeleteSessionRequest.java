package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeleteSessionRequest extends AbstractDeletionRequest {

    public DeleteSessionRequest() {
        super();
    }

    public DeleteSessionRequest(final LiquidUUID target) {
        this(null, null, target);
    }

    public DeleteSessionRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public DeleteSessionRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidUUID target) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new DeleteSessionRequest(getId(), getSessionIdentifier(), getTarget());
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_SESSION;
    }
}