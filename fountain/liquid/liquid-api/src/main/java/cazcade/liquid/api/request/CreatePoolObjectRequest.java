/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CreatePoolObjectRequest extends AbstractCreationRequest {
    public CreatePoolObjectRequest(final SessionIdentifier authenticatedUser, final LiquidURI uri, final TransferEntity entity, final LiquidURI authorURI) {
        this(authenticatedUser, null, uri, null, entity, authorURI);
    }

    public CreatePoolObjectRequest(@Nonnull final SessionIdentifier identity, @Nullable final LiquidUUID pool, @Nullable final LiquidURI uri, @Nullable final LiquidUUID id, final TransferEntity entity, @Nullable final LiquidURI authorURI) {
        super();
        setUri(uri);
        id(id);
        setAuthor(authorURI);
        session(identity);
        setPoolUUID(pool);
        setRequestEntity(entity);
    }

    public CreatePoolObjectRequest(final SessionIdentifier authenticatedUser, final LiquidUUID pool, final TransferEntity entity, final LiquidURI authorURI) {
        this(authenticatedUser, pool, null, null, entity, authorURI);
    }

    public CreatePoolObjectRequest(@Nonnull final SessionIdentifier authenticatedUser, final LiquidURI uri, final TransferEntity entity) {
        this(authenticatedUser, null, uri, null, entity, authenticatedUser.alias());
    }

    public CreatePoolObjectRequest(final LiquidURI uri, final TransferEntity entity) {
        this(SessionIdentifier.ANON, null, uri, null, entity, null);
    }

    public CreatePoolObjectRequest() {
        super();
    }

    public CreatePoolObjectRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new CreatePoolObjectRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        if (hasUri()) {
            return Arrays.asList(new AuthorizationRequest(session(), uri(), Permission.MODIFY_PERM));
        } else {
            return Arrays.asList(new AuthorizationRequest(session(), getPoolUUID(), Permission.MODIFY_PERM));
        }
    }

    public List<String> notificationLocations() {
        if (hasRequestEntity() && !hasResponse()) {
            TransferEntity requestEntity = request();
            if (requestEntity.hasURI() && requestEntity.has$(Dictionary.NAME) || requestEntity.hasId()) {
                return Arrays.asList(uri().asReverseDNSString(), uri().asReverseDNSString() + "." + requestEntity.nameOrId());
            } else {
                if (requestEntity.hasId()) {
                    return Arrays.asList(getPoolUUID().toString(), requestEntity.id().toString());
                } else {
                    return Arrays.asList(getPoolUUID().toString());
                }
            }
        } else {
            if (hasUri()) {
                return Arrays.asList(uri().asReverseDNSString(), uri().asReverseDNSString() + "." + response().nameOrId());
            } else {
                return Arrays.asList(getPoolUUID().toString(), response().id().toString());
            }
        }
    }

    @Nullable
    public LiquidUUID getPool() {
        return getPoolUUID();
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.CREATE_POOL_OBJECT;
    }
}
