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


    public FollowRequest() {
    }

    public FollowRequest(LiquidSessionIdentifier identity, LiquidURI uri, boolean follow) {
        this(null, identity, uri, follow);
    }

    public FollowRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidURI uri, boolean follow) {
        this.setId(id);
        this.setIdentity(identity);
        this.setFollow(follow);
        this.setUri(uri);
    }

    public FollowRequest(LiquidURI uri, boolean follow) {
        this(null, null, uri, follow);
    }


    public Collection<LiquidURI> getAffectedEntities() {
        if (getSessionIdentifier() != null) {
            return Arrays.asList(getUri(), getSessionIdentifier().getAliasURL());
        } else {
            return Arrays.asList(getUri());
        }
    }

    @Override
    public LiquidMessage copy() {
        return new FollowRequest(getId(), getSessionIdentifier(), getUri(), super.isFollow());
    }

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


    public LiquidRequestType getRequestType() {
        return LiquidRequestType.FOLLOW;
    }

}
