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
    public SearchRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final String searchText) {
        super();
        setId(id);
        setSearchText(searchText);
        setSessionId(identity);
    }

    public SearchRequest(final String searchText) {
        this(null, null, searchText);
    }

    public SearchRequest() {
        super();
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new SearchRequest(getId(), getSessionIdentifier(), getSearchText());
    }

    @Nullable
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return null;
    }

    public List<String> getNotificationLocations() {
        return Arrays.asList();
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.SEARCH;
    }

    public boolean isMutationRequest() {
        return false;
    }
}
