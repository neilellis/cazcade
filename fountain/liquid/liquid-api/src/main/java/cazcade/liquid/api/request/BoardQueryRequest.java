/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class BoardQueryRequest extends AbstractRequest {
    public BoardQueryRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, @Nonnull final QueryType type, @Nullable final LiquidURI alias) {
        super();
        id(id);
        setQueryType(type);
        setAlias(alias);
        session(identity);
    }

    public BoardQueryRequest(final SessionIdentifier sessionIdentifier, @Nonnull final QueryType type, @Nullable final LiquidURI alias, int start, int max) {
        this(null, sessionIdentifier, type, alias);
        setStart(start);
        setMax(max);
    }

    public BoardQueryRequest(@Nonnull final QueryType type, @Nullable final LiquidURI alias, int start, int max) {
        this(null, SessionIdentifier.ANON, type, alias);
        setStart(start);
        setMax(max);
    }

    public BoardQueryRequest(@Nonnull final QueryType type, @Nullable final LiquidURI alias) {
        this(null, SessionIdentifier.ANON, type, alias);
    }

    public BoardQueryRequest() {
        super();
    }

    public BoardQueryRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new BoardQueryRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Arrays.asList();
    }

    public List<String> notificationLocations() {
        return Arrays.asList();
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.BOARD_QUERY;
    }

    public boolean isMutationRequest() {
        return false;
    }
}
