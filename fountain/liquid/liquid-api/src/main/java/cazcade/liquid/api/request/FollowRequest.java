    package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FollowRequest extends AbstractRequest {

    /**
     * If negative then unfollow.
     */

    private boolean follow;

    public FollowRequest() {
    }

    public FollowRequest(LiquidSessionIdentifier identity, LiquidURI uri, boolean follow) {
        this(null, identity, uri, follow);
    }

    public FollowRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI uri, boolean follow) {
        this.id = id;
        this.identity = identity;
        this.follow = follow;
        this.uri = uri;
    }

    public FollowRequest(LiquidURI uri, boolean follow) {
        this(null, null, uri, follow);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        if (identity != null) {
            return Arrays.asList(uri, identity.getAliasURL());
        } else {
            return Arrays.asList(uri);
        }
    }

    @Override
    public LiquidMessage copy() {
        return new FollowRequest(id, identity, uri, follow);
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        return new ArrayList<AuthorizationRequest>();
    }


    public boolean isMutationRequest() {
        return true;
    }

    @Override
    public List<String> getNotificationLocations() {
        return Arrays.asList(uri.toString());
    }


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.FOLLOW;
    }

    public boolean isFollow() {
        return follow;
    }
}
