/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.RequestType;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class RetrieveUpdatesRequest extends AbstractRetrievalRequest {
    public RetrieveUpdatesRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final long since) {
        super();
        setSince(since);
        id(id);
        session(identity);
    }

    public RetrieveUpdatesRequest(final SessionIdentifier identity, final long since) {
        this(null, identity, since);
    }

    public RetrieveUpdatesRequest(final long since) {
        this(null, SessionIdentifier.ANON, since);
    }

    public RetrieveUpdatesRequest() {
        super();
    }

    public RetrieveUpdatesRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrieveUpdatesRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Collections.EMPTY_LIST;
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.RETRIEVE_UPDATES;
    }
}
