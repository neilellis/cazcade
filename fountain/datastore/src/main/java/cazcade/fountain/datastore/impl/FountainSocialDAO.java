package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.lsd.LSDEntity;
import org.neo4j.graphdb.Node;

import java.util.Collection;
import java.util.List;

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

    /**
     * Performs some action on each user in the system, there is no guarantees of sequence or timeliness here, just that
     * it will be executed eventually. This is a readonly method.
     *
     * @param callback the callback to call.
     */
    void forEachUser(UserCallback callback);

    List<LSDEntity> getUpdateSummaryForAlias(LiquidURI aliasURI, long since);

    interface UserCallback {
        /**
         * @param userEntity  the entity for the user.
         * @param aliasEntity the entity for the primary alias node.
         */
        void process(LSDEntity userEntity, LSDEntity aliasEntity);
    }
}
