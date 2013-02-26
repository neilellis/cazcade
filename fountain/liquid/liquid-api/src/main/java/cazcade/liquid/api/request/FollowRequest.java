/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FollowRequest extends AbstractRequest {
    public FollowRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidURI uri, final boolean follow) {
        super();
        id(id);
        session(identity);
        setFollow(follow);
        setUri(uri);
    }

    public FollowRequest(final SessionIdentifier identity, final LiquidURI uri, final boolean follow) {
        this(null, identity, uri, follow);
    }

    public FollowRequest(final LiquidURI uri, final boolean follow) {
        this(null, SessionIdentifier.ANON, uri, follow);
    }

    /**
     * If negative then unfollow.
     */


    public FollowRequest() {
        super();
    }

    public FollowRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new FollowRequest(getEntity());
    }

    public Collection<LiquidURI> affectedEntities() {
        if (!session().anon()) {
            return Arrays.asList(uri(), session().aliasURI());
        } else {
            return Arrays.asList(uri());
        }
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return new ArrayList<AuthorizationRequest>();
    }

    @Override
    public List<String> notificationLocations() {
        return Arrays.asList(uri().toString());
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.FOLLOW;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
