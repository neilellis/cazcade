/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.request;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CreatePoolRequest extends AbstractCreationRequest {
    public CreatePoolRequest(@Nonnull final LSDDictionaryTypes type, @Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidURI parent, final String name, final String title, final String description, final double x, final double y) {
        super();
        setTitle(title);
        setDescription(description);
        setX(x);
        setY(y);
        setId(id);
        setSessionId(identity);
        setParent(parent);
        setName(name);
        setType(type);
    }

    public CreatePoolRequest(@Nonnull final LSDDictionaryTypes type, @Nullable final LiquidUUID id, @Nonnull final LiquidSessionIdentifier identity, final LiquidURI parent, final String name, final String title, final String description, final double x, final double y, final String imageUrl) {
        super();
        setTitle(title);
        setDescription(description);
        setX(x);
        setY(y);
        setId(id);
        setSessionId(identity);
        setParent(parent);
        setName(name);
        setType(type);
        setImageUrl(imageUrl);
    }


    public CreatePoolRequest(final LiquidSessionIdentifier identity, final LiquidURI parent, final String name, final String title, final String description, final double x, final double y) {
        this(LSDDictionaryTypes.POOL2D, null, identity, parent, name, title, description, x, y);
    }

    public CreatePoolRequest(final LiquidSessionIdentifier identity, final LiquidURI parent, final String name, final String title, final String description, final double x, final double y, String imageUrl) {
        this(LSDDictionaryTypes.POOL2D, null, identity, parent, name, title, description, x, y, imageUrl);
    }

    public CreatePoolRequest(@Nonnull final LSDDictionaryTypes type, final LiquidURI parent, final String name, final String title, final String description, final double x, final double y) {
        this(type, null, LiquidSessionIdentifier.ANON, parent, name, title, description, x, y);
    }

    public CreatePoolRequest(@Nonnull final LSDDictionaryTypes type, final LiquidURI parent, final String name, final String title, final String description, final double x, final double y, final String imageUrl) {
        this(type, null, LiquidSessionIdentifier.ANON, parent, name, title, description, x, y, imageUrl);
    }


    public CreatePoolRequest() {
        super();
    }

    public CreatePoolRequest(final LSDTransferEntity entity) {
        super(entity);
    }

    @Nonnull @Override
    public LiquidMessage copy() {
        return new CreatePoolRequest(getEntity());
    }

    @Nonnull
    public Collection<LiquidURI> getAffectedEntities() {
        return getStandardAffectedEntitiesInternalPlus(getParent());
    }

    @Nonnull
    public List<AuthorizationRequest> getAuthorizationRequests() {
        return Arrays.asList(new AuthorizationRequest(getSessionIdentifier(), getParent(), LiquidPermission.MODIFY));
    }

    @Nonnull
    public LiquidRequestType getRequestType() {
        return LiquidRequestType.CREATE_POOL;
    }
}
