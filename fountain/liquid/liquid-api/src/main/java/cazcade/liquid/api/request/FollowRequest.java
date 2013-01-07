package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FollowRequest extends AbstractRequest {
    public FollowRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidURI uri, final boolean follow) {
        super();
        setId(id);
        setSessionId(identity);
        setFollow(follow);
        setUri(uri);
    }

    public FollowRequest(final LiquidSessionIdentifier identity, final LiquidURI uri, final boolean follow) {
        this(null, identity, uri, follow);
    }

    public FollowRequest(final LiquidURI uri, final boolean follow) {
        this(null, LiquidSessionIdentifier.ANON, uri, follow);
    }

    /**
     * If negative then unfollow.
     */


    public FollowRequest() {
        super();
    }

    public FollowRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new FollowRequest(getEntity());
    }

    public Collection<LiquidURI> getAffectedEntities() {
        if (!getSessionIdentifier().isAnon()) {
            return Arrays.asList(getUri(), getSessionIdentifier().getAliasURL());
        }
        else {
            return Arrays.asList(getUri());
        }
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return new ArrayList<AuthorizationRequest>();
    }

    @Override
    public List<String> getNotificationLocations() {
        return Arrays.asList(getUri().toString());
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.FOLLOW;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
