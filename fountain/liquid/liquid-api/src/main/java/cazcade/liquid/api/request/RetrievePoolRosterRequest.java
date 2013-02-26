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

public class RetrievePoolRosterRequest extends AbstractRetrievalRequest {
    private RetrievePoolRosterRequest(final LiquidUUID id, final SessionIdentifier identity, final LiquidUUID target, final LiquidURI uri) {
        super();
        setTarget(target);
        id(id);
        session(identity);
        setUri(uri);
    }

    public RetrievePoolRosterRequest(final SessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public RetrievePoolRosterRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidUUID target) {
        super();
        setTarget(target);
        id(id);
        session(identity);
    }

    public RetrievePoolRosterRequest(final SessionIdentifier identity, final LiquidURI uri) {
        this(null, identity, uri);
    }

    public RetrievePoolRosterRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidURI uri) {
        super();
        id(id);
        session(identity);
        setUri(uri);
    }

    public RetrievePoolRosterRequest() {
        super();
    }

    public RetrievePoolRosterRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrievePoolRosterRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        if (hasTarget()) {
            return Arrays.asList(new AuthorizationRequest(session(), getTarget(), Permission.VIEW_PERM));
        } else {
            return Arrays.asList(new AuthorizationRequest(session(), uri(), Permission.VIEW_PERM));
        }
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.RETRIEVE_POOL_ROSTER;
    }
}
