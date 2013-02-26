/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.RequestDetailLevel;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainSocialDAO {
    TransferEntity followResourceTX(SessionIdentifier sessionIdentifier, LiquidURI uri, RequestDetailLevel detail, boolean internal) throws Exception;

    TransferEntity getAliasAsProfileTx(SessionIdentifier sessionIdentifier, LiquidURI uri, boolean internal, RequestDetailLevel detail) throws Exception;

    @Nullable
    Collection<Entity> getRosterNoTX(LiquidURI uri, boolean internal, SessionIdentifier identity, RequestDetailLevel request) throws InterruptedException;

    @Nullable
    Collection<Entity> getRosterNoTX(LiquidUUID target, boolean internal, SessionIdentifier identity, RequestDetailLevel detail) throws InterruptedException;


    @Nonnull ChangeReport getUpdateSummaryForAlias(LiquidURI aliasURI, long since) throws Exception;

    boolean isFollowing(PersistedEntity currentAlias, PersistedEntity persistedEntity) throws InterruptedException;

    void recordChat(SessionIdentifier sessionIdentifier, LiquidURI uri, Entity entity);

    TransferEntity unfollowResourceTX(SessionIdentifier sessionIdentifier, LiquidURI uri, RequestDetailLevel detail, boolean internal) throws Exception;
}
