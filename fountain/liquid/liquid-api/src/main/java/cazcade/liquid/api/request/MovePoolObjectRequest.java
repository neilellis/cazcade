/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class MovePoolObjectRequest extends AbstractRequest {
    public MovePoolObjectRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidURI objectURI, @Nullable final LiquidUUID pool, @Nullable final LiquidUUID object, final Double x, final Double y, final Double z) {
        super();
        setId(id);
        setSessionId(identity);
        setObjectUUID(object);
        setPoolUUID(pool);
        setX(x);
        setY(y);
        setZ(z);
        setUri(objectURI);
    }

    @Deprecated
    public MovePoolObjectRequest(final LiquidSessionIdentifier identity, final LiquidURI objectURI, final LiquidUUID pool, final LiquidUUID object, final Double x, final Double y, final Double z) {
        super();
        throw new UnsupportedOperationException();
        //        this(null, identity, objectURI, pool, object, x, y, z);
    }

    public MovePoolObjectRequest(final LiquidURI objectURI, final Double x, final Double y, final Double z) {
        this(null, LiquidSessionIdentifier.ANON, objectURI, null, null, x, y, z);
    }

    public MovePoolObjectRequest() {
        super();
    }

    public MovePoolObjectRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        final LiquidURI uri = getUri();
        return new MovePoolObjectRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (hasUri()) {
            final LiquidURI uri = getUri();
            return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), uri.getParentURI(), LiquidPermission.MODIFY));
        }
        else {
            final LiquidUUID poolUUID = getPoolUUID();
            return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), poolUUID, LiquidPermission.MODIFY));
        }
    }

    @Override
    public List<String> getNotificationLocations() {
        if (hasUri()) {
            final LiquidURI uri = getUri();
            return Arrays.asList(uri.getWithoutFragment().asReverseDNSString(), uri.asReverseDNSString());
        }
        else {
            final LiquidUUID poolUUID = getPoolUUID();
            final LiquidUUID objectUUID = getObjectUUID();
            return Arrays.asList(poolUUID.toString(), objectUUID.toString());
        }
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.MOVE_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }

    @Override
    public boolean shouldSendProvisional() {
        return true;
    }
}