/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.RequestType;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchRequest extends AbstractRequest {
    public SearchRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final String searchText) {
        super();
        id(id);
        setSearchText(searchText);
        session(identity);
    }

    public SearchRequest(final String searchText) {
        this(null, SessionIdentifier.ANON, searchText);
    }

    public SearchRequest() {
        super();
    }

    public SearchRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new SearchRequest(getEntity());
    }

    @Override @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Collections.emptyList();
    }

    @Override
    public List<String> notificationLocations() {
        return Arrays.asList();
    }

    @Override @Nonnull
    public RequestType requestType() {
        return RequestType.SEARCH;
    }

    @Override
    public boolean isMutationRequest() {
        return false;
    }
}
