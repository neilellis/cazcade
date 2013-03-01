/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.RequestType;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DeleteUserRequest extends AbstractDeletionRequest {
    public DeleteUserRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidUUID target) {
        super();
        id(id);
        session(identity);
        setTarget(target);
    }

    public DeleteUserRequest(final SessionIdentifier identity, final LiquidUUID target) {
        this(null, identity, target);
    }

    public DeleteUserRequest() {
        super();
    }

    public DeleteUserRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new DeleteUserRequest(getEntity());
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_DELETE_USER;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}
