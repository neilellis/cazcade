package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class BoardQueryRequest extends AbstractRequest {


    public BoardQueryRequest() {
    }


    public BoardQueryRequest(LiquidSessionIdentifier liquidSessionId, QueryType type) {
        this(null, liquidSessionId, type, null);
    }

    public BoardQueryRequest(QueryType type) {
        this(null, null, type, null);
    }

    public BoardQueryRequest(QueryType type, LiquidURI alias) {
        this(null, null, type, alias);
    }

    public BoardQueryRequest(LiquidSessionIdentifier sessionIdentifier, QueryType type, LiquidURI alias) {
        this(null, sessionIdentifier, type, alias);
    }


    public BoardQueryRequest(LiquidUUID id, LiquidSessionIdentifier identity, QueryType type, LiquidURI alias) {
        this.setId(id);
        this.setQueryType(type);
        this.setAlias(alias);
        this.setSessionId(identity);
    }


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


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.BOARD_QUERY;
    }


}
