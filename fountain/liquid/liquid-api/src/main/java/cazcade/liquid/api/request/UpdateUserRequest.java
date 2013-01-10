/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class UpdateUserRequest extends AbstractUpdateRequest {
    public UpdateUserRequest(@Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDTransferEntity entity) {
        super();
        setId(id);
        setSessionId(identity);
        setTarget(target);
        setRequestEntity(entity);
    }

    public UpdateUserRequest(final LiquidSessionIdentifier identity, final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, identity, target, entity);
    }

    public UpdateUserRequest(final LiquidUUID target, final LSDTransferEntity entity) {
        this(null, LiquidSessionIdentifier.ANON, target, entity);
    }

    public UpdateUserRequest() {
        super();
    }

    public UpdateUserRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new UpdateUserRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getTarget(), LiquidPermission.EDIT));
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.UPDATE_USER;
    }

    public boolean isMutationRequest() {
        return true;
    }

    @Override
    public boolean isSecureOperation() {
        return true;
    }
}
