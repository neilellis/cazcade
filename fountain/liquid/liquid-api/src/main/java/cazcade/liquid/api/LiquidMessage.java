/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api;

import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.SerializedRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author neilellis@cazcade.com
 */
public interface LiquidMessage extends Serializable {

    @Nonnull SerializedRequest asSerializedRequest();


    /**
     * Makes a copy of this message, but <b>note</b> that the origin is reset to UNASSIGNED.
     *
     * @return a copy of this message.
     */
    @Nonnull LiquidMessage copy();

    /**
     * TODO: Deprecate this, it's used by the GWT Bus only.
     *
     * @return
     */
    @Deprecated Collection<LiquidURI> affectedEntities();

    @Nonnull String cacheIdentifier();

    CachingScope cachingScope();

    @Nonnull String deduplicationIdentifier();

    @Nonnull LiquidUUID id();

    @Nonnull LiquidMessageType messageType();

    LiquidMessageOrigin origin();

    @Nonnull TransferEntity request();

    @Nonnull TransferEntity response();

    /**
     * If there is a response entity returns that otherwise returns the original entity from the request.
     */
    @Nullable Entity getResponseOrRequestEntity();

    LiquidMessageState getState();

    boolean isCacheable();

    void setCachingScope(CachingScope cachingScope);

    void id(LiquidUUID id);

    void origin(LiquidMessageOrigin origin);


    void response(TransferEntity entity);

    void state(LiquidMessageState status);

    boolean hasResponse();

    boolean hasId();
}
