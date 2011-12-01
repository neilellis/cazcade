package cazcade.fountain.datastore.impl;

import cazcade.fountain.datastore.Node;
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

    //todo: remove all Node based API calls, replace with URI and LSDEntity parameters and return types


    //Pool & object Creation


    @Nonnull
    Node createPoolNoTx(LiquidSessionIdentifier identity, LiquidURI owner, Node parent, String poolName, double x, double y, @Nullable String title, boolean listed) throws InterruptedException;

    @Nonnull
    Node createPoolNoTx(LiquidSessionIdentifier identity, LiquidURI owner, Node parent, LSDType type, String poolName, double x, double y, String title, boolean listed) throws InterruptedException;

    @Nullable
    LSDEntity createPoolObjectTx(Node poolNode, LiquidSessionIdentifier identity, LiquidURI owner, LiquidURI author, LSDEntity entity, LiquidRequestDetailLevel detail, boolean internal, boolean createAuthor) throws Exception;


    //User pool Creation
    void createPoolsForAliasNoTx(LiquidURI aliasURI, String name, String fullName, boolean systemUser) throws InterruptedException;

    void createPoolsForCazcadeAliasNoTx(String name, String fullName, boolean systemUser) throws InterruptedException;

    void createPoolsForUserNoTx(String username) throws InterruptedException;


    //Pool and object Retrieval

    //todo: this should be a utility method not public API
    @Nullable
    LSDEntity convertNodeToEntityWithRelatedEntitiesNoTX(LiquidSessionIdentifier sessionIdentifier, Node pool, @Nullable Node parentNode, LiquidRequestDetailLevel detail, boolean internal, boolean b) throws InterruptedException;

    @Nullable
    LSDEntity getPoolObjectTx(LiquidSessionIdentifier identity, LiquidURI uri, boolean internal, boolean historical, LiquidRequestDetailLevel detail) throws InterruptedException;

    @Nullable
    LSDEntity getPoolAndContentsNoTx(Node targetNode, LiquidRequestDetailLevel detail, boolean contents, ChildSortOrder order, boolean internal, LiquidSessionIdentifier identity, @Nullable Integer start, @Nullable Integer end, boolean historical) throws Exception;


    //Pool and object Update
    @Nullable
    LSDEntity updatePoolObjectNoTx(LiquidSessionIdentifier identity, LiquidSessionIdentifier editor, LSDEntity entity, Node pool, Node origNode, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    LSDEntity linkPoolObjectTx(LiquidSessionIdentifier editor, LiquidURI newOwner, LiquidURI target, LiquidURI to, LiquidRequestDetailLevel detail, boolean internal) throws Exception;

    @Nullable
    LSDEntity getPoolAndContentsNoTx(LiquidURI uri, LiquidRequestDetailLevel detail, ChildSortOrder order, boolean contents, boolean internal, LiquidSessionIdentifier identity, Integer start, Integer end, boolean historical) throws Exception;

    @Nullable
    LSDEntity deletePoolObjectTx(LiquidURI uri, boolean internal, LiquidRequestDetailLevel detail) throws Exception;


    //Misc pool and object actions

    @Nullable
    LSDEntity selectPoolObjectTx(LiquidSessionIdentifier identity, boolean selected, LiquidURI target, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    @Nonnull
    Node movePoolObjectNoTx(LiquidURI object, Double x, Double y, Double z) throws Exception;

    void visitNodeNoTx(Node node, LiquidSessionIdentifier identity) throws InterruptedException;


    //Link and unlink  (unlink is like delete)

    @Nonnull
    Node linkPoolObject(LiquidSessionIdentifier editor, Node newOwner, Node target, Node to) throws InterruptedException;

    @Nonnull
    Node linkPoolObject(LiquidSessionIdentifier editor, Node newOwner, Node target, Node from, Node to) throws InterruptedException;

    @Nonnull
    Node unlinkPoolObject(Node target) throws InterruptedException;

    @Nonnull
    Node linkPool(Node newOwner, Node target, Node to) throws InterruptedException;

    @Nonnull
    Node linkPool(Node newOwner, Node target, Node from, Node to) throws InterruptedException;

    @Nonnull
    Node unlinkPool(Node target) throws InterruptedException;


    //Comments

    @Nonnull
    Node addCommentNoTX(Node commentTargetNode, LSDEntity entity, LiquidURI alias) throws InterruptedException;

    @Nullable
    Collection<LSDEntity> getCommentsTx(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, int max, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    //Utility methods

    void recalculatePoolURIs(Node node) throws InterruptedException;


    @Nullable
    LSDEntity updatePool(LiquidSessionIdentifier sessionIdentifier, Node node, LiquidRequestDetailLevel detail, boolean internal, boolean historical, Integer end, int start, ChildSortOrder order, boolean contents, LSDEntity requestEntity, Runnable onRenameAction) throws Exception;
}
