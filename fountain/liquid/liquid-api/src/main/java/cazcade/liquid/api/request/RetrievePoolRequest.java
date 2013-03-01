/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class RetrievePoolRequest extends AbstractRetrievalRequest {
    public RetrievePoolRequest(@Nonnull final SessionIdentifier identity, final LURI uri, @Nonnull final RequestDetailLevel detail, final boolean contents, final boolean orCreate) {
        this(null, identity, null, uri, detail, contents, orCreate, null);
    }

    public RetrievePoolRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, @Nullable final LiquidUUID target, @Nullable final LURI uri, @Nonnull final RequestDetailLevel detail, final boolean contents, final boolean orCreate, @Nullable final ChildSortOrder order) {
        this(id, identity, target, uri, detail, contents, orCreate, order, 50);
    }

    public RetrievePoolRequest(final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidUUID target, final LURI uri, @Nonnull final RequestDetailLevel detail, final boolean contents, final boolean orCreate, final ChildSortOrder order, final int max) {
        super();
        setOrCreate(orCreate);
        session(identity);
        setUri(uri);
        setDetail(detail);
        setContents(contents);
        setOrder(order);
        setTarget(target);
        id(id);
        setMax(max);
    }

    /**
     * @deprecated retrieve by URI not UUID.
     */
    public RetrievePoolRequest(final SessionIdentifier identity, final LiquidUUID target, final boolean contents, final boolean orCreate) {
        this(null, identity, target, null, RequestDetailLevel.NORMAL, contents, orCreate, null);
    }

    public RetrievePoolRequest(final SessionIdentifier identity, final LURI uri, final boolean contents, final boolean orCreate) {
        this(null, identity, null, uri, RequestDetailLevel.NORMAL, contents, orCreate, null);
    }

    public RetrievePoolRequest(final LURI uri, @Nonnull final RequestDetailLevel detailLevel, final boolean contents, final boolean orCreate) {
        this(null, SessionIdentifier.ANON, null, uri, detailLevel, contents, orCreate, null);
    }

    public RetrievePoolRequest(final SessionIdentifier sessionIdentifier, final LURI uri, final ChildSortOrder sortOrder, final boolean orCreate) {
        this(null, sessionIdentifier, null, uri, RequestDetailLevel.NORMAL, true, orCreate, sortOrder);
    }

    /**
     * @deprecated retrieve by URI not UUID.
     */
    public RetrievePoolRequest(final LiquidUUID target, final boolean contents, final boolean orCreate) {
        this(null, SessionIdentifier.ANON, target, null, RequestDetailLevel.NORMAL, contents, orCreate, null);
    }

    public RetrievePoolRequest(final LURI uri, final boolean contents, final boolean orCreate) {
        this(null, SessionIdentifier.ANON, null, uri, RequestDetailLevel.NORMAL, contents, orCreate, null);
    }

    public RetrievePoolRequest(final LURI uri, final ChildSortOrder order, final boolean orCreate) {
        this(null, SessionIdentifier.ANON, null, uri, RequestDetailLevel.NORMAL, true, orCreate, order);
    }

    public RetrievePoolRequest() {
        super();
    }

    public RetrievePoolRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrievePoolRequest(getEntity());
    }

    @Override @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        if (hasTarget()) {
            return Arrays.asList(new AuthorizationRequest(session(), getTarget(), Permission.P_VIEW));
        } else {
            return Arrays.asList(new AuthorizationRequest(session(), uri(), Permission.P_VIEW));
        }
    }

    @Override @Nonnull
    public RequestType requestType() {
        return RequestType.R_RETRIEVE_POOL;
    }
}
