package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateAliasRequest extends AbstractUpdateRequest {

    public UpdateAliasRequest() {
    }

    public UpdateAliasRequest(@Nonnull LSDEntity entity) {
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
    public UpdateAliasRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, LiquidUUID target, LSDEntity entity) {
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
        this.setRequestEntity(entity);
    }

    public UpdateAliasRequest(@Nullable LiquidSessionIdentifier identity, LiquidURI uri, LSDEntity newEntity) {
        this.setUri(uri);
        this.setSessionId(identity);
        this.setRequestEntity(newEntity);
    }

    public UpdateAliasRequest(LiquidURI uri, LSDEntity newEntity) {
        this.setUri(uri);
        this.setRequestEntity(newEntity);
    }

    public UpdateAliasRequest(@Nullable LiquidUUID id, LiquidSessionIdentifier identity, @Nullable LiquidUUID target, LiquidURI uri, LSDEntity entity) {
        this.setUri(uri);
        this.setId(id);
        this.setSessionId(identity);
        this.setTarget(target);
        this.setRequestEntity(entity);
    }

    public UpdateAliasRequest(LiquidSessionIdentifier sessionIdentifier, @Nonnull LSDSimpleEntity alias) {
        this(null, sessionIdentifier, null, alias.getURI(), alias);
    }


    @Nullable
    @Override
    public LiquidMessage copy() {
        return new UpdateAliasRequest(getId(), getSessionIdentifier(), super.getTarget(), getUri(), super.getRequestEntity());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_ALIAS;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}