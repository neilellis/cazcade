/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class RetrieveAliasRequest extends AbstractRetrievalRequest {
    private RetrieveAliasRequest(final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidURI uri) {
        super();
        setTarget(target);
        setId(id);
        setSessionId(identity);
        setUri(uri);
    }

    public RetrieveAliasRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidUUID target) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
    }

    public RetrieveAliasRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidURI uri) {
        super();
        setId(id);
        setSessionId(identity);
        setUri(uri);
    }

    public RetrieveAliasRequest(final LiquidSessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveAliasRequest(final LiquidSessionIdentifier identity, final LiquidURI uri) {
        this(null, identity, uri);
    }

    public RetrieveAliasRequest(final LiquidUUID target) {
        this(null, LiquidSessionIdentifier.ANON, target);
    }

    public RetrieveAliasRequest(final LiquidURI uri) {
        this(null, LiquidSessionIdentifier.ANON, uri);
    }

    public RetrieveAliasRequest(final LiquidSessionIdentifier identity) {
        super();
        setSessionId(identity);
    }

    public RetrieveAliasRequest() {
        super();
    }

    public RetrieveAliasRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrieveAliasRequest(getEntity());
    }

    @Nonnull @Override
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.emptyList();
    }

    @Override @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_ALIAS;
    }
}