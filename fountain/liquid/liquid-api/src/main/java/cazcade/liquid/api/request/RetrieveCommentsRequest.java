/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RetrieveCommentsRequest extends AbstractRetrievalRequest {
    public RetrieveCommentsRequest(final LURI pool, final int max) {
        this(SessionIdentifier.ANON, pool, max, false);
    }

    public RetrieveCommentsRequest(@Nonnull final SessionIdentifier identity, final LURI uri, final int max, final boolean historical) {
        this(null, identity, uri, max, historical);
    }

    public RetrieveCommentsRequest(@Nullable final LiquidUUID id, final SessionIdentifier identity, final LURI uri, final int max, final boolean historical) {
        super();
        id(id);
        session(identity);
        setUri(uri);
        setHistorical(historical);
        setMax(max);
    }

    public RetrieveCommentsRequest() {
        super();
    }

    public RetrieveCommentsRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrieveCommentsRequest(getEntity());
    }

    @Override @Nonnull
    public RequestType requestType() {
        return RequestType.R_RETRIEVE_COMMENTS;
    }
}
