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

public class ResizePoolObjectRequest extends AbstractRequest {
    public ResizePoolObjectRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidURI objectURI, @Nullable final LiquidUUID pool, @Nullable final LiquidUUID object, final Integer width, final Integer height) {
        super();
        setWidth(width);
        setHeight(height);
        id(id);
        session(identity);
        setObjectUUID(object);
        setPoolUUID(pool);
        setUri(objectURI);
    }

    public ResizePoolObjectRequest(final SessionIdentifier identity, final LiquidUUID pool, final LiquidUUID object, final Integer width, final Integer height, final LiquidURI objectURI) {
        this(null, identity, objectURI, pool, object, width, height);
    }


    public ResizePoolObjectRequest(final LiquidURI objectURI, final Integer width, final Integer height) {
        this(null, SessionIdentifier.ANON, objectURI, null, null, width, height);
    }

    public ResizePoolObjectRequest() {
        super();
    }

    public ResizePoolObjectRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new ResizePoolObjectRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        if (!hasUri()) {
            return Arrays.asList(new AuthorizationRequest(session(), getPoolUUID(), Permission.MODIFY_PERM));
        } else {
            return Arrays.asList(new AuthorizationRequest(session(), uri().parent(), Permission.MODIFY_PERM));
        }
    }

    public List<String> notificationLocations() {
        if (!hasUri()) {
            return Arrays.asList(getPoolUUID().toString(), getObjectUUID().toString());
        } else {
            return Arrays.asList(uri().parent().asReverseDNSString(), uri().asReverseDNSString());
        }
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.RESIZE_POOL_OBJECT;
    }

    public boolean isMutationRequest() {
        return true;
    }
}