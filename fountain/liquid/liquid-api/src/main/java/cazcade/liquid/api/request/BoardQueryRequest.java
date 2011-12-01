package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class BoardQueryRequest extends AbstractRequest {


    public BoardQueryRequest() {
        super();
    }


    public BoardQueryRequest(final LiquidSessionIdentifier liquidSessionId, @Nonnull final QueryType type) {
        this(null, liquidSessionId, type, null);
    }

    public BoardQueryRequest(@Nonnull final QueryType type) {
        this(null, null, type, null);
    }

    public BoardQueryRequest(@Nonnull final QueryType type, final LiquidURI alias) {
        this(null, null, type, alias);
    }

    public BoardQueryRequest(final LiquidSessionIdentifier sessionIdentifier, @Nonnull final QueryType type, final LiquidURI alias) {
        this(null, sessionIdentifier, type, alias);
    }


    public BoardQueryRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, @Nonnull final QueryType type, @Nullable final LiquidURI alias) {
        super();
        setId(id);
        setQueryType(type);
        setAlias(alias);
        setSessionId(identity);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new BoardQueryRequest(getId(), getSessionIdentifier(), getQueryType(), getAlias());
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList();
    }


    public boolean isMutationRequest() {
        return false;
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList();
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.BOARD_QUERY;
    }


}
