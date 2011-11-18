package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;

import java.util.Arrays;
import java.util.List;

public class SearchRequest extends AbstractRequest {

    public SearchRequest() {
    }

    public SearchRequest(String searchText) {
        this(null, null, searchText);
    }

    public SearchRequest(LiquidUUID id, LiquidSessionIdentifier identity, String searchText) {
        this.setId(id);
        this.setSearchText(searchText);
        this.setIdentity(identity);
    }


    @Override
    public LiquidMessage copy() {
        return new SearchRequest(getId(), getSessionIdentifier(), super.getSearchText());
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


}
