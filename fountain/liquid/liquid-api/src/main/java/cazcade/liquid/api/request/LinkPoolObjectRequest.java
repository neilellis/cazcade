package cazcade.liquid.api.request;

import cazcade.liquid.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class LinkPoolObjectRequest extends AbstractRequest {
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
        this.setFrom(from);
        this.setTo(to);
        this.setTarget(target);
        this.setUnlink(unlink);
        this.setId(id);
        this.setIdentity(identity);
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
        return new LinkPoolObjectRequest(getId(), getSessionIdentifier(), super.getTarget(), super.getFrom(), super.getTo(), super.isUnlink());
    }

    public boolean isMutationRequest() {
        return true;
    }

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

    public LiquidRequestType getRequestType() {
        return LiquidRequestType.LINK_POOL_OBJECT;
    }

    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (super.isUnlink()) {
            ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (super.getFrom() != null && super.getTo() != null) {
                requests.add(new AuthorizationRequest(super.getTarget(), LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(super.getFrom(), LiquidPermission.MODIFY));
                requests.add(new AuthorizationRequest(super.getTo(), LiquidPermission.MODIFY));
            } else if (super.getTo() != null) {
                requests.add(new AuthorizationRequest(super.getTarget(), LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(super.getTo(), LiquidPermission.MODIFY));
            }
            return requests;
        } else {
            ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (super.getFrom() != null && super.getTo() != null) {
                requests.add(new AuthorizationRequest(super.getTarget(), LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(super.getFrom(), LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(super.getTo(), LiquidPermission.MODIFY));
            } else if (super.getTo() != null) {
                requests.add(new AuthorizationRequest(super.getTarget(), LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(super.getTo(), LiquidPermission.MODIFY));
            }
            return requests;

        }
    }


}
