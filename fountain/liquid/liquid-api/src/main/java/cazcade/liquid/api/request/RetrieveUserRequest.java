/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RetrieveUserRequest extends AbstractRetrievalRequest {

    public RetrieveUserRequest(@Nonnull final SessionIdentifier identity, final LURI uri, final boolean internal) {
        this(null, identity, uri, internal);
    }

    RetrieveUserRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, @Nonnull final LURI uri, final boolean internal) {
        super();
        if (id != null) {
            id(id);
        }
        session(identity);
        setUri(uri);
        setInternal(internal);
    }

    public RetrieveUserRequest(final SessionIdentifier identity, final LURI uri) {
        this(null, identity, uri, false);
    }

    public RetrieveUserRequest(final SessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveUserRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidUUID target) {
        super();
        if (id != null) {
            id(id);
        }
        session(identity);
        setTarget(target);
    }

    public RetrieveUserRequest(final LiquidUUID target) {
        this(null, SessionIdentifier.ANON, target);
    }

    public RetrieveUserRequest(final LURI uri) {
        this(null, SessionIdentifier.ANON, uri, false);
    }

    public RetrieveUserRequest() {
        super();
    }

    public RetrieveUserRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrieveUserRequest(getEntity());
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_RETRIEVE_USER;
    }
}
