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

public class RetrieveSessionRequest extends AbstractRetrievalRequest {
    public RetrieveSessionRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidUUID target) {
        super();
        id(id);
        session(identity);
        setTarget(target);
    }

    public RetrieveSessionRequest(final SessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }


    public RetrieveSessionRequest() {
        super();
    }

    public RetrieveSessionRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrieveSessionRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(session(), getTarget(), Permission.VIEW_PERM));
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.RETRIEVE_SESSION;
    }
}