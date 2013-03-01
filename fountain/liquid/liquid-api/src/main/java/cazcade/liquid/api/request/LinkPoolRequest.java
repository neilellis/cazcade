/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class LinkPoolRequest extends AbstractRequest {
    public LinkPoolRequest(@Nullable final LiquidUUID id, final SessionIdentifier identity, final LiquidUUID target, @Nullable final LiquidUUID from, @Nullable final LiquidUUID to, final boolean unlink) {
        super();
        setFrom(from);
        setTo(to);
        setTarget(target);
        setUnlink(unlink);
        id(id);
        session(identity);
    }

    public LinkPoolRequest(final SessionIdentifier identity, final LiquidUUID target, final LiquidUUID from, final boolean unlink) {
        this(null, identity, target, from, null, unlink);
    }

    public LinkPoolRequest(final SessionIdentifier identity, final LiquidUUID target, final LiquidUUID from, final LiquidUUID to) {
        this(null, identity, target, from, to, false);
    }

    public LinkPoolRequest(final SessionIdentifier identity, final LiquidUUID target, final LiquidUUID to) {
        this(null, identity, target, null, to, false);
    }

    public LinkPoolRequest() {
        super();
    }

    public LinkPoolRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new LinkPoolRequest(getEntity());
    }

    public Collection<LURI> affectedEntities() {
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
        return super.affectedEntities();
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        if (isUnlink()) {
            final ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (hasFrom()) {
                requests.add(new AuthorizationRequest(session(), getTarget(), Permission.P_EDIT));
                requests.add(new AuthorizationRequest(session(), getFrom(), Permission.P_MODIFY));
            }
            return requests;
        } else {
            final ArrayList<AuthorizationRequest> requests = new ArrayList<AuthorizationRequest>();
            if (hasFrom() && hasTo()) {
                requests.add(new AuthorizationRequest(session(), getTarget(), Permission.P_EDIT));
                requests.add(new AuthorizationRequest(session(), getFrom(), Permission.P_MODIFY));
                requests.add(new AuthorizationRequest(session(), getTo(), Permission.P_MODIFY));
            } else if (hasTo()) {
                requests.add(new AuthorizationRequest(session(), getTarget(), Permission.P_VIEW));
                requests.add(new AuthorizationRequest(session(), getTo(), Permission.P_MODIFY));
            }
            return requests;
        }
    }

    @Nonnull
    public List<String> notificationLocations() {
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
    public RequestType requestType() {
        return RequestType.R_LINK_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }
}
