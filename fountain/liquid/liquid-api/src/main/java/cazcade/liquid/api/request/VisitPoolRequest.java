/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.lsd.LSDType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VisitPoolRequest extends AbstractRetrievalRequest {
    private VisitPoolRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, @Nonnull final LSDType type, final LiquidURI uri, @Nullable final LiquidURI previous, final boolean orCreate, final int max, final boolean listed, @Nullable final LiquidPermissionChangeType permission, @Nullable final String imageUrl) {
        super();
        setOrCreate(orCreate);
        setListed(listed);
        setPermission(permission);
        setId(id);
        setSessionId(identity);
        setUri(uri);
        setPreviousPool(previous);
        setMax(max);
        setPoolType(type);
        if (imageUrl != null) {
            setImageUrl(imageUrl);
        }
    }

    private VisitPoolRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, @Nonnull final LSDType type, final LiquidURI uri, @Nullable final LiquidURI previous, final boolean orCreate, final boolean listed) {
        this(id, identity, type, uri, previous, orCreate, 60, listed, null, null);
    }

    public VisitPoolRequest(@Nonnull final LSDType type, final LiquidURI uri, final LiquidURI previous, final boolean orCreate, final boolean listed, final LiquidPermissionChangeType permission, final String imageUrl) {
        this(null, LiquidSessionIdentifier.ANON, type, uri, previous, orCreate, 60, listed, permission, imageUrl);
    }

    public VisitPoolRequest(@Nonnull final LSDType type, final LiquidURI uri, final LiquidURI previous, final boolean orCreate, final boolean listed) {
        this(null, LiquidSessionIdentifier.ANON, type, uri, previous, orCreate, listed);
    }

    public VisitPoolRequest(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LSDType type, final LiquidURI uri, final boolean orCreate, final boolean listed) {
        this(null, identity, type, uri, null, orCreate, listed);
    }

    public VisitPoolRequest(final LiquidSessionIdentifier identity, final LiquidURI uri) {
        this(null, identity, LSDDictionaryTypes.POOL, uri, null, false, false);
    }

    public VisitPoolRequest() {
        super();
    }

    public VisitPoolRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new VisitPoolRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        //TODO:
        return new ArrayList<AuthorizationRequest>();

        //        if (orCreate) {
        //            return Arrays.asList(new AuthorizationRequest(uri.getParentURI(), LiquidPermission.MODIFY));
        //        } else {
        //            return Arrays.asList(new AuthorizationRequest(uri, LiquidPermission.VIEW));
        //        }
    }

    public List<String> getNotificationLocations() {
        if (hasPreviousPool()) {
            return Arrays.asList(getUri().asReverseDNSString(), getPreviousPool().asReverseDNSString());
        }
        else {
            return Arrays.asList(getUri().asReverseDNSString());
        }
    }


    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.VISIT_POOL;
    }

    @Override
    public boolean shouldNotify() {
        return true;
    }
}