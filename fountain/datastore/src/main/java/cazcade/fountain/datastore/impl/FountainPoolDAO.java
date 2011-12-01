package cazcade.fountain.datastore.impl;

import cazcade.fountain.datastore.FountainEntity;
import cazcade.liquid.api.ChildSortOrder;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainPoolDAO {

    //todo: remove all FountainEntity based API calls, replace with URI and LSDEntity parameters and return types


    //Pool & object Creation


    @Nonnull
    FountainEntity createPoolNoTx(LiquidSessionIdentifier identity, LiquidURI owner, FountainEntity parent, String poolName, double x, double y, @Nullable String title, boolean listed) throws InterruptedException;

    @Nonnull
    FountainEntity createPoolNoTx(LiquidSessionIdentifier identity, LiquidURI owner, FountainEntity parent, LSDType type, String poolName, double x, double y, String title, boolean listed) throws InterruptedException;

    @Nullable
    LSDEntity createPoolObjectTx(FountainEntity poolFountainEntity, LiquidSessionIdentifier identity, LiquidURI owner, LiquidURI author, LSDEntity entity, LiquidRequestDetailLevel detail, boolean internal, boolean createAuthor) throws Exception;


    //User pool Creation
    void createPoolsForAliasNoTx(LiquidURI aliasURI, String name, String fullName, boolean systemUser) throws InterruptedException;

    void createPoolsForCazcadeAliasNoTx(String name, String fullName, boolean systemUser) throws InterruptedException;

    void createPoolsForUserNoTx(String username) throws InterruptedException;


    //Pool and object Retrieval

    //todo: this should be a utility method not public API
    @Nullable
    LSDEntity convertNodeToEntityWithRelatedEntitiesNoTX(LiquidSessionIdentifier sessionIdentifier, FountainEntity pool, @Nullable FountainEntity parentFountainEntity, LiquidRequestDetailLevel detail, boolean internal, boolean b) throws InterruptedException;

    @Nullable
    LSDEntity getPoolObjectTx(LiquidSessionIdentifier identity, LiquidURI uri, boolean internal, boolean historical, LiquidRequestDetailLevel detail) throws InterruptedException;

    @Nullable
    LSDEntity getPoolAndContentsNoTx(FountainEntity targetFountainEntity, LiquidRequestDetailLevel detail, boolean contents, ChildSortOrder order, boolean internal, LiquidSessionIdentifier identity, @Nullable Integer start, @Nullable Integer end, boolean historical) throws Exception;


    //Pool and object Update
    @Nullable
    LSDEntity updatePoolObjectNoTx(LiquidSessionIdentifier identity, LiquidSessionIdentifier editor, LSDEntity entity, FountainEntity pool, FountainEntity origFountainEntity, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    LSDEntity linkPoolObjectTx(LiquidSessionIdentifier editor, LiquidURI newOwner, LiquidURI target, LiquidURI to, LiquidRequestDetailLevel detail, boolean internal) throws Exception;

    @Nullable
    LSDEntity getPoolAndContentsNoTx(LiquidURI uri, LiquidRequestDetailLevel detail, ChildSortOrder order, boolean contents, boolean internal, LiquidSessionIdentifier identity, Integer start, Integer end, boolean historical) throws Exception;

    @Nullable
    LSDEntity deletePoolObjectTx(LiquidURI uri, boolean internal, LiquidRequestDetailLevel detail) throws Exception;


    //Misc pool and object actions

    @Nullable
    LSDEntity selectPoolObjectTx(LiquidSessionIdentifier identity, boolean selected, LiquidURI target, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    @Nonnull
    FountainEntity movePoolObjectNoTx(LiquidURI object, Double x, Double y, Double z) throws Exception;

    void visitNodeNoTx(FountainEntity fountainEntityImpl, LiquidSessionIdentifier identity) throws InterruptedException;


    //Link and unlink  (unlink is like delete)

    @Nonnull
    FountainEntity linkPoolObject(LiquidSessionIdentifier editor, FountainEntity newOwner, FountainEntity target, FountainEntity to) throws InterruptedException;

    @Nonnull
    FountainEntity linkPoolObject(LiquidSessionIdentifier editor, FountainEntity newOwner, FountainEntity target, FountainEntity from, FountainEntity to) throws InterruptedException;

    @Nonnull
    FountainEntity unlinkPoolObject(FountainEntity target) throws InterruptedException;

    @Nonnull
    FountainEntity linkPool(FountainEntity newOwner, FountainEntity target, FountainEntity to) throws InterruptedException;

    @Nonnull
    FountainEntity linkPool(FountainEntity newOwner, FountainEntity target, FountainEntity from, FountainEntity to) throws InterruptedException;

    @Nonnull
    FountainEntity unlinkPool(FountainEntity target) throws InterruptedException;


    //Comments

    @Nonnull
    FountainEntity addCommentNoTX(FountainEntity commentTargetFountainEntity, LSDEntity entity, LiquidURI alias) throws InterruptedException;

    @Nullable
    Collection<LSDEntity> getCommentsTx(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, int max, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    //Utility methods

    void recalculatePoolURIs(FountainEntity fountainEntity) throws InterruptedException;


    @Nullable
    LSDEntity updatePool(LiquidSessionIdentifier sessionIdentifier, FountainEntity fountainEntityImpl, LiquidRequestDetailLevel detail, boolean internal, boolean historical, Integer end, int start, ChildSortOrder order, boolean contents, LSDEntity requestEntity, Runnable onRenameAction) throws Exception;
}
