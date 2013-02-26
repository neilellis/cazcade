/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpdatePoolRequest extends AbstractUpdateRequest {
    protected UpdatePoolRequest(final LiquidUUID id, final SessionIdentifier identity, final LiquidUUID target, final LiquidURI uri, final TransferEntity entity) {
        super();
        id(id);
        session(identity);
        setTarget(target);
        setUri(uri);
        setRequestEntity(entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdatePoolRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidUUID target, final TransferEntity entity) {
        super();
        id(id);
        session(identity);
        setTarget(target);
        setRequestEntity(entity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdatePoolRequest(final SessionIdentifier identity, final LiquidUUID target, final TransferEntity entity) {
        this(null, identity, target, entity);
    }

    public UpdatePoolRequest(@Nonnull final SessionIdentifier identity, final LiquidURI poolURI, final TransferEntity newEntity) {
        super();
        session(identity);
        setUri(poolURI);
        setRequestEntity(newEntity);
    }

    /**
     * @deprecated use URIs where possible.
     */
    public UpdatePoolRequest(final LiquidUUID target, final TransferEntity entity) {
        this(null, SessionIdentifier.ANON, target, entity);
    }

    public UpdatePoolRequest(@Nonnull final TransferEntity updateEntity) {
        this(SessionIdentifier.ANON, updateEntity.uri(), updateEntity);
    }

    public UpdatePoolRequest() {
        super();
    }

    UpdatePoolRequest(final TransferEntity entity, String marker) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new UpdatePoolRequest(getEntity(), "copy constructor");
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.UPDATE_POOL;
    }
}
