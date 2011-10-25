package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDType;
import org.neo4j.graphdb.Node;

import java.util.Collection;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainPoolDAO {

    //todo: remove all Node based API calls, replace with URI and LSDEntity parameters and return types


    //Pool & object Creation


    Node createPoolNoTx(LiquidSessionIdentifier identity, LiquidURI owner, Node parent, String poolName, double x, double y, String title, boolean listed) throws InterruptedException;

    Node createPoolNoTx(LiquidSessionIdentifier identity, LiquidURI owner, Node parent, LSDType type, String poolName, double x, double y, String title, boolean listed) throws InterruptedException;

    LSDEntity createPoolObjectTx(Node poolNode, LiquidSessionIdentifier identity, LiquidURI owner, LiquidURI author, LSDEntity entity, LiquidRequestDetailLevel detail, boolean internal, boolean createAuthor) throws Exception;


    //User pool Creation
    void createPoolsForAliasNoTx(LiquidURI aliasURI, String name, String fullName, boolean systemUser) throws InterruptedException;

    void createPoolsForCazcadeAliasNoTx(String name, String fullName, boolean systemUser) throws InterruptedException;

    void createPoolsForUserNoTx(String username) throws InterruptedException;


    //Pool and object Retrieval

    //todo: this should be a utility method not public API
    LSDEntity convertNodeToEntityWithRelatedEntitiesNoTX(LiquidSessionIdentifier sessionIdentifier, Node pool, Node parentNode, LiquidRequestDetailLevel detail, boolean internal, boolean b) throws InterruptedException;

    LSDEntity getPoolObjectTx(LiquidSessionIdentifier identity, LiquidURI uri, boolean internal, boolean historical, LiquidRequestDetailLevel detail) throws InterruptedException;

    LSDEntity getPoolAndContentsNoTx(Node targetNode, LiquidRequestDetailLevel detail, boolean contents, ChildSortOrder order, boolean internal, LiquidSessionIdentifier identity, Integer start, Integer end, boolean historical) throws Exception;


    //Pool and object Update
    LSDEntity updatePoolObjectNoTx(LiquidSessionIdentifier identity, LiquidSessionIdentifier editor, LSDEntity entity, Node pool, Node origNode, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    LSDEntity linkPoolObjectTx(LiquidSessionIdentifier editor, LiquidURI newOwner, LiquidURI target, LiquidURI to, LiquidRequestDetailLevel detail, boolean internal) throws Exception;

    LSDEntity getPoolAndContentsNoTx(LiquidURI uri, LiquidRequestDetailLevel detail, ChildSortOrder order, boolean contents, boolean internal, LiquidSessionIdentifier identity, Integer start, Integer end, boolean historical) throws Exception;

    LSDEntity deletePoolObjectTx(LiquidURI uri, boolean internal, LiquidRequestDetailLevel detail) throws Exception;


    //Misc pool and object actions

    LSDEntity selectPoolObjectTx(LiquidSessionIdentifier identity, boolean selected, LiquidURI target, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    Node movePoolObjectNoTx(LiquidURI object, Double x, Double y, Double z) throws Exception;

    void visitNodeNoTx(Node node, LiquidSessionIdentifier identity) throws InterruptedException;


    //Link and unlink  (unlink is like delete)

    Node linkPoolObject(LiquidSessionIdentifier editor, Node newOwner, Node target, Node to) throws InterruptedException;

    Node linkPoolObject(LiquidSessionIdentifier editor, Node newOwner, Node target, Node from, Node to) throws InterruptedException;

    Node unlinkPoolObject(Node target) throws InterruptedException;

    Node linkPool(Node newOwner, Node target, Node to) throws InterruptedException;

    Node linkPool(Node newOwner, Node target, Node from, Node to) throws InterruptedException;

    Node unlinkPool(Node target) throws InterruptedException;


    //Comments

    Node addCommentNoTX(Node commentTargetNode, LSDEntity entity, LiquidURI alias) throws InterruptedException;

    Collection<LSDEntity> getCommentsTx(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, int max, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    //Utility methods

    void recalculatePoolURIs(Node node) throws InterruptedException;


    LSDEntity updatePool(LiquidSessionIdentifier sessionIdentifier, Node node, LiquidRequestDetailLevel detail, boolean internal, boolean historical, Integer end, int start, ChildSortOrder order, boolean contents, LSDEntity requestEntity, Runnable onRenameAction) throws Exception;
}
