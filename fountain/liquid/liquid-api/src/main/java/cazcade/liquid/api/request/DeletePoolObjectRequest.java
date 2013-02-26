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

public class DeletePoolObjectRequest extends AbstractDeletionRequest {
    public DeletePoolObjectRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, @Nullable final LiquidUUID pool, @Nullable final LiquidUUID target, @Nullable final LiquidURI uri) {
        super();
        id(id);
        session(identity);
        setPoolUUID(pool);
        setTarget(target);
        setUri(uri);
    }

    public DeletePoolObjectRequest(final SessionIdentifier identity, final LiquidUUID pool, final LiquidUUID target) {
        this(null, identity, pool, target, null);
    }

    public DeletePoolObjectRequest(final LiquidURI uri) {
        this(null, SessionIdentifier.ANON, null, null, uri);
    }

    public DeletePoolObjectRequest() {
        super();
    }

    public DeletePoolObjectRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new DeletePoolObjectRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        if (hasUri()) {
            return Arrays.asList(new AuthorizationRequest(session(), uri(), Permission.DELETE_PERM).or(new AuthorizationRequest(session(), uri()
                    .withoutFragment(), Permission.EDIT_PERM)), new AuthorizationRequest(session(), uri().withoutFragment(), Permission.MODIFY_PERM));
        } else {
            return Arrays.asList(new AuthorizationRequest(session(), getTarget(), Permission.DELETE_PERM).or(new AuthorizationRequest(session(), getPoolUUID(), Permission.EDIT_PERM)), new AuthorizationRequest(session(), getPoolUUID(), Permission.MODIFY_PERM));
        }
    }

    public List<String> notificationLocations() {
        if (hasUri()) {
            return Arrays.asList(uri().asReverseDNSString(), uri().withoutFragment().asReverseDNSString());
        } else {
            return Arrays.asList(getPoolUUID().toString(), getTarget().toString());
        }
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.DELETE_POOL_OBJECT;
    }
}
