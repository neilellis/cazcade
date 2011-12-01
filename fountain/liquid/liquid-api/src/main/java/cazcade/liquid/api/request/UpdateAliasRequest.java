package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateAliasRequest extends AbstractUpdateRequest {

    public UpdateAliasRequest() {
        super();
    }

    public UpdateAliasRequest(@Nonnull final LSDEntity entity) {
        this(null, entity.getURI(), entity);
    }

    /**
     * @deprecated use URIs where possible.
     */

    public UpdateAliasRequest(final LiquidUUID target, final LSDEntity entity) {
        this(null, null, target, entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdateAliasRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDEntity entity) {
        this(null, identity, target, entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdateAliasRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setRequestEntity(entity);
    }

    public UpdateAliasRequest(@Nullable final LiquidSessionIdentifier identity, final LiquidURI uri, final LSDEntity newEntity) {
        super();
        setUri(uri);
        setSessionId(identity);
        setRequestEntity(newEntity);
    }

    public UpdateAliasRequest(final LiquidURI uri, final LSDEntity newEntity) {
        super();
        setUri(uri);
        setRequestEntity(newEntity);
    }

    public UpdateAliasRequest(@Nullable final LiquidUUID id, final LiquidSessionIdentifier identity, @Nullable final LiquidUUID target, final LiquidURI uri, final LSDEntity entity) {
        super();
        setUri(uri);
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setRequestEntity(entity);
    }

    public UpdateAliasRequest(final LiquidSessionIdentifier sessionIdentifier, @Nonnull final LSDSimpleEntity alias) {
        this(null, sessionIdentifier, null, alias.getURI(), alias);
    }


    @Nullable
    @Override
    public LiquidMessage copy() {
        return new UpdateAliasRequest(getId(), getSessionIdentifier(), getTarget(), getUri(), getRequestEntity());
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