/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class RetrieveAliasRequest extends AbstractRetrievalRequest {
    private RetrieveAliasRequest(final LiquidUUID id, final SessionIdentifier identity, final LiquidUUID target, final LiquidURI uri) {
        super();
        setTarget(target);
        id(id);
        session(identity);
        setUri(uri);
    }

    public RetrieveAliasRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidUUID target) {
        super();
        id(id);
        session(identity);
        setTarget(target);
    }

    public RetrieveAliasRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidURI uri) {
        super();
        id(id);
        session(identity);
        setUri(uri);
    }

    public RetrieveAliasRequest(final SessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrieveAliasRequest(final SessionIdentifier identity, final LiquidURI uri) {
        this(null, identity, uri);
    }

    public RetrieveAliasRequest(final LiquidUUID target) {
        this(null, SessionIdentifier.ANON, target);
    }

    public RetrieveAliasRequest(final LiquidURI uri) {
        this(null, SessionIdentifier.ANON, uri);
    }

    public RetrieveAliasRequest(final SessionIdentifier identity) {
        super();
        session(identity);
    }

    public RetrieveAliasRequest() {
        super();
    }

    public RetrieveAliasRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrieveAliasRequest(getEntity());
    }

    @Nonnull @Override
    public List<AuthorizationRequest> authorizationRequests() {
        return Collections.emptyList();
    }

    @Override @Nonnull
    public RequestType requestType() {
        return RequestType.RETRIEVE_ALIAS;
    }
}