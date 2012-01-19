package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdatePoolRequest extends AbstractUpdateRequest {
    protected UpdatePoolRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target,
                                final LiquidURI uri, final LSDTransferEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setUri(uri);
        setRequestEntity(entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdatePoolRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity,
                             final LiquidUUID target, final LSDTransferEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setRequestEntity(entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdatePoolRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, identity, target, entity);
    }

    public UpdatePoolRequest(@Nullable final LiquidSessionIdentifier identity, final LiquidURI poolURI,
                             final LSDTransferEntity newEntity) {
        super();
        setSessionId(identity);
        setUri(poolURI);
        setRequestEntity(newEntity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdatePoolRequest(final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, null, target, entity);
    }

    public UpdatePoolRequest(@Nonnull final LSDTransferEntity updateEntity) {
        this(null, updateEntity.getURI(), updateEntity);
    }

    public UpdatePoolRequest() {
        super();
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new UpdatePoolRequest(getId(), getSessionIdentifier(), getTarget(), getUri(), getRequestEntity());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_POOL;
    }
}
