package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDEntity;
import org.neo4j.graphdb.Node;

/**
 *
 * Note the Tx suffix means that a transaction is created for the method call.
 *
 * @author neilellis@cazcade.com
 */
public interface FountainUserDAO {

    LSDEntity unlinkAliasTX(LiquidSessionIdentifier identity, LiquidUUID target, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    //Migrate from Node based methods, parameters should include URIs and return LSDEntity where appropriate

    LSDEntity getAliasFromNode(Node node, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;

//    Node createAlias(LSDEntity entity) throws InterruptedException;

    Node createUser(LSDEntity user, boolean systemUser) throws InterruptedException;

    Node createAlias(Node userNode, LSDEntity entity, boolean me, boolean orupdate, boolean claim, boolean systemUser) throws InterruptedException;

    Node createSession(LiquidURI aliasUri, ClientApplicationIdentifier clientApplicationIdentifier) throws InterruptedException;

    void addAuthorToNodeNoTX(LiquidURI author, boolean createAuthor, Node node) throws InterruptedException;

}
