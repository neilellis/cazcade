package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.lsd.LSDType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainPoolDAO {

    //todo: remove all LSDPersistedEntity based API calls, replace with URI and LSDTransferEntity parameters and return types


    //Pool & object Creation


    @Nonnull
    LSDPersistedEntity createPoolNoTx(LiquidSessionIdentifier identity, LiquidURI owner, LSDPersistedEntity parent, String poolName, double x, double y, @Nullable String title, boolean listed) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity createPoolNoTx(LiquidSessionIdentifier identity, LiquidURI owner, LSDPersistedEntity parent, LSDType type, String poolName, double x, double y, String title, boolean listed) throws InterruptedException;

    @Nullable
    LSDTransferEntity createPoolObjectTx(LSDPersistedEntity poolPersistedEntity, LiquidSessionIdentifier identity, LiquidURI owner, LiquidURI author, LSDTransferEntity entity, LiquidRequestDetailLevel detail, boolean internal, boolean createAuthor) throws Exception;


    //User pool Creation
    void createPoolsForAliasNoTx(LiquidURI aliasURI, String name, String fullName, boolean systemUser) throws InterruptedException;

    void createPoolsForCazcadeAliasNoTx(String name, String fullName, boolean systemUser) throws InterruptedException;

    void createPoolsForUserNoTx(String username) throws InterruptedException;


    //Pool and object Retrieval

    //todo: this should be a utility method not public API
    @Nullable
    LSDTransferEntity convertNodeToEntityWithRelatedEntitiesNoTX(LiquidSessionIdentifier sessionIdentifier, LSDPersistedEntity pool, @Nullable LSDPersistedEntity parentPersistedEntity, LiquidRequestDetailLevel detail, boolean internal, boolean b) throws InterruptedException;

    @Nullable
    LSDTransferEntity getPoolObjectTx(LiquidSessionIdentifier identity, LiquidURI uri, boolean internal, boolean historical, LiquidRequestDetailLevel detail) throws InterruptedException;

    @Nullable
    LSDTransferEntity getPoolAndContentsNoTx(LSDPersistedEntity targetPersistedEntity, LiquidRequestDetailLevel detail, boolean contents, ChildSortOrder order, boolean internal, LiquidSessionIdentifier identity, @Nullable Integer start, @Nullable Integer end, boolean historical) throws Exception;


    //Pool and object Update
    @Nullable
    LSDTransferEntity updatePoolObjectNoTx(LiquidSessionIdentifier identity, LiquidSessionIdentifier editor, LSDTransferEntity entity, LSDPersistedEntity pool, LSDPersistedEntity origPersistedEntity, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    LSDTransferEntity linkPoolObjectTx(LiquidSessionIdentifier editor, LiquidURI newOwner, LiquidURI target, LiquidURI to, LiquidRequestDetailLevel detail, boolean internal) throws Exception;

    @Nullable
    LSDTransferEntity getPoolAndContentsNoTx(LiquidURI uri, LiquidRequestDetailLevel detail, ChildSortOrder order, boolean contents, boolean internal, LiquidSessionIdentifier identity, Integer start, Integer end, boolean historical) throws Exception;

    @Nullable
    LSDTransferEntity deletePoolObjectTx(LiquidURI uri, boolean internal, LiquidRequestDetailLevel detail) throws Exception;


    //Misc pool and object actions

    @Nullable
    LSDTransferEntity selectPoolObjectTx(LiquidSessionIdentifier identity, boolean selected, LiquidURI target, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    @Nonnull
    LSDPersistedEntity movePoolObjectNoTx(LiquidURI object, Double x, Double y, Double z) throws Exception;

    void visitNodeNoTx(LSDPersistedEntity persistedEntityImpl, LiquidSessionIdentifier identity) throws InterruptedException;


    //Link and unlink  (unlink is like delete)

    @Nonnull
    LSDPersistedEntity linkPoolObject(LiquidSessionIdentifier editor, LSDPersistedEntity newOwner, LSDPersistedEntity target, LSDPersistedEntity to) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity linkPoolObject(LiquidSessionIdentifier editor, LSDPersistedEntity newOwner, LSDPersistedEntity target, LSDPersistedEntity from, LSDPersistedEntity to) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity unlinkPoolObject(LSDPersistedEntity target) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity linkPool(LSDPersistedEntity newOwner, LSDPersistedEntity target, LSDPersistedEntity to) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity linkPool(LSDPersistedEntity newOwner, LSDPersistedEntity target, LSDPersistedEntity from, LSDPersistedEntity to) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity unlinkPool(LSDPersistedEntity target) throws InterruptedException;


    //Comments

    @Nonnull
    LSDPersistedEntity addCommentNoTX(LSDPersistedEntity commentTargetPersistedEntity, LSDTransferEntity entity, LiquidURI alias) throws InterruptedException;

    @Nullable
    Collection<LSDBaseEntity> getCommentsTx(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, int max, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    //Utility methods

    void recalculatePoolURIs(LSDPersistedEntity persistedEntity) throws InterruptedException;


    @Nullable
    LSDTransferEntity updatePool(LiquidSessionIdentifier sessionIdentifier, LSDPersistedEntity persistedEntityImpl, LiquidRequestDetailLevel detail, boolean internal, boolean historical, Integer end, int start, ChildSortOrder order, boolean contents, LSDTransferEntity requestEntity, Runnable onRenameAction) throws Exception;
}
