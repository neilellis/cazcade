/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.SimpleEntity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatRequest extends AbstractUpdateRequest {
    public ChatRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, @Nullable final LiquidUUID target, @Nullable final LURI uri, final TransferEntity entity) {
        super();
        id(id);
        session(identity);
        setTarget(target);
        setRequestEntity(entity);
        setUri(uri);
    }

    public ChatRequest(final SessionIdentifier identity, final LiquidUUID target, final TransferEntity entity) {
        this(null, identity, target, null, entity);
    }

    public ChatRequest(final SessionIdentifier identity, final LURI uri, final TransferEntity entity) {
        this(null, identity, null, uri, entity);
    }


    public ChatRequest(final LURI uri, final String value) {
        super();
        final SimpleEntity<? extends TransferEntity> requestEntity = SimpleEntity.create(Types.T_CHAT);
        //Time clocks vary so we don't want this set.
        requestEntity.remove(Dictionary.PUBLISHED);
        requestEntity.$(Dictionary.TEXT_BRIEF, value);
        setRequestEntity(requestEntity);
        setUri(uri);
    }

    public ChatRequest() {
        super();
    }

    public ChatRequest(final TransferEntity entity) {
        super(entity);
    }

    @Override
    public void adjustTimeStampForServerTime() {
        super.adjustTimeStampForServerTime();
        if (hasRequestEntity()) {
            getEntity().$(Dictionary.REQUEST_ENTITY, Dictionary.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        }
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new ChatRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        if (uri().scheme() == LiquidURIScheme.alias) {
            return Collections.EMPTY_LIST;
        } else {
            return hasTarget()
                   ? Arrays.asList(new AuthorizationRequest(session(), getTarget(), Permission.P_VIEW))
                   : Arrays.asList(new AuthorizationRequest(session(), uri(), Permission.P_VIEW));
        }
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_CHAT;
    }
}
