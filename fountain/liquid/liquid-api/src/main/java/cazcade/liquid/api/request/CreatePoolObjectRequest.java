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
    public CreatePoolObjectRequest(final SessionIdentifier authenticatedUser, final LURI uri, final TransferEntity entity, final LURI authorURI) {
        this(authenticatedUser, null, uri, null, entity, authorURI);
    }

    public CreatePoolObjectRequest(@Nonnull final SessionIdentifier identity, @Nullable final LiquidUUID pool, @Nullable final LURI uri, @Nullable final LiquidUUID id, final TransferEntity entity, @Nullable final LURI authorURI) {
        super();
        setUri(uri);
        id(id);
        setAuthor(authorURI);
        session(identity);
        setPoolUUID(pool);
        setRequestEntity(entity);
    }

    public CreatePoolObjectRequest(final SessionIdentifier authenticatedUser, final LiquidUUID pool, final TransferEntity entity, final LURI authorURI) {
        this(authenticatedUser, pool, null, null, entity, authorURI);
    }

    public CreatePoolObjectRequest(@Nonnull final SessionIdentifier session, final LURI uri, final TransferEntity entity) {
        this(session, null, uri, null, entity, session.alias());
    }

    public CreatePoolObjectRequest(final LURI uri, final TransferEntity entity) {
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
            return Arrays.asList(new AuthorizationRequest(session(), uri(), Permission.P_MODIFY));
        } else {
            return Arrays.asList(new AuthorizationRequest(session(), getPoolUUID(), Permission.P_MODIFY));
        }
    }

    public List<String> notificationLocations() {
        if (hasRequestEntity() && !hasResponse()) {
            TransferEntity requestEntity = request();
            if (requestEntity.hasURI() && requestEntity.has(Dictionary.NAME) || requestEntity.hasId()) {
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
        return RequestType.R_CREATE_POOL_OBJECT;
    }
}
