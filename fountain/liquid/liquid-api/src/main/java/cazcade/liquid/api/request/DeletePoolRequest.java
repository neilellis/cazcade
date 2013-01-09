package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeletePoolRequest extends AbstractDeletionRequest {
    public DeletePoolRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target,
                             final LiquidURI uri) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setUri(uri);
    }

    public DeletePoolRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity,
                             final LiquidUUID target) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
    }

    public DeletePoolRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public DeletePoolRequest(final LiquidSessionIdentifier identity, final LiquidURI poolURI) {
        super();
        setSessionId(identity);
        setUri(poolURI);
    }



    public DeletePoolRequest() {
        super();
    }

    public DeletePoolRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new DeletePoolRequest(getEntity());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.DELETE_POOL;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}
