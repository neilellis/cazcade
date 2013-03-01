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

public class UpdateUserRequest extends AbstractUpdateRequest {
    public UpdateUserRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LiquidUUID target, final TransferEntity entity) {
        super();
        id(id);
        session(identity);
        setTarget(target);
        setRequestEntity(entity);
    }

    public UpdateUserRequest(final SessionIdentifier identity, final LiquidUUID target, final TransferEntity entity) {
        this(null, identity, target, entity);
    }

    public UpdateUserRequest(final LiquidUUID target, final TransferEntity entity) {
        this(null, SessionIdentifier.ANON, target, entity);
    }

    public UpdateUserRequest() {
        super();
    }

    public UpdateUserRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new UpdateUserRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(session(), getTarget(), Permission.P_EDIT));
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_UPDATE_USER;
    }

    public boolean isMutationRequest() {
        return true;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}
