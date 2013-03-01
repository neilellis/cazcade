/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CreatePoolRequest extends AbstractCreationRequest {
    public CreatePoolRequest(@Nonnull final Types type, @Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LURI parent, final String name, final String title, final String description, final double x, final double y) {
        super();
        setTitle(title);
        setDescription(description);
        setX(x);
        setY(y);
        id(id);
        session(identity);
        setParent(parent);
        setName(name);
        setType(type);
    }

    public CreatePoolRequest(@Nonnull final Types type, @Nullable final LiquidUUID id, @Nonnull final SessionIdentifier identity, final LURI parent, final String name, final String title, final String description, final double x, final double y, final String imageUrl) {
        super();
        setTitle(title);
        setDescription(description);
        setX(x);
        setY(y);
        id(id);
        session(identity);
        setParent(parent);
        setName(name);
        setType(type);
        setImageUrl(imageUrl);
    }


    public CreatePoolRequest(final SessionIdentifier identity, final LURI parent, final String name, final String title, final String description, final double x, final double y) {
        this(Types.T_POOL2D, null, identity, parent, name, title, description, x, y);
    }

    public CreatePoolRequest(final SessionIdentifier identity, final LURI parent, final String name, final String title, final String description, final double x, final double y, String imageUrl) {
        this(Types.T_POOL2D, null, identity, parent, name, title, description, x, y, imageUrl);
    }

    public CreatePoolRequest(@Nonnull final Types type, final LURI parent, final String name, final String title, final String description, final double x, final double y) {
        this(type, null, SessionIdentifier.ANON, parent, name, title, description, x, y);
    }

    public CreatePoolRequest(@Nonnull final Types type, final LURI parent, final String name, final String title, final String description, final double x, final double y, final String imageUrl) {
        this(type, null, SessionIdentifier.ANON, parent, name, title, description, x, y, imageUrl);
    }


    public CreatePoolRequest() {
        super();
    }

    public CreatePoolRequest(final TransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new CreatePoolRequest(getEntity());
    }

    @Nonnull
    public Collection<LURI> affectedEntities() {
        return getStandardAffectedEntitiesInternalPlus(getParent());
    }

    @Nonnull
    public List<AuthorizationRequest> authorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(session(), getParent(), Permission.P_MODIFY));
    }

    @Nonnull
    public RequestType requestType() {
        return RequestType.R_CREATE_POOL;
    }
}
