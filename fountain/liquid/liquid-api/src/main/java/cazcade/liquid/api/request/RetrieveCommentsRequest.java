package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RetrieveCommentsRequest extends AbstractRetrievalRequest {
    public RetrieveCommentsRequest(final LiquidURI pool, final int max) {
        this(LiquidSessionIdentifier.ANON, pool, max, false);
    }

    public RetrieveCommentsRequest(@Nonnull final LiquidSessionIdentifier identity, final LiquidURI uri, final int max, final boolean historical) {
        this(null, identity, uri, max, historical);
    }

    public RetrieveCommentsRequest(@Nullable final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidURI uri, final int max, final boolean historical) {
        super();
        setId(id);
        setSessionId(identity);
        setUri(uri);
        setHistorical(historical);
        setMax(max);
    }

    public RetrieveCommentsRequest() {
        super();
    }

    public RetrieveCommentsRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new RetrieveCommentsRequest(getEntity());
    }

    @Override @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_COMMENTS;
    }
}
