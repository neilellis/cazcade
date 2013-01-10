/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RetrieveUserRequest extends AbstractRetrievalRequest {
    private RetrieveUserRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, @Nullable final LiquidUUID target, @Nullable final LiquidURI uri) {
        super();
        setTarget(target);
        setId(id);
        setSessionId(identity);
        setUri(uri);
    }

    public RetrieveUserRequest(final LiquidSessionIdentifier identity, final LiquidURI uri, final boolean internal) {
        this(null, identity, uri, internal);
    }

    public RetrieveUserRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidURI uri, final boolean internal) {
        super();
        setId(id);
        setSessionId(identity);
        setUri(uri);
        setInternal(internal);
    }

    public RetrieveUserRequest(final LiquidSessionIdentifier identity, final LiquidURI uri) {
        this(null, identity, uri, false);
    }

    public RetrieveUserRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveUserRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidUUID target) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
    }

    public RetrieveUserRequest(final LiquidUUID target) {
        this(null, LiquidSessionIdentifier.ANON, target);
    }

    public RetrieveUserRequest(final LiquidURI uri) {
        this(null, LiquidSessionIdentifier.ANON, uri, false);
    }

    public RetrieveUserRequest() {
        super();
    }

    public RetrieveUserRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrieveUserRequest(getEntity());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_USER;
    }
}
