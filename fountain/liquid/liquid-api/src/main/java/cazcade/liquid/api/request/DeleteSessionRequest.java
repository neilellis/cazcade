package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeleteSessionRequest extends AbstractDeletionRequest {
    public DeleteSessionRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity,
                                final LiquidUUID target) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
    }

    public DeleteSessionRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public DeleteSessionRequest() {
        super();
    }

    public DeleteSessionRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new DeleteSessionRequest(getEntity());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_SESSION;
    }
}