/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdateAliasRequest extends AbstractUpdateRequest {
    UpdateAliasRequest(@Nullable final LiquidUUID id, final SessionIdentifier identity, @Nullable final LiquidUUID target, final LiquidURI uri, final TransferEntity entity) {
        super();
        setUri(uri);
        id(id);
        session(identity);
        setTarget(target);
        setRequestEntity(entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    UpdateAliasRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidUUID target, final TransferEntity entity) {
        super();
        id(id);
        session(identity);
        setTarget(target);
        setRequestEntity(entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdateAliasRequest(final SessionIdentifier identity, final LiquidUUID target, final TransferEntity entity) {
        this(null, identity, target, entity);
    }

    public UpdateAliasRequest(@Nonnull final SessionIdentifier identity, final LiquidURI uri, final TransferEntity newEntity) {
        super();
        setUri(uri);
        session(identity);
        setRequestEntity(newEntity);
    }

    /**
     * @deprecated use URIs where possible.
     */

    UpdateAliasRequest(final LiquidUUID target, final TransferEntity entity) {
        this(null, SessionIdentifier.ANON, target, entity);
    }

    UpdateAliasRequest(final LiquidURI uri, final TransferEntity newEntity) {
        super();
        setUri(uri);
        setRequestEntity(newEntity);
    }

    public UpdateAliasRequest(final SessionIdentifier sessionIdentifier, @Nonnull final TransferEntity alias) {
        this(null, sessionIdentifier, null, alias.uri(), alias);
    }

    public UpdateAliasRequest(@Nonnull final TransferEntity alias) {
        super();
        setUri(alias.uri());
        setRequestEntity(alias);
    }

    UpdateAliasRequest(@Nonnull final TransferEntity entity, String marker) {
        super(entity);
    }

    public UpdateAliasRequest() {
        super();
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new UpdateAliasRequest(getEntity(), "copy constructor");
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.UPDATE_ALIAS;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}