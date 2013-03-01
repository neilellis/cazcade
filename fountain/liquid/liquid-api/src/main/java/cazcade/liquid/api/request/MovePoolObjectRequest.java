/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class MovePoolObjectRequest extends AbstractRequest {
    public MovePoolObjectRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LURI objectURI, @Nullable final LiquidUUID pool, @Nullable final LiquidUUID object, final Double x, final Double y, final Double z) {
        super();
        id(id);
        session(identity);
        setObjectUUID(object);
        setPoolUUID(pool);
        setX(x);
        setY(y);
        setZ(z);
        setUri(objectURI);
    }

    @Deprecated
    public MovePoolObjectRequest(final SessionIdentifier identity, final LURI objectURI, final LiquidUUID pool, final LiquidUUID object, final Double x, final Double y, final Double z) {
        super();
        throw new UnsupportedOperationException();
        //        this(null, identity, objectURI, pool, object, x, y, z);
    }

    public MovePoolObjectRequest(final LURI objectURI, final Double x, final Double y, final Double z) {
        this(null, SessionIdentifier.ANON, objectURI, null, null, x, y, z);
    }

    public MovePoolObjectRequest() {
        super();
    }

    public MovePoolObjectRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        final LURI uri = uri();
        return new MovePoolObjectRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        if (hasUri()) {
            final LURI uri = uri();
            return Arrays.asList(new AuthorizationRequest(session(), uri.parent(), Permission.P_MODIFY));
        } else {
            final LiquidUUID poolUUID = getPoolUUID();
            return Arrays.asList(new AuthorizationRequest(session(), poolUUID, Permission.P_MODIFY));
        }
    }

    @Override
    public List<String> notificationLocations() {
        if (hasUri()) {
            final LURI uri = uri();
            return Arrays.asList(uri.withoutFragment().asReverseDNSString(), uri.asReverseDNSString());
        } else {
            final LiquidUUID poolUUID = getPoolUUID();
            final LiquidUUID objectUUID = getObjectUUID();
            return Arrays.asList(poolUUID.toString(), objectUUID.toString());
        }
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_MOVE_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }

    @Override
    public boolean shouldSendProvisional() {
        return true;
    }
}