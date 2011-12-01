package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeleteUserRequest extends AbstractDeletionRequest {

    public DeleteUserRequest() {
        super();
    }

    public DeleteUserRequest(final LiquidUUID target) {
        this(null, null, target);
    }

    public DeleteUserRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public DeleteUserRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidUUID target) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new DeleteUserRequest(getId(), getSessionIdentifier(), getTarget());
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
