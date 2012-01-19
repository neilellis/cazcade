package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateAliasRequest extends AbstractUpdateRequest {
    public UpdateAliasRequest(@Nullable final LiquidUUID id, final LiquidSessionIdentifier identity,
                              @Nullable final LiquidUUID target, final LiquidURI uri, final LSDTransferEntity entity) {
        super();
        setUri(uri);
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setRequestEntity(entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdateAliasRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity,
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
    public UpdateAliasRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, identity, target, entity);
    }

    public UpdateAliasRequest(@Nullable final LiquidSessionIdentifier identity, final LiquidURI uri,
                              final LSDTransferEntity newEntity) {
        super();
        setUri(uri);
        setSessionId(identity);
        setRequestEntity(newEntity);
    }

    /**
     * @deprecated use URIs where possible.
     */

    public UpdateAliasRequest(final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, null, target, entity);
    }

    public UpdateAliasRequest(final LiquidURI uri, final LSDTransferEntity newEntity) {
        super();
        setUri(uri);
        setRequestEntity(newEntity);
    }

    public UpdateAliasRequest(final LiquidSessionIdentifier sessionIdentifier, @Nonnull final LSDTransferEntity alias) {
        this(null, sessionIdentifier, null, alias.getURI(), alias);
    }

    public UpdateAliasRequest(@Nonnull final LSDTransferEntity entity) {
        this(null, entity.getURI(), entity);
    }

    public UpdateAliasRequest() {
        super();
    }

    @Nonnull
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