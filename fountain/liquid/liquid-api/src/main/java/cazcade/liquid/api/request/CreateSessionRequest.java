/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;


public class CreateSessionRequest extends AbstractCreationRequest {
    public CreateSessionRequest(@Nullable final LiquidUUID id, @Nullable final LiquidURI alias, @Nonnull final ClientApplicationIdentifier client) {
        super();
        id(id);
        setUri(alias);
        setClient(client);
    }

    public CreateSessionRequest(final LiquidURI alias, @Nonnull final ClientApplicationIdentifier client) {
        this(null, alias, client);
    }

    public CreateSessionRequest(@Nonnull final ClientApplicationIdentifier client) {
        this(null, null, client);
    }

    public CreateSessionRequest() {
        super();
    }

    public CreateSessionRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new CreateSessionRequest(getEntity());
    }

    public Collection<LiquidURI> affectedEntities() {
        return Arrays.asList(uri());
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.CREATE_SESSION;
    }

    @Override
    public boolean isAsyncRequest() {
        return false;
    }
}