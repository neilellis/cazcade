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

public class AddCommentRequest extends AbstractUpdateRequest {
    public AddCommentRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, @Nullable final LiquidUUID target, @Nullable final LiquidURI uri, @Nonnull final TransferEntity entity) {
        super();
        id(id);
        session(identity);
        setTarget(target);
        setRequestEntity(entity);
        setUri(uri);
    }

    public AddCommentRequest(final SessionIdentifier identity, final LiquidUUID target, final TransferEntity entity) {
        this(null, identity, target, null, entity);
    }

    public AddCommentRequest(final SessionIdentifier identity, final LiquidURI uri, final TransferEntity entity) {
        this(null, identity, null, uri, entity);
    }


    public AddCommentRequest(final LiquidURI uri, final String text) {
        super();
        final SimpleEntity<? extends TransferEntity> requestEntity = SimpleEntity.create(Types.T_COMMENT);
        requestEntity.remove(Dictionary.PUBLISHED);
        requestEntity.$(Dictionary.TEXT_BRIEF, text);
        setRequestEntity(requestEntity);
        //Time clocks vary so we don't want this set.
        setUri(uri);
    }

    public AddCommentRequest() {
        super();
    }

    public AddCommentRequest(final TransferEntity entity) {
        super(entity);
    }

    @Override
    public void adjustTimeStampForServerTime() {
        super.adjustTimeStampForServerTime();
        getEntity().$(Dictionary.REQUEST_ENTITY, Dictionary.PUBLISHED, String.valueOf(System.currentTimeMillis()));
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new AddCommentRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        if (uri().scheme() == LiquidURIScheme.alias) {
            return Collections.EMPTY_LIST;
        } else {
            return hasTarget()
                   ? Arrays.asList(new AuthorizationRequest(session(), getTarget(), Permission.VIEW_PERM))
                   : Arrays.asList(new AuthorizationRequest(session(), uri(), Permission.VIEW_PERM));
        }
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.ADD_COMMENT;
    }
}
