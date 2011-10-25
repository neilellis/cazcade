package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;

public class UpdatePoolRequest extends AbstractUpdateRequest {

    public UpdatePoolRequest() {
    }

    /**
     * @deprecated  use URIs where possible.
     */
    public UpdatePoolRequest(LiquidUUID target, LSDEntity entity) {
        this(null, null, target, entity);
    }

    /**
     * @deprecated  use URIs where possible.
     */
    public UpdatePoolRequest(LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity) {
        this(null, identity, target, entity);
    }

    /**
     * @deprecated  use URIs where possible.
     */
    public UpdatePoolRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity) {
        this.id = id;
        this.identity = identity;
        this.target = target;
        this.entity = entity;
    }

    public UpdatePoolRequest(LiquidSessionIdentifier identity, LiquidURI poolURI, LSDEntity newEntity) {
        this.identity = identity;
        this.uri = poolURI;
        this.entity = newEntity;
    }

    protected UpdatePoolRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri, LSDEntity entity) {
        this.id = id;
        this.identity = identity;
        this.target = target;
        this.uri = uri;
        this.entity = entity;
    }

    public UpdatePoolRequest(LSDEntity updateEntity) {
        this(null, updateEntity.getURI(), updateEntity);
    }


    @Override
    public LiquidMessage copy() {
        return new UpdatePoolRequest(id, identity, target, uri, entity);
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_POOL;
    }

}
