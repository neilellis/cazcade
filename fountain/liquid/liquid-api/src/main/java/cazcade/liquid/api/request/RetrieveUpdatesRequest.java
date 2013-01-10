/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class RetrieveUpdatesRequest extends AbstractRetrievalRequest {
    public RetrieveUpdatesRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final long since) {
        super();
        setSince(since);
        setId(id);
        setSessionId(identity);
    }

    public RetrieveUpdatesRequest(final LiquidSessionIdentifier identity, final long since) {
        this(null, identity, since);
    }

    public RetrieveUpdatesRequest(final long since) {
        this(null, LiquidSessionIdentifier.ANON, since);
    }

    public RetrieveUpdatesRequest() {
        super();
    }

    public RetrieveUpdatesRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrieveUpdatesRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.EMPTY_LIST;
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_UPDATES;
    }
}
