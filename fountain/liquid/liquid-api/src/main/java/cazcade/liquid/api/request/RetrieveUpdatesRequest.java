package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class RetrieveUpdatesRequest extends AbstractRetrievalRequest {

    public RetrieveUpdatesRequest() {
        super();
    }


    public RetrieveUpdatesRequest(final LiquidSessionIdentifier identity, final long since) {
        this(null, identity, since);
    }


    public RetrieveUpdatesRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final long since) {
        super();
        setSince(since);
        setId(id);
        setSessionId(identity);
    }

    public RetrieveUpdatesRequest(final long since) {
        this(null, null, since);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new RetrieveUpdatesRequest(getId(), getSessionIdentifier(), getSince());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.EMPTY_LIST;
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.RETRIEVE_UPDATES;
    }


}
