/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeletePoolRequest extends AbstractDeletionRequest {
    public DeletePoolRequest(final LiquidUUID id, final SessionIdentifier identity, final LiquidUUID target, final LiquidURI uri) {
        super();
        id(id);
        session(identity);
        setTarget(target);
        setUri(uri);
    }

    public DeletePoolRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidUUID target) {
        super();
        id(id);
        session(identity);
        setTarget(target);
    }

    public DeletePoolRequest(final SessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public DeletePoolRequest(final SessionIdentifier identity, final LiquidURI poolURI) {
        super();
        session(identity);
        setUri(poolURI);
    }


    public DeletePoolRequest() {
        super();
    }

    public DeletePoolRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new DeletePoolRequest(getEntity());
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.DELETE_POOL;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}
