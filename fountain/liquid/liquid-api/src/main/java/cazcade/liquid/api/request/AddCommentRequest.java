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

public class AddCommentRequest extends AbstractUpdateRequest {
    public AddCommentRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, @Nullable final LiquidUUID target, @Nullable final LiquidURI uri, @Nonnull final LSDTransferEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setRequestEntity(entity);
        setUri(uri);
    }

    public AddCommentRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, identity, target, null, entity);
    }

    public AddCommentRequest(final LiquidSessionIdentifier identity, final LiquidURI uri, final LSDTransferEntity entity) {
        this(null, identity, null, uri, entity);
    }


    public AddCommentRequest(final LiquidURI uri, final String text) {
        super();
        final LSDTransferEntity requestEntity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.COMMENT);
        requestEntity.remove(LSDAttribute.PUBLISHED);
        requestEntity.setAttribute(LSDAttribute.TEXT_BRIEF, text);
        setRequestEntity(requestEntity);
        //Time clocks vary so we don't want this set.
        setUri(uri);
    }

    public AddCommentRequest() {
        super();
    }

    public AddCommentRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Override
    public void adjustTimeStampForServerTime() {
        super.adjustTimeStampForServerTime();
        getEntity().setAttribute(LSDAttribute.REQUEST_ENTITY, LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new AddCommentRequest(getEntity());
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
        return LiquidRequestType.ADD_COMMENT;
    }
}
