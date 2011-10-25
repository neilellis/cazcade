package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.Arrays;
import java.util.List;

public class SearchRequest extends AbstractRequest {
    private String searchText;

    public SearchRequest() {
    }

    public SearchRequest(String  searchText) {
        this(null, null, searchText);
    }

    public SearchRequest(LiquidUUID id, LiquidSessionIdentifier identity, String searchText) {
        this.id = id;
        this.searchText = searchText;
        this.identity = identity;
    }



    @Override
    public LiquidMessage copy() {
        return new SearchRequest(id, identity, searchText);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return null;
    }


    public boolean isMutationRequest() {
        return false;
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList();
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.SEARCH;
    }


    public String getSearchText() {
        return searchText;
    }
}
