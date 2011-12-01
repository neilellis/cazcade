package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class BoardQueryRequest extends AbstractRequest {


    public BoardQueryRequest() {
    }


    public BoardQueryRequest(LiquidSessionIdentifier liquidSessionId, @Nonnull QueryType type) {
        this(null, liquidSessionId, type, null);
    }

    public BoardQueryRequest(@Nonnull QueryType type) {
        this(null, null, type, null);
    }

    public BoardQueryRequest(@Nonnull QueryType type, LiquidURI alias) {
        this(null, null, type, alias);
    }

    public BoardQueryRequest(LiquidSessionIdentifier sessionIdentifier, @Nonnull QueryType type, LiquidURI alias) {
        this(null, sessionIdentifier, type, alias);
    }


    public BoardQueryRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, @Nonnull QueryType type, @Nullable LiquidURI alias) {
        this.setId(id);
        this.setQueryType(type);
        this.setAlias(alias);
        this.setSessionId(identity);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new BoardQueryRequest(getId(), getSessionIdentifier(), getQueryType(), super.getAlias());
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
