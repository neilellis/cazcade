package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class LinkPoolRequest extends AbstractRequest {

    public LinkPoolRequest() {
    }

    public LinkPoolRequest(LiquidSessionIdentifier identity, LiquidUUID target, LiquidUUID from, boolean unlink) {
        this(null, identity, target, from, null, unlink);
    }

    public LinkPoolRequest(LiquidSessionIdentifier identity, LiquidUUID target, LiquidUUID from, LiquidUUID to) {
        this(null, identity, target, from, to, false);
    }

    public LinkPoolRequest(LiquidSessionIdentifier identity, LiquidUUID target, LiquidUUID to) {
        this(null, identity, target, null, to, false);
    }

    public LinkPoolRequest(@Nullable LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, @Nullable LiquidUUID from, @Nullable LiquidUUID to, boolean unlink) {
        this.setFrom(from);
        this.setTo(to);
        this.setTarget(target);
        this.setUnlink(unlink);
        this.setId(id);
        this.setSessionId(identity);
    }

    public Collection<LiquidURI> getAffectedEntities() {
//        ArrayList<LiquidUUID> ids = new ArrayList<LiquidUUID>();
//        if (from != null) {
//            ids.add(from);
//        }
//        if (to != null) {
//            ids.add(to);
//        }
//        ids.add(target);
//        return ids;
        //todo: support uris
        return super.getAffectedEntities();
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new LinkPoolRequest(getId(), getSessionIdentifier(), super.getTarget(), super.getFrom(), super.getTo(), super.isUnlink());
    }

    public boolean isMutationRequest() {
        return true;
    }

    @Nonnull
    public List<String> getNotificationLocations() {
        List<String> locations = new ArrayList<String>();
        if (super.getFrom() != null) {
            locations.add(super.getFrom().toString());
        }
        if (super.getTo() != null) {
            locations.add(super.getTo().toString());
        }
        return locations;
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.LINK_POOL_OBJECT;
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (super.isUnlink()) {
            ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (super.getFrom() != null) {
                requests.add(new AuthorizationRequest(super.getTarget(), LiquidPermission.EDIT));
                requests.add(new AuthorizationRequest(super.getFrom(), LiquidPermission.MODIFY));
            }
            return requests;
        } else {
            ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (super.getFrom() != null && super.getTo() != null) {
                requests.add(new AuthorizationRequest(super.getTarget(), LiquidPermission.EDIT));
                requests.add(new AuthorizationRequest(super.getFrom(), LiquidPermission.MODIFY));
                requests.add(new AuthorizationRequest(super.getTo(), LiquidPermission.MODIFY));
            } else if (super.getTo() != null) {
                requests.add(new AuthorizationRequest(super.getTarget(), LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(super.getTo(), LiquidPermission.MODIFY));
            }
            return requests;

        }
    }


}
