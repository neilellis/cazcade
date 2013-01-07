package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.lsd.LSDType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainPoolDAO {
    //Comments

    @Nonnull
    LSDPersistedEntity addCommentNoTX(@Nullable LSDPersistedEntity commentEntity, @Nullable LSDTransferEntity commentTransferEntity, LiquidURI alias)
            throws InterruptedException;


    //Pool and object Retrieval

    //todo: this should be a utility method not public API
    @Nonnull
    LSDTransferEntity convertNodeToEntityWithRelatedEntitiesNoTX(LiquidSessionIdentifier sessionIdentifier, LSDPersistedEntity pool,
                                                                 @Nullable LSDPersistedEntity parentPersistedEntity,
                                                                 LiquidRequestDetailLevel detail, boolean internal, boolean b)
            throws InterruptedException;

    @Nonnull
    LSDPersistedEntity createPoolNoTx(LiquidSessionIdentifier identity, LiquidURI owner, LSDPersistedEntity parent, LSDType type,
                                      String poolName, double x, double y, String title, boolean listed)
            throws InterruptedException;

    //todo: remove all LSDPersistedEntity based API calls, replace with URI and LSDTransferEntity parameters and return types


    //Pool & object Creation
    @Nonnull
    LSDPersistedEntity createPoolNoTx(LiquidSessionIdentifier identity, LiquidURI owner, LSDPersistedEntity parent, String poolName,
                                      double x, double y, @Nullable String title, boolean listed) throws InterruptedException;

    @Nonnull
    LSDTransferEntity createPoolObjectTx(LSDPersistedEntity pool, LiquidSessionIdentifier identity, LiquidURI owner,
                                         LiquidURI author, LSDTransferEntity entity, LiquidRequestDetailLevel detail,
                                         boolean internal, boolean createAuthor) throws Exception;


    //User pool Creation
    void createPoolsForAliasNoTx(LiquidURI aliasURI, String name, @Nullable String fullName, boolean systemUser) throws InterruptedException;

    void createPoolsForCazcadeAliasNoTx(String name, String fullName, boolean systemUser) throws InterruptedException;

    void createPoolsForUserNoTx(String username) throws InterruptedException;

    @Nonnull
    LSDTransferEntity deletePoolObjectTx(LiquidURI uri, boolean internal, LiquidRequestDetailLevel detail) throws Exception;

    @Nullable
    Collection<LSDTransferEntity> getCommentsTx(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, int max, boolean internal,
                                                LiquidRequestDetailLevel detail) throws InterruptedException;

    @Nonnull
    LSDTransferEntity getPoolAndContentsNoTx(LSDPersistedEntity targetPersistedEntity, LiquidRequestDetailLevel detail,
                                             boolean contents, ChildSortOrder order, boolean internal,
                                             LiquidSessionIdentifier identity, @Nullable Integer start, @Nullable Integer end,
                                             boolean historical) throws Exception;

    @Nullable
    LSDTransferEntity getPoolAndContentsNoTx(LiquidURI uri, LiquidRequestDetailLevel detail, ChildSortOrder order, boolean contents,
                                             boolean internal, LiquidSessionIdentifier identity, Integer start, Integer end,
                                             boolean historical) throws Exception;

    @Nullable
    LSDTransferEntity getPoolObjectTx(LiquidSessionIdentifier identity, LiquidURI uri, boolean internal, boolean historical,
                                      LiquidRequestDetailLevel detail) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity linkPool(LSDPersistedEntity newOwner, LSDPersistedEntity target, LSDPersistedEntity from,
                                LSDPersistedEntity to) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity linkPool(LSDPersistedEntity newOwner, LSDPersistedEntity target, LSDPersistedEntity to)
            throws InterruptedException;

    @Nonnull
    LSDPersistedEntity linkPoolObject(LiquidSessionIdentifier editor, LSDPersistedEntity newOwner, LSDPersistedEntity target,
                                      LSDPersistedEntity from, LSDPersistedEntity to) throws InterruptedException;


    //Link and unlink  (unlink is like delete)

    @Nonnull
    LSDPersistedEntity linkPoolObject(LiquidSessionIdentifier editor, LSDPersistedEntity newOwner, LSDPersistedEntity target,
                                      LSDPersistedEntity to) throws InterruptedException;


    LSDTransferEntity linkPoolObjectTx(LiquidSessionIdentifier editor, LiquidURI newOwner, LiquidURI target, LiquidURI to,
                                       LiquidRequestDetailLevel detail, boolean internal) throws Exception;


    @Nonnull
    LSDPersistedEntity movePoolObjectNoTx(LiquidURI object, Double x, Double y, Double z) throws Exception;


    //Utility methods

    void recalculatePoolURIs(LSDPersistedEntity persistedEntity) throws InterruptedException;


    //Misc pool and object actions

    @Nullable
    LSDTransferEntity selectPoolObjectTx(LiquidSessionIdentifier identity, boolean selected, LiquidURI target, boolean internal,
                                         LiquidRequestDetailLevel detail) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity unlinkPool(LSDPersistedEntity target) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity unlinkPoolObject(LSDPersistedEntity target) throws InterruptedException;


    @Nonnull
    LSDTransferEntity updatePool(LiquidSessionIdentifier sessionIdentifier, LSDPersistedEntity persistedEntityImpl,
                                 LiquidRequestDetailLevel detail, boolean internal, boolean historical, Integer end, int start,
                                 ChildSortOrder order, boolean contents, LSDTransferEntity requestEntity, Runnable onRenameAction)
            throws Exception;


    //Pool and object Update
    @Nonnull
    LSDTransferEntity updatePoolObjectNoTx(LiquidSessionIdentifier identity, LiquidSessionIdentifier editor,
                                           LSDTransferEntity entity, @Nullable LSDPersistedEntity pool,
                                           LSDPersistedEntity origPersistedEntity, boolean internal,
                                           LiquidRequestDetailLevel detail) throws InterruptedException;

    void visitNodeNoTx(LSDPersistedEntity entity, LiquidSessionIdentifier identity) throws InterruptedException;
}
