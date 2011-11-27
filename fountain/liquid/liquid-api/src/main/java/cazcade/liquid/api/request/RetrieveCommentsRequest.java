package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

public class RetrieveCommentsRequest extends AbstractRetrievalRequest {


    public RetrieveCommentsRequest() {
    }


    public RetrieveCommentsRequest(LiquidSessionIdentifier identity, LiquidURI uri, int max, boolean historical) {
        this(null, identity, uri, max, historical);
    }

    public RetrieveCommentsRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI uri, int max, boolean historical) {
        this.setId(id);
        this.setSessionId(identity);
        this.setUri(uri);
        this.setHistorical(historical);
        this.setMax(max);
    }

    public RetrieveCommentsRequest(LiquidURI pool, int max) {
        this(null, pool, max, false);
    }


    @Override
    public LiquidMessage copy() {
        return new RetrieveCommentsRequest(getId(), getSessionIdentifier(), getUri(), getMax(), super.isHistorical());
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_COMMENTS;
    }


}
