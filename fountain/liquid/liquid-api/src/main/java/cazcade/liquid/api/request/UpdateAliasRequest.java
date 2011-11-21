package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;

public class UpdateAliasRequest extends AbstractUpdateRequest {

    public UpdateAliasRequest() {
    }

    public UpdateAliasRequest(LSDEntity entity) {
        this(null, entity.getURI(), entity);
    }

    /**
     * @deprecated use URIs where possible.
     */

    public UpdateAliasRequest(LiquidUUID target, LSDEntity entity) {
        this(null, null, target, entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdateAliasRequest(LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity) {
        this(null, identity, target, entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdateAliasRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity) {
        this.setId(id);
        this.setIdentity(identity);
        this.setTarget(target);
        this.setRequestEntity(entity);
    }

    public UpdateAliasRequest(LiquidSessionIdentifier identity, LiquidURI uri, LSDEntity newEntity) {
        this.setUri(uri);
        this.setIdentity(identity);
        this.setRequestEntity(newEntity);
    }

    public UpdateAliasRequest(LiquidURI uri, LSDEntity newEntity) {
        this.setUri(uri);
        this.setRequestEntity(newEntity);
    }

    public UpdateAliasRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidURI uri, LSDEntity entity) {
        this.setUri(uri);
        this.setId(id);
        this.setIdentity(identity);
        this.setTarget(target);
        this.setRequestEntity(entity);
    }

    public UpdateAliasRequest(LiquidSessionIdentifier sessionIdentifier, LSDSimpleEntity alias) {
        this(null, sessionIdentifier, null, alias.getURI(), alias);
    }


    @Override
    public LiquidMessage copy() {
        return new UpdateAliasRequest(getId(), getSessionIdentifier(), super.getTarget(), getUri(), super.getRequestEntity());
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_ALIAS;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}