package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RetrieveCommentsRequest extends AbstractRetrievalRequest {


    public RetrieveCommentsRequest() {
        super();
    }


    public RetrieveCommentsRequest(@Nullable final LiquidSessionIdentifier identity, final LiquidURI uri, final int max, final boolean historical) {
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

    public RetrieveCommentsRequest(final LiquidURI pool, final int max) {
        this(null, pool, max, false);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrieveCommentsRequest(getId(), getSessionIdentifier(), getUri(), getMax(), isHistorical());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_COMMENTS;
    }


}
