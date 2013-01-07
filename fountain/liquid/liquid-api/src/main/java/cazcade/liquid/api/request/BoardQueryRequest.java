package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class BoardQueryRequest extends AbstractRequest {
    public BoardQueryRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity,
                             @Nonnull final QueryType type, @Nullable final LiquidURI alias) {
        super();
        setId(id);
        setQueryType(type);
        setAlias(alias);
        setSessionId(identity);
    }

    public BoardQueryRequest(final LiquidSessionIdentifier sessionIdentifier, @Nonnull final QueryType type,
                             @Nullable final LiquidURI alias) {
        this(null, sessionIdentifier, type, alias);
    }

    public BoardQueryRequest(final LiquidSessionIdentifier liquidSessionId, @Nonnull final QueryType type) {
        this(null, liquidSessionId, type, null);
    }


    public BoardQueryRequest() {
        super();
    }

    public BoardQueryRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new BoardQueryRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList();
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList();
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.BOARD_QUERY;
    }

    public boolean isMutationRequest() {
        return false;
    }
}
