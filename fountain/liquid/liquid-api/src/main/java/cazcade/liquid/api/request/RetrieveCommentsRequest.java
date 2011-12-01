package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RetrieveCommentsRequest extends AbstractRetrievalRequest {


    public RetrieveCommentsRequest() {
    }


    public RetrieveCommentsRequest(@Nullable LiquidSessionIdentifier identity, LiquidURI uri, int max, boolean historical) {
        this(null, identity, uri, max, historical);
    }

    public RetrieveCommentsRequest(@Nullable LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI uri, int max, boolean historical) {
        this.setId(id);
        this.setSessionId(identity);
        this.setUri(uri);
        this.setHistorical(historical);
        this.setMax(max);
    }

    public RetrieveCommentsRequest(LiquidURI pool, int max) {
        this(null, pool, max, false);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrieveCommentsRequest(getId(), getSessionIdentifier(), getUri(), getMax(), super.isHistorical());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_COMMENTS;
    }


}
