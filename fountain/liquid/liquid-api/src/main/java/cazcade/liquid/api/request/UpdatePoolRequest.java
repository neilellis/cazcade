package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdatePoolRequest extends AbstractUpdateRequest {

    public UpdatePoolRequest() {
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdatePoolRequest(LiquidUUID target, LSDEntity entity) {
        this(null, null, target, entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdatePoolRequest(LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity) {
        this(null, identity, target, entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdatePoolRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
        this.setRequestEntity(entity);
    }

    public UpdatePoolRequest(@Nullable LiquidSessionIdentifier identity, LiquidURI poolURI, LSDEntity newEntity) {
        this.setSessionId(identity);
        this.setUri(poolURI);
        this.setRequestEntity(newEntity);
    }

    protected UpdatePoolRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri, LSDEntity entity) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
        this.setUri(uri);
        this.setRequestEntity(entity);
    }

    public UpdatePoolRequest(@Nonnull LSDEntity updateEntity) {
        this(null, updateEntity.getURI(), updateEntity);
    }


    @Nullable
    @Override
    public LiquidMessage copy() {
        return new UpdatePoolRequest(getId(), getSessionIdentifier(), super.getTarget(), getUri(), super.getRequestEntity());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_POOL;
    }

}
