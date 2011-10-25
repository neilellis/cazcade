package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDEntity;
import org.neo4j.graphdb.Node;

import java.util.Collection;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainSocialDAO {
    Collection<LSDEntity> getRosterNoTX(LiquidURI uri, boolean internal, LiquidSessionIdentifier identity, LiquidRequestDetailLevel request) throws InterruptedException;

    Collection<LSDEntity> getRosterNoTX(LiquidUUID target, boolean internal, LiquidSessionIdentifier identity, LiquidRequestDetailLevel detail) throws InterruptedException;

    boolean isFollowing(Node currentAlias, Node node) throws InterruptedException;

    LSDEntity followResourceTX(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, LiquidRequestDetailLevel detail, boolean internal) throws Exception;

    LSDEntity unfollowResourceTX(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, LiquidRequestDetailLevel detail, boolean internal) throws Exception;

    LSDEntity getAliasAsProfileTx(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, boolean internal, LiquidRequestDetailLevel detail) throws Exception;

    void recordChat(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, LSDEntity entity);
}
