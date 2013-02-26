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

public class UpdatePoolObjectRequest extends AbstractUpdateRequest {
    protected UpdatePoolObjectRequest(final LiquidUUID id, final SessionIdentifier identity, final LiquidUUID pool, final LiquidUUID target, final LiquidURI uri, final TransferEntity entity) {
        super();
        id(id);
        session(identity);
        setTarget(target);
        setPoolUUID(pool);
        setRequestEntity(entity);
        setUri(uri);
    }

    /**
     * @deprecated use URIs where possible.
     */

    public UpdatePoolObjectRequest(@Nullable final LiquidUUID id, final SessionIdentifier identity, final LiquidUUID pool, final LiquidUUID target, final TransferEntity entity) {
        super();
        id(id);
        session(identity);
        setTarget(target);
        setPoolUUID(pool);
        setRequestEntity(entity);
    }

    /**
     * @deprecated use URIs where possible.
     */

    public UpdatePoolObjectRequest(final SessionIdentifier identity, final LiquidUUID pool, final LiquidUUID target, final TransferEntity entity) {
        this(null, identity, pool, target, entity);
    }

    @Deprecated
    public UpdatePoolObjectRequest(final SessionIdentifier identity, final LiquidURI objectURI, final TransferEntity newEntity) {
        super();
        session(identity);
        setUri(objectURI);
        setRequestEntity(newEntity);
    }

    @Deprecated
    public UpdatePoolObjectRequest(final LiquidURI poolURI, @Nonnull final TransferEntity request) {
        super();
        setUri(request.uri());
        setRequestEntity(request);
    }

    public UpdatePoolObjectRequest(@Nonnull final TransferEntity request) {
        super();
        if (!request.hasURI()) {
            throw new IllegalArgumentException("To update a pool object the entity should have a URI");
        }
        setUri(request.uri());
        if (uri().equals(getPoolURI())) {
            throw new IllegalArgumentException(
                    "To update a pool object the entity supplied should be a pool object and have a pool object URI ending in #<object-name> the URI supplied was "
                    + uri());
        }
        setRequestEntity(request);
    }

    public UpdatePoolObjectRequest() {
        super();
    }

    UpdatePoolObjectRequest(final TransferEntity entity, String marker) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new UpdatePoolObjectRequest(getEntity(), "copy constructor");
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        final SessionIdentifier sessionIdentifier = session();
        if (hasUri()) {
            return Arrays.asList(new AuthorizationRequest(sessionIdentifier, getPoolURI(), Permission.EDIT_PERM).or(new AuthorizationRequest(sessionIdentifier, uri(), Permission.EDIT_PERM)
                    .and(new AuthorizationRequest(sessionIdentifier, getPoolURI(), Permission.MODIFY_PERM))));
        } else {
            return Arrays.asList(new AuthorizationRequest(sessionIdentifier, getPoolUUID(), Permission.EDIT_PERM).or(new AuthorizationRequest(sessionIdentifier, getTarget(), Permission.EDIT_PERM)
                    .and(new AuthorizationRequest(sessionIdentifier, getPoolUUID(), Permission.MODIFY_PERM))));
        }
    }

    public List<String> notificationLocations() {
        if (hasUri()) {
            return Arrays.asList(getPoolURI().asReverseDNSString(), uri().asReverseDNSString());
        } else {
            return Arrays.asList(getPoolUUID().toString());
        }
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.UPDATE_POOL_OBJECT;
    }
}
