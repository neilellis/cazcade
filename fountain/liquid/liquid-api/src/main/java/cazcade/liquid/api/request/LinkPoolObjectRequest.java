package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class LinkPoolObjectRequest extends AbstractRequest {
    private LiquidUUID from;
    private LiquidUUID to;
    private LiquidUUID target;
    private boolean unlink;
    //todo: support URIS *then* remove this
    public static final boolean SUPPORTS_URI = false;


    public LinkPoolObjectRequest() {
    }


    public LinkPoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID target, LiquidUUID from, boolean unlink) {
        this(null, identity, target, from, null, unlink);
    }

    public LinkPoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID target, LiquidUUID from, LiquidUUID to) {
        this(null, identity, target, from, to, false);
    }

    public LinkPoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID target, LiquidUUID to) {
        this(null, identity, target, null, to, false);
    }

    public LinkPoolObjectRequest(LiquidUUID target, LiquidUUID to, boolean unlink) {
        this(null, null, target, null, to, unlink);
    }
    public LinkPoolObjectRequest(LiquidUUID id, LiquidSessionIdentifier identity, LiquidUUID target, LiquidUUID from, LiquidUUID to, boolean unlink) {
        this.from = from;
        this.to = to;
        this.target = target;
        this.unlink = unlink;
        this.id = id;
        this.identity = identity;
    }

    public LinkPoolObjectRequest(LiquidSessionIdentifier identity, LiquidUUID target, LiquidUUID from, LiquidUUID to, boolean unlink) {
        this(null, identity, target, from, to, unlink);
    }

    public Collection<LiquidURI> getAffectedEntities() {
        //todo: support URIS
//        ArrayList<LiquidUUID> ids = new ArrayList<LiquidUUID>();
//        if (from != null) {
//            ids.add(from);
//        }
//        if (to != null) {
//            ids.add(to);
//        }
//        ids.add(target);
//        return ids;
       return super.getAffectedEntities();
    }

    @Override
    public LiquidMessage copy() {
        return new LinkPoolObjectRequest(id, identity, target, from, to, unlink);
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
            if (from != null && to != null) {
                requests.add(new AuthorizationRequest(target, LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(from, LiquidPermission.MODIFY));
                requests.add(new AuthorizationRequest(to, LiquidPermission.MODIFY));
            } else if (to != null) {
                requests.add(new AuthorizationRequest(target, LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(to, LiquidPermission.MODIFY));
            }
            return requests;
        } else {
            ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (from != null && to != null) {
                requests.add(new AuthorizationRequest(target, LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(from, LiquidPermission.VIEW));
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
