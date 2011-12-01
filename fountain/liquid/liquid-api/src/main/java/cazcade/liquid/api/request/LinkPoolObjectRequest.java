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
public class LinkPoolObjectRequest extends AbstractRequest {
    //todo: support URIS *then* remove this
    public static final boolean SUPPORTS_URI = false;


    public LinkPoolObjectRequest() {
        super();
    }


    public LinkPoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidUUID from, final boolean unlink) {
        this(null, identity, target, from, null, unlink);
    }

    public LinkPoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidUUID from, final LiquidUUID to) {
        this(null, identity, target, from, to, false);
    }

    public LinkPoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidUUID to) {
        this(null, identity, target, null, to, false);
    }

    public LinkPoolObjectRequest(final LiquidUUID target, final LiquidUUID to, final boolean unlink) {
        this(null, null, target, null, to, unlink);
    }

    public LinkPoolObjectRequest(@Nullable final LiquidUUID id, @Nullable final LiquidSessionIdentifier identity, final LiquidUUID target, @Nullable final LiquidUUID from, @Nullable final LiquidUUID to, final boolean unlink) {
        super();
        setFrom(from);
        setTo(to);
        setTarget(target);
        setUnlink(unlink);
        setId(id);
        setSessionId(identity);
    }

    public LinkPoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidUUID from, final LiquidUUID to, final boolean unlink) {
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

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new LinkPoolObjectRequest(getId(), getSessionIdentifier(), getTarget(), getFrom(), getTo(), isUnlink());
    }

    public boolean isMutationRequest() {
        return true;
    }

    @Nonnull
    public List<String> getNotificationLocations() {
        final List<String> locations = new ArrayList<String>();
        if (getFrom() != null) {
            locations.add(getFrom().toString());
        }
        if (getTo() != null) {
            locations.add(getTo().toString());
        }
        return locations;
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.LINK_POOL_OBJECT;
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (isUnlink()) {
            final ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (getFrom() != null && getTo() != null) {
                requests.add(new AuthorizationRequest(getTarget(), LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(getFrom(), LiquidPermission.MODIFY));
                requests.add(new AuthorizationRequest(getTo(), LiquidPermission.MODIFY));
            } else if (getTo() != null) {
                requests.add(new AuthorizationRequest(getTarget(), LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(getTo(), LiquidPermission.MODIFY));
            }
            return requests;
        } else {
            final ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (getFrom() != null && getTo() != null) {
                requests.add(new AuthorizationRequest(getTarget(), LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(getFrom(), LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(getTo(), LiquidPermission.MODIFY));
            } else if (getTo() != null) {
                requests.add(new AuthorizationRequest(getTarget(), LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(getTo(), LiquidPermission.MODIFY));
            }
            return requests;

        }
    }


}
