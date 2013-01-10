/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChatRequest extends AbstractUpdateRequest {
    public ChatRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, @Nullable final LiquidUUID target, @Nullable final LiquidURI uri, final LSDTransferEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setRequestEntity(entity);
        setUri(uri);
    }

    public ChatRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, identity, target, null, entity);
    }

    public ChatRequest(final LiquidSessionIdentifier identity, final LiquidURI uri, final LSDTransferEntity entity) {
        this(null, identity, null, uri, entity);
    }


    public ChatRequest(final LiquidURI uri, final String value) {
        super();
        final LSDTransferEntity requestEntity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.CHAT);
        //Time clocks vary so we don't want this set.
        requestEntity.remove(LSDAttribute.PUBLISHED);
        requestEntity.setAttribute(LSDAttribute.TEXT_BRIEF, value);
        setRequestEntity(requestEntity);
        setUri(uri);
    }

    public ChatRequest() {
        super();
    }

    public ChatRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Override
    public void adjustTimeStampForServerTime() {
        super.adjustTimeStampForServerTime();
        if (hasRequestEntity()) {
            getEntity().setAttribute(LSDAttribute.REQUEST_ENTITY, LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        }
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new ChatRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        if (getUri().getScheme() == LiquidURIScheme.alias) {
            return Collections.EMPTY_LIST;
        }
        else {
            return hasTarget()
                   ? Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getTarget(), LiquidPermission.VIEW))
                   : Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getUri(), LiquidPermission.VIEW));
        }
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CHAT;
    }
}
