package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UnlinkAliasRequest extends AbstractDeletionRequest {
    public UnlinkAliasRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public UnlinkAliasRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidUUID target) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
    }

    public UnlinkAliasRequest(final LiquidUUID target) {
        this(null, LiquidSessionIdentifier.ANON, target);
    }

    public UnlinkAliasRequest() {
        super();
    }

    public UnlinkAliasRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new UnlinkAliasRequest(getEntity());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_ALIAS;
    }
}