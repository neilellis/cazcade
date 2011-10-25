package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class LinkPoolRequest extends AbstractRequest {
    private LiquidUUID from;
    private LiquidUUID to;
    private LiquidUUID target;
    private boolean unlink;

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

    public LinkPoolRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidUUID from, LiquidUUID to, boolean unlink) {
        this.from = from;
        this.to = to;
        this.target = target;
        this.unlink = unlink;
        this.id = id;
        this.identity = identity;
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

    @Override
    public LiquidMessage copy() {
        return new LinkPoolRequest(id, identity, target, from, to, unlink);
    }

    public boolean isMutationRequest() {
        return true;
    }

    public List<String> getNotificationLocations() {
        List<String> locations = new ArrayList<String>();
        if (from != null) {
            locations.add(from.toString());
        }
        if (to != null) {
            locations.add(to.toString());
        }
        return locations;
    }

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.LINK_POOL_OBJECT;
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (unlink) {
            ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (from != null) {
                requests.add(new AuthorizationRequest(target, LiquidPermission.EDIT));
                requests.add(new AuthorizationRequest(from, LiquidPermission.MODIFY));
            }
            return requests;
        } else {
            ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (from != null && to != null) {
                requests.add(new AuthorizationRequest(target, LiquidPermission.EDIT));
                requests.add(new AuthorizationRequest(from, LiquidPermission.MODIFY));
                requests.add(new AuthorizationRequest(to, LiquidPermission.MODIFY));
            } else if (to != null) {
                requests.add(new AuthorizationRequest(target, LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(to, LiquidPermission.MODIFY));
            }
            return requests;

        }
    }

    public LiquidUUID getFrom() {
        return from;
    }

    public LiquidUUID getTo() {
        return to;
    }

    public LiquidUUID getTarget() {
        return target;
    }

    public boolean isUnlink() {
        return unlink;
    }

}
