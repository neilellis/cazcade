package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequestType;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchRequest extends AbstractRequest {
    public SearchRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final String searchText) {
        super();
        setId(id);
        setSearchText(searchText);
        setSessionId(identity);
    }

    public SearchRequest(final String searchText) {
        this(null, LiquidSessionIdentifier.ANON, searchText);
    }

    public SearchRequest() {
        super();
    }

    public SearchRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new SearchRequest(getEntity());
    }

    @Override
    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getNotificationLocations() {
        return Arrays.asList();
    }

    @Override
    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.SEARCH;
    }

    @Override
    public boolean isMutationRequest() {
        return false;
    }
}
