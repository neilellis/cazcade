package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class BoardQueryRequest extends AbstractRequest {
    private QueryType type;
    private LiquidURI alias;
    private int start;

    //60 is divisible by 1,2,3,4,5,6 - therefore a good number to show as blocks on a page with columns between 1 and 6.
    private int max=60;



    public int getStart() {
        return start;
    }

    public int getMax() {
        return max;
    }

    public enum QueryType {
        MY, USERS_BOARDS, RECENT, HISTORY, POPULAR
    }


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
        this.id = id;
        this.type = type;
        this.alias= alias;
        this.identity = identity;
    }



    @Override
    public LiquidMessage copy() {
        return new BoardQueryRequest(id, identity, type, alias);
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

    public QueryType getType() {
        return type;
    }

    public LiquidURI getAlias() {
        return alias;
    }
}
