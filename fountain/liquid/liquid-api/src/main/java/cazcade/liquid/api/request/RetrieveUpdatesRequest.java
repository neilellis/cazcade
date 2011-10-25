package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidUUID;

import java.util.Collections;
import java.util.List;

public class RetrieveUpdatesRequest extends AbstractRetrievalRequest {
    private long since;

    public RetrieveUpdatesRequest() {
    }


    public RetrieveUpdatesRequest(LiquidSessionIdentifier identity, long since) {
        this(null, identity, since);
    }


    public RetrieveUpdatesRequest(LiquidUUID id, LiquidSessionIdentifier identity, long since) {
        this.since = since;
        this.id = id;
        this.identity = identity;
    }

    public RetrieveUpdatesRequest(long since) {
        this(null, null, since);
    }


    @Override
    public LiquidMessage copy() {
        return new RetrieveUpdatesRequest(id, identity, since);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.EMPTY_LIST;
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_UPDATES;
    }

    public long getSince() {
        return since;
    }
}
