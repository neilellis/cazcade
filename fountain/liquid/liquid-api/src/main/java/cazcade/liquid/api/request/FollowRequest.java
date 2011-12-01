package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FollowRequest extends AbstractRequest {

    /**
     * If negative then unfollow.
     */


    public FollowRequest() {
        super();
    }

    public FollowRequest(final LiquidSessionIdentifier identity, final LiquidURI uri, final boolean follow) {
        this(null, identity, uri, follow);
    }

    public FollowRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidURI uri, final boolean follow) {
        super();
        setId(id);
        setSessionId(identity);
        setFollow(follow);
        setUri(uri);
    }

    public FollowRequest(final LiquidURI uri, final boolean follow) {
        this(null, null, uri, follow);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        if (getSessionIdentifier() != null) {
            return Arrays.asList(getUri(), getSessionIdentifier().getAliasURL());
        } else {
            return Arrays.asList(getUri());
        }
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new FollowRequest(getId(), getSessionIdentifier(), getUri(), isFollow());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return new ArrayList<AuthorizationRequest>();
    }


    public boolean isMutationRequest() {
        return true;
    }

    @Override
    public List<String> getNotificationLocations() {
        return Arrays.asList(getUri().toString());
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.FOLLOW;
    }

}
