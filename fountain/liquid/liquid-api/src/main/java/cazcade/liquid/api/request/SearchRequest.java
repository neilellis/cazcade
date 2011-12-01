package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class SearchRequest extends AbstractRequest {

    public SearchRequest() {
    }

    public SearchRequest(String searchText) {
        this(null, null, searchText);
    }

    public SearchRequest(@Nullable LiquidUUID id, @Nullable LiquidSessionIdentifier identity, String searchText) {
        this.setId(id);
        this.setSearchText(searchText);
        this.setSessionId(identity);
    }


    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new SearchRequest(getId(), getSessionIdentifier(), super.getSearchText());
    }

    @Nullable
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return null;
    }


    public boolean isMutationRequest() {
        return false;
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList();
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.SEARCH;
    }


}
