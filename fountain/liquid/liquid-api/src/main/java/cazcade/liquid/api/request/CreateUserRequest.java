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
import java.util.Collections;
import java.util.List;

public class CreateUserRequest extends AbstractCreationRequest {
    public CreateUserRequest(@Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final TransferEntity entity) {
        super();
        id(id);
        session(identity);
        setRequestEntity(entity);
    }

    public CreateUserRequest(final SessionIdentifier identity, final TransferEntity entity) {
        this(null, identity, entity);
    }

    public CreateUserRequest() {
        super();
    }

    public CreateUserRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new CreateUserRequest(getEntity());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Collections.EMPTY_LIST;
    }

    @Nullable @Override
    public String notificationSession() {
        //Don't notify anyone of a user creation request.
        return null;
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_CREATE_USER;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }
}
