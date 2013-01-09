package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class LinkPoolRequest extends AbstractRequest {
    public LinkPoolRequest(@Nullable final LiquidUUID id, final LiquidSessionIdentifier identity, final LiquidUUID target,
                           @Nullable final LiquidUUID from, @Nullable final LiquidUUID to, final boolean unlink) {
        super();
        setFrom(from);
        setTo(to);
        setTarget(target);
        setUnlink(unlink);
        setId(id);
        setSessionId(identity);
    }

    public LinkPoolRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidUUID from,
                           final boolean unlink) {
        this(null, identity, target, from, null, unlink);
    }

    public LinkPoolRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidUUID from,
                           final LiquidUUID to) {
        this(null, identity, target, from, to, false);
    }

    public LinkPoolRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LiquidUUID to) {
        this(null, identity, target, null, to, false);
    }

    public LinkPoolRequest() {
        super();
    }

    public LinkPoolRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull
    @Override
    public LiquidMessage copy() {
        return new LinkPoolRequest(getEntity());
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
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (isUnlink()) {
            final ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (hasFrom()) {
                requests.add(new AuthorizationRequest(getSessionIdentifier(), getTarget(), LiquidPermission.EDIT));
                requests.add(new AuthorizationRequest(getSessionIdentifier(), getFrom(), LiquidPermission.MODIFY));
            }
            return requests;
        }
        else {
            final ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (hasFrom() && hasTo()) {
                requests.add(new AuthorizationRequest(getSessionIdentifier(), getTarget(), LiquidPermission.EDIT));
                requests.add(new AuthorizationRequest(getSessionIdentifier(), getFrom(), LiquidPermission.MODIFY));
                requests.add(new AuthorizationRequest(getSessionIdentifier(), getTo(), LiquidPermission.MODIFY));
            }
            else if (hasTo()) {
                requests.add(new AuthorizationRequest(getSessionIdentifier(), getTarget(), LiquidPermission.VIEW));
                requests.add(new AuthorizationRequest(getSessionIdentifier(), getTo(), LiquidPermission.MODIFY));
            }
            return requests;
        }
    }

    @Nonnull
    public List<String> getNotificationLocations() {
        final List<String> locations = new ArrayList<String>();
        if (hasFrom()) {
            locations.add(getFrom().toString());
        }
        if (hasTo()) {
            locations.add(getTo().toString());
        }
        return locations;
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.LINK_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
