package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

public class RetrieveCommentsRequest extends AbstractRetrievalRequest {

    private int max;

    public RetrieveCommentsRequest() {
    }


    public RetrieveCommentsRequest(LiquidSessionIdentifier identity, LiquidURI uri, int max, boolean historical) {
        this(null, identity, uri, max, historical);
    }

    public RetrieveCommentsRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI uri, int max, boolean historical) {
        this.id = id;
        this.identity = identity;
        this.uri = uri;
        this.historical = historical;
        this.max = max;
    }

    public RetrieveCommentsRequest(LiquidURI pool, int max) {
        this(null, pool, max, false);
    }


    @Override
    public LiquidMessage copy() {
        return new RetrieveCommentsRequest(id, identity, uri, max, historical);
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_COMMENTS;
    }

    public int getMax() {
        return max;
    }
}
