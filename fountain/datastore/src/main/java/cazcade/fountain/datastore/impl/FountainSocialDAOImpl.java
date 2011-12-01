package cazcade.fountain.datastore.impl;

import cazcade.common.Logger;
import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.Relationship;
import cazcade.fountain.datastore.impl.graph.LatestContentFinder;
import cazcade.fountain.index.persistence.dao.BoardDAO;
import cazcade.fountain.index.persistence.entities.BoardIndexEntity;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import org.neo4j.graphdb.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class FountainSocialDAOImpl implements FountainSocialDAO {

    @Nonnull
    private final static Logger log = Logger.getLogger(FountainSocialDAOImpl.class);

    @Autowired
    private FountainNeo fountainNeo;

    @Autowired
    private FountainPoolDAO poolDAO;

    @Autowired
    private FountainUserDAO userDAO;

    @Autowired
    private BoardDAO boardDao;

    @Autowired
    private FountainIndexServiceImpl indexDAO;


    @Nullable
    @Override
    public Collection<LSDEntity> getRosterNoTX(@Nonnull LiquidURI uri, boolean internal, LiquidSessionIdentifier identity, LiquidRequestDetailLevel request) throws InterruptedException {
        fountainNeo.begin();
        try {
            Node node = fountainNeo.findByURI(uri);
            if (node == null) {
                return null;
            }
            return getRosterNoTX(node, internal, identity, request);
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    Collection<LSDEntity> getRosterNoTX(@Nonnull Node poolNode, boolean internal, LiquidSessionIdentifier identity, LiquidRequestDetailLevel detail) {
        ArrayList<LSDEntity> entities = new ArrayList<LSDEntity>();
        Iterable<Relationship> visitingSessions = poolNode.getRelationships(FountainRelationships.VISITING, Direction.INCOMING);
        for (Relationship visitingSession : visitingSessions) {
            try {
                Node sessionNode = visitingSession.getOtherNode(poolNode);
                String updatedStr = sessionNode.getProperty(LSDAttribute.UPDATED);
                if (updatedStr != null) {
                    long updated = sessionNode.getUpdated().getTime();
                    if (updated < (System.currentTimeMillis() - FountainNeo.SESSION_INACTIVE_MILLI)) {
                        sessionNode.setAttribute(LSDAttribute.ACTIVE, false);
                    }
                }
                if (sessionNode.hasAttribute(LSDAttribute.ACTIVE) && sessionNode.getBooleanAttribute(LSDAttribute.ACTIVE)) {
                    Node aliasNode = sessionNode.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING).getOtherNode(sessionNode);
                    //If the alias has no profile image, take it from the profile pool!
                    fountainNeo.putProfileInformationIntoAlias(aliasNode);
                    LSDEntity aliasEntity = aliasNode.convertNodeToLSD(detail, internal);
                    entities.add(aliasEntity);
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
        return entities;
    }

    @Nullable
    @Override
    public Collection<LSDEntity> getRosterNoTX(@Nonnull LiquidUUID target, boolean internal, LiquidSessionIdentifier identity, LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            Node node = fountainNeo.findByUUID(target);
            if (node == null) {
                return null;
            }
            return getRosterNoTX(node, internal, identity, detail);
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public boolean isFollowing(@Nonnull Node currentAlias, Node node) throws InterruptedException {
        fountainNeo.begin();
        try {
            boolean following = false;

            for (Relationship relationship : currentAlias.getRelationships(FountainRelationships.FOLLOW_ALIAS, FountainRelationships.FOLLOW_CONTENT)) {
                if (relationship.getEndNode().equals(node)) {
                    following = true;
                }

            }
            return following;
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public LSDEntity followResourceTX(@Nonnull final LiquidSessionIdentifier sessionIdentifier, @Nonnull final LiquidURI uri, final LiquidRequestDetailLevel detail, final boolean internal) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<LSDEntity>() {
            @Nullable
            @Override
            public LSDEntity call() throws Exception {
                Node currentAlias = fountainNeo.findByURI(sessionIdentifier.getAlias(), true);
                Node resourceToFollow = fountainNeo.findByURI(uri, true);

                deltaFollowersCount(resourceToFollow, 1);
                indexDAO.syncFollowerCount(resourceToFollow);


                if (uri.getScheme() == LiquidURIScheme.alias) {
                    if (!isFollowing(currentAlias, resourceToFollow)) {
                        currentAlias.createRelationshipTo(resourceToFollow, FountainRelationships.FOLLOW_ALIAS);
                        deltaFollowsAliasCount(currentAlias, 1);
                    }
                    return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
                } else {
                    if (!isFollowing(currentAlias, resourceToFollow)) {
                        currentAlias.createRelationshipTo(resourceToFollow, FountainRelationships.FOLLOW_CONTENT);
                        deltaFollowsResourcesCount(currentAlias, 1);
                    }
                    indexDAO.syncFollowCounts(currentAlias);
                    return poolDAO.getPoolObjectTx(sessionIdentifier, uri, internal, false, detail);
                }

            }
        });
    }

    private void deltaFollowersCount(@Nonnull Node node, int delta) {
        deltaCount(LSDAttribute.FOLLOWERS_COUNT, node, delta);
    }

    private void deltaFollowsAliasCount(@Nonnull Node node, int delta) {
        deltaCount(LSDAttribute.FOLLOWS_ALIAS_COUNT, node, delta);
    }

    private void deltaFollowsResourcesCount(@Nonnull Node node, int delta) {
        deltaCount(LSDAttribute.FOLLOWS_RESOURCES_COUNT, node, delta);
    }

    private void deltaCount(@Nonnull LSDAttribute attribute, @Nonnull Node resourceToFollow, int delta) {
        int followerCount;
        String attributeKey = attribute.getKeyName();
        if (resourceToFollow.hasAttribute(attribute)) {
            followerCount = resourceToFollow.getIntegerAttribute(attribute);
        } else {
            followerCount = 0;
        }
        log.debug("Count is now {0} for {1}", followerCount + delta, attribute);
        resourceToFollow.setAttribute(attribute, followerCount + delta);
    }

    @Override
    public LSDEntity unfollowResourceTX(@Nonnull final LiquidSessionIdentifier sessionIdentifier, @Nonnull final LiquidURI uri, final LiquidRequestDetailLevel detail, final boolean internal) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<LSDEntity>() {
            @Nullable
            @Override
            public LSDEntity call() throws Exception {
                Node currentAlias = fountainNeo.findByURI(sessionIdentifier.getAlias(), true);
                Node resourceToFollow = fountainNeo.findByURI(uri, true);
                deltaFollowersCount(resourceToFollow, -1);
                indexDAO.syncFollowerCount(resourceToFollow);

                if (uri.getScheme() == LiquidURIScheme.alias) {
                    for (Relationship relationship : currentAlias.getRelationships(FountainRelationships.FOLLOW_ALIAS, Direction.OUTGOING)) {
                        if (relationship.getOtherNode(currentAlias).equals(resourceToFollow)) {
                            relationship.delete();
                            deltaFollowsAliasCount(currentAlias, -1);
                        }
                    }
                    return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
                } else {
                    for (Relationship relationship : currentAlias.getRelationships(FountainRelationships.FOLLOW_CONTENT, Direction.OUTGOING)) {
                        if (relationship.getOtherNode(currentAlias).equals(resourceToFollow)) {
                            relationship.delete();
                            deltaFollowsResourcesCount(currentAlias, -1);
                        }
                    }
                    indexDAO.syncFollowCounts(currentAlias);
                    return poolDAO.getPoolObjectTx(sessionIdentifier, uri, internal, false, detail);
                }
            }
        });
    }

    @Override
    public LSDEntity getAliasAsProfileTx(@Nonnull final LiquidSessionIdentifier sessionIdentifier, @Nonnull final LiquidURI uri, final boolean internal, final LiquidRequestDetailLevel detail) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<LSDEntity>() {
            @Nullable
            @Override
            public LSDEntity call() throws Exception {
                return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
            }
        });

    }

    @Override
    public void recordChat(LiquidSessionIdentifier sessionIdentifier, LiquidURI uri, LSDEntity entity) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Nonnull
    @Override
    public ChangeReport getUpdateSummaryForAlias(@Nonnull LiquidURI aliasURI, long since) throws InterruptedException {
        ChangeReport report = new ChangeReport();
        final Node aliasNode = fountainNeo.findByURI(aliasURI);
        final Traverser traverse = aliasNode.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
            @Override
            public boolean isReturnableNode(@Nonnull TraversalPosition currentPos) {
                return currentPos.currentNode().getProperty(LSDAttribute.TYPE.getKeyName()).toString().startsWith(LSDDictionaryTypes.BOARD.asString());
            }
        }, FountainRelationships.FOLLOW_CONTENT, Direction.INCOMING);
        for (org.neo4j.graphdb.Node node : traverse) {
            final Node currNode = new Node(node);
            final long updated = currNode.getUpdated().getTime();
            if (updated > since) {
                report.addChangedFollowedBoard(currNode.convertNodeToLSD(LiquidRequestDetailLevel.NORMAL, true));
            }
        }
        final List<BoardIndexEntity> ownedBoards = boardDao.getMyBoards(0, 10000, aliasURI.asString());
        for (BoardIndexEntity ownedBoard : ownedBoards) {
            if (ownedBoard.getUpdated().getTime() > since) {
                report.addChangedOwnedBoard(fountainNeo.findByURI(new LiquidURI(ownedBoard.getUri())).convertNodeToLSD(LiquidRequestDetailLevel.NORMAL, true));
            }
        }
        final LatestContentFinder latestContentFinder = new LatestContentFinder(new LiquidSessionIdentifier(aliasURI), fountainNeo, aliasNode, since, 25, 5000, LiquidRequestDetailLevel.NORMAL, 50, userDAO);
        report.setLatestChanges(latestContentFinder.getNodes());

        return report;

    }

    @Nullable
    LSDEntity getAliasAsProfile(@Nonnull LiquidSessionIdentifier sessionIdentifier, @Nonnull LiquidURI uri, LiquidRequestDetailLevel detail, boolean internal) throws InterruptedException {
        Node currentAlias = fountainNeo.findByURI(sessionIdentifier.getAlias());
        Node node = fountainNeo.findByURI(uri, true);
        LSDEntity result = node.convertNodeToLSD(detail, internal);

//        int follows = node.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, FountainRelationships.FOLLOW_ALIAS, Direction.OUTGOING).getAllNodes().size();
//        int followers = node.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, FountainRelationships.FOLLOW_ALIAS, Direction.INCOMING).getAllNodes().size();
//        result.setAttribute(LSDAttribute.FOLLOWS_ALIAS_COUNT, String.valueOf(follows));

        boolean following = isFollowing(currentAlias, node);
        result.setAttribute(LSDAttribute.FOLLOWING, following);
        node.setPermissionFlagsOnEntity(sessionIdentifier, null, result);

        return result;
    }


}