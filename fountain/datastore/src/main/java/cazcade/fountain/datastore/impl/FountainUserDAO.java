package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;
import org.neo4j.graphdb.Node;

import java.io.UnsupportedEncodingException;

/**
 * Note the Tx suffix means that a transaction is created for the method call.
 *
 * @author neilellis@cazcade.com
 */
public interface FountainUserDAO {


    LSDEntity unlinkAliasTX(LiquidSessionIdentifier identity, LiquidUUID target, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    //Migrate from Node based methods, parameters should include URIs and return LSDEntity where appropriate

    LSDEntity getAliasFromNode(Node node, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;

//    Node createAlias(LSDEntity entity) throws InterruptedException;

    Node createUser(LSDEntity user, boolean systemUser) throws InterruptedException, UnsupportedEncodingException;

    Node createAlias(Node userNode, LSDEntity entity, boolean me, boolean orupdate, boolean claim, boolean systemUser) throws InterruptedException;

    Node createSession(LiquidURI aliasUri, ClientApplicationIdentifier clientApplicationIdentifier) throws InterruptedException;

    void addAuthorToNodeNoTX(LiquidURI author, boolean createAuthor, Node node) throws InterruptedException;

    /**
     * Performs some action on each user in the system, there is no guarantees of sequence or timeliness here, just that
     * it will be executed eventually. This is a readonly method.
     *
     * @param callback the callback to call.
     */
    void forEachUser(UserCallback callback);


    interface UserCallback {
        /**
         * @param userEntity  the entity for the user.
         * @param aliasEntity the entity for the primary alias node.
         */
        void process(LSDEntity userEntity, LSDEntity aliasEntity) throws InterruptedException;
    }


}
