package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class UpdateSessionRequest extends AbstractUpdateRequest {
    public UpdateSessionRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity,
                                final LiquidUUID target, final LSDTransferEntity entity, final boolean internal) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setRequestEntity(entity);
        setInternal(internal);
    }

    public UpdateSessionRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDTransferEntity entity,
                                final boolean internal) {
        this(null, identity, target, entity, internal);
    }



    public UpdateSessionRequest() {
        super();
    }

    public UpdateSessionRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new UpdateSessionRequest(getEntity());
    }

    @Nullable
    @Override
    public List<String> getNotificationLocations() {
        return null;
    }

    @Nullable
    @Override
    public String getNotificationSession() {
        //Don't notify anyone of a session update.
        return null;
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_SESSION;
    }
}