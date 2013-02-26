/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RetrievePoolObjectRequest extends AbstractRetrievalRequest {
    public RetrievePoolObjectRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier authenticatedUser, final LiquidUUID pool, final LiquidUUID target, final boolean historical) {
        super();
        session(authenticatedUser);
        setPoolUUID(pool);
        setTarget(target);
        setHistorical(historical);
    }

    private RetrievePoolObjectRequest(final LiquidUUID id, final SessionIdentifier authenticatedUser, final LiquidUUID pool, final LiquidUUID target, final LiquidURI uri) {
        super();
        id(id);
        session(authenticatedUser);
        setPoolUUID(pool);
        setTarget(target);
        setUri(uri);
    }

    public RetrievePoolObjectRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidURI uri, final boolean historical) {
        super();
        id(id);
        session(identity);
        setUri(uri);
        setHistorical(historical);
    }

    public RetrievePoolObjectRequest(final SessionIdentifier identity, final LiquidUUID pool, final LiquidUUID target, final boolean historical) {
        this(null, identity, pool, target, historical);
    }

    public RetrievePoolObjectRequest(final SessionIdentifier identity, final LiquidURI uri, final boolean historical) {
        this(null, identity, uri, historical);
    }


    public RetrievePoolObjectRequest() {
        super();
    }

    public RetrievePoolObjectRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrievePoolObjectRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        if (hasPoolUUID()) {
            return Arrays.asList(new AuthorizationRequest(session(), getTarget(), Permission.VIEW_PERM), new AuthorizationRequest(session(), getPoolUUID(), Permission.VIEW_PERM));
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.RETRIEVE_POOL_OBJECT;
    }
}
