/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.RequestDetailLevel;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.lsd.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainPoolDAO {
    //Comments

    @Nonnull
    PersistedEntity addCommentNoTX(@Nullable PersistedEntity commentEntity, @Nullable TransferEntity commentTransferEntity, LiquidURI alias) throws InterruptedException;


    //Pool and object Retrieval

    //todo: this should be a utility method not public API
    @Nonnull
    TransferEntity convertNodeToEntityWithRelatedEntitiesNoTX(SessionIdentifier sessionIdentifier, PersistedEntity pool, @Nullable PersistedEntity parentPersistedEntity, RequestDetailLevel detail, boolean internal, boolean b) throws Exception;

    @Nonnull
    PersistedEntity createPoolNoTx(SessionIdentifier identity, LiquidURI owner, PersistedEntity parent, Type type, String poolName, double x, double y, String title, boolean listed) throws InterruptedException;

    //todo: remove all PersistedEntity based API calls, replace with URI and TransferEntity parameters and return types

    //Pool & object Creation
    @Nonnull
    PersistedEntity createPoolNoTx(SessionIdentifier identity, LiquidURI owner, PersistedEntity parent, String poolName, double x, double y, @Nullable String title, boolean listed) throws InterruptedException;

    @Nonnull
    TransferEntity createPoolObjectTx(PersistedEntity pool, SessionIdentifier identity, LiquidURI owner, LiquidURI author, TransferEntity entity, RequestDetailLevel detail, boolean internal, boolean createAuthor) throws Exception;

    //User pool Creation
    void createPoolsForAliasNoTx(LiquidURI aliasURI, String name, @Nullable String fullName, boolean systemUser) throws InterruptedException;

    void createPoolsForCazcadeAliasNoTx(String name, String fullName, boolean systemUser) throws InterruptedException;

    void createPoolsForUserNoTx(String username) throws InterruptedException;

    @Nonnull TransferEntity deletePoolObjectTx(LiquidURI uri, boolean internal, RequestDetailLevel detail) throws Exception;

    @Nullable
    Collection<TransferEntity> getCommentsTx(SessionIdentifier sessionIdentifier, LiquidURI uri, int max, boolean internal, RequestDetailLevel detail) throws Exception;

    @Nonnull
    TransferEntity getPoolAndContentsNoTx(PersistedEntity targetPersistedEntity, RequestDetailLevel detail, boolean contents, ChildSortOrder order, boolean internal, SessionIdentifier identity, @Nullable Integer start, @Nullable Integer end, boolean historical) throws Exception;

    @Nullable
    TransferEntity getPoolAndContentsNoTx(LiquidURI uri, RequestDetailLevel detail, ChildSortOrder order, boolean contents, boolean internal, SessionIdentifier identity, Integer start, Integer end, boolean historical) throws Exception;

    @Nullable
    TransferEntity getPoolObjectTx(SessionIdentifier identity, LiquidURI uri, boolean internal, boolean historical, RequestDetailLevel detail) throws Exception;

    @Nonnull
    PersistedEntity linkPool(PersistedEntity newOwner, PersistedEntity target, PersistedEntity from, PersistedEntity to) throws Exception;

    @Nonnull PersistedEntity linkPool(PersistedEntity newOwner, PersistedEntity target, PersistedEntity to) throws Exception;

    @Nonnull
    PersistedEntity linkPoolObject(SessionIdentifier editor, PersistedEntity newOwner, PersistedEntity target, PersistedEntity from, PersistedEntity to) throws Exception;


    //Link and unlink  (unlink is like delete)

    @Nonnull
    PersistedEntity linkPoolObject(SessionIdentifier editor, PersistedEntity newOwner, PersistedEntity target, PersistedEntity to) throws Exception;

    TransferEntity linkPoolObjectTx(SessionIdentifier editor, LiquidURI newOwner, LiquidURI target, LiquidURI to, RequestDetailLevel detail, boolean internal) throws Exception;

    @Nonnull PersistedEntity movePoolObjectNoTx(LiquidURI object, Double x, Double y, Double z) throws Exception;


    //Utility methods

    void recalculatePoolURIs(PersistedEntity persistedEntity) throws InterruptedException;


    //Misc pool and object actions

    @Nullable
    TransferEntity selectPoolObjectTx(SessionIdentifier identity, boolean selected, LiquidURI target, boolean internal, RequestDetailLevel detail) throws Exception;

    @Nonnull PersistedEntity unlinkPool(PersistedEntity target) throws InterruptedException;

    @Nonnull PersistedEntity unlinkPoolObject(PersistedEntity target) throws InterruptedException;

    @Nonnull
    TransferEntity updatePool(SessionIdentifier sessionIdentifier, PersistedEntity persistedEntityImpl, RequestDetailLevel detail, boolean internal, boolean historical, Integer end, int start, ChildSortOrder order, boolean contents, TransferEntity requestEntity, Runnable onRenameAction) throws Exception;

    //Pool and object Update
    @Nonnull
    TransferEntity updatePoolObjectNoTx(SessionIdentifier identity, SessionIdentifier editor, TransferEntity entity, @Nullable PersistedEntity pool, PersistedEntity origPersistedEntity, boolean internal, RequestDetailLevel detail) throws InterruptedException, Exception;

    void visitNodeNoTx(PersistedEntity entity, SessionIdentifier identity) throws Exception;
}
