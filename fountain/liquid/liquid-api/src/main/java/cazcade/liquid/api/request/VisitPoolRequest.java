/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Type;
import cazcade.liquid.api.lsd.Types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisitPoolRequest extends AbstractRetrievalRequest {
    private VisitPoolRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, @Nonnull final Type type, final LURI uri, @Nullable final LURI previous, final boolean orCreate, final int max, final boolean listed, @Nullable final PermissionChangeType permission, @Nullable final String imageUrl) {
        super();
        setOrCreate(orCreate);
        setListed(listed);
        setPermission(permission);
        id(id);
        session(identity);
        setUri(uri);
        setPreviousPool(previous);
        setMax(max);
        setPoolType(type);
        if (imageUrl != null) {
            setImageUrl(imageUrl);
        }
    }

    private VisitPoolRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, @Nonnull final Type type, final LURI uri, @Nullable final LURI previous, final boolean orCreate, final boolean listed) {
        this(id, identity, type, uri, previous, orCreate, 60, listed, null, null);
    }

    public VisitPoolRequest(@Nonnull final Type type, final LURI uri, final LURI previous, final boolean orCreate, final boolean listed, final PermissionChangeType permission) {
        this(null, SessionIdentifier.ANON, type, uri, previous, orCreate, 60, listed, permission, null);
    }

    public VisitPoolRequest(@Nonnull final Type type, final LURI uri, final LURI previous, final boolean orCreate, final boolean listed) {
        this(null, SessionIdentifier.ANON, type, uri, previous, orCreate, listed);
    }

    public VisitPoolRequest(@Nonnull final SessionIdentifier identity, @Nonnull final Type type, final LURI uri, final boolean orCreate, final boolean listed) {
        this(null, identity, type, uri, null, orCreate, listed);
    }

    public VisitPoolRequest(final SessionIdentifier identity, final LURI uri) {
        this(null, identity, Types.T_POOL, uri, null, false, false);
    }

    public VisitPoolRequest() {
        super();
    }

    public VisitPoolRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new VisitPoolRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        //TODO:
        return new ArrayList<AuthorizationRequest>();

        //        if (orCreate) {
        //            return Arrays.asList(new AuthorizationRequest(uri.parent(), Permission.MODIFY));
        //        } else {
        //            return Arrays.asList(new AuthorizationRequest(uri, Permission.VIEW));
        //        }
    }

    public List<String> notificationLocations() {
        if (hasPreviousPool()) {
            return Arrays.asList(uri().asReverseDNSString(), getPreviousPool().asReverseDNSString());
        } else {
            return Arrays.asList(uri().asReverseDNSString());
        }
    }


    @Nonnull
    public RequestType requestType() {
        return RequestType.R_VISIT_POOL;
    }

    @Override
    public boolean shouldNotify() {
        return true;
    }
}