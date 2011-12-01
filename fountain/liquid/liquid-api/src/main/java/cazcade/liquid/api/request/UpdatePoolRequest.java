package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdatePoolRequest extends AbstractUpdateRequest {

    public UpdatePoolRequest() {
        super();
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdatePoolRequest(final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, null, target, entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdatePoolRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, identity, target, entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdatePoolRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDTransferEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setRequestEntity(entity);
    }

    public UpdatePoolRequest(@Nullable final LiquidSessionIdentifier identity, final LiquidURI poolURI, final LSDTransferEntity newEntity) {
        super();
        setSessionId(identity);
        setUri(poolURI);
        setRequestEntity(newEntity);
    }

    protected UpdatePoolRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidURI uri, final LSDTransferEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setUri(uri);
        setRequestEntity(entity);
    }

    public UpdatePoolRequest(@Nonnull final LSDTransferEntity updateEntity) {
        this(null, updateEntity.getURI(), updateEntity);
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
