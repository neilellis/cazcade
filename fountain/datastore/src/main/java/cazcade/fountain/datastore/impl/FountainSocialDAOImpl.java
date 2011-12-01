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
    private static final Logger log = Logger.getLogger(FountainSocialDAOImpl.class);

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
    public Collection<LSDEntity> getRosterNoTX(@Nonnull final LiquidURI uri, final boolean internal, final LiquidSessionIdentifier identity, final LiquidRequestDetailLevel request) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Node node = fountainNeo.findByURI(uri);
            if (node == null) {
                return null;
            }
            return getRosterNoTX(node, internal, identity, request);
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    Collection<LSDEntity> getRosterNoTX(@Nonnull final Node poolNode, final boolean internal, final LiquidSessionIdentifier identity, final LiquidRequestDetailLevel detail) {
        final ArrayList<LSDEntity> entities = new ArrayList<LSDEntity>();
        final Iterable<Relationship> visitingSessions = poolNode.getRelationships(FountainRelationships.VISITING, Direction.INCOMING);
        for (final Relationship visitingSession : visitingSessions) {
            try {
                final Node sessionNode = visitingSession.getOtherNode(poolNode);
                final String updatedStr = sessionNode.getProperty(LSDAttribute.UPDATED);
                if (updatedStr != null) {
                    final long updated = sessionNode.getUpdated().getTime();
                    if (updated < System.currentTimeMillis() - FountainNeo.SESSION_INACTIVE_MILLI) {
                        sessionNode.setAttribute(LSDAttribute.ACTIVE, false);
                    }
                }
                if (sessionNode.hasAttribute(LSDAttribute.ACTIVE) && sessionNode.getBooleanAttribute(LSDAttribute.ACTIVE)) {
                    final Node aliasNode = sessionNode.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING).getOtherNode(sessionNode);
                    //If the alias has no profile image, take it from the profile pool!
                    fountainNeo.putProfileInformationIntoAlias(aliasNode);
                    final LSDEntity aliasEntity = aliasNode.convertNodeToLSD(detail, internal);
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
    public Collection<LSDEntity> getRosterNoTX(@Nonnull final LiquidUUID target, final boolean internal, final LiquidSessionIdentifier identity, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Node node = fountainNeo.findByUUID(target);
            if (node == null) {
                return null;
            }
            return getRosterNoTX(node, internal, identity, detail);
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public boolean isFollowing(@Nonnull final Node currentAlias, final Node node) throws InterruptedException {
        fountainNeo.begin();
        try {
            boolean following = false;

            for (final Relationship relationship : currentAlias.getRelationships(FountainRelationships.FOLLOW_ALIAS, FountainRelationships.FOLLOW_CONTENT)) {
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
                final Node currentAlias = fountainNeo.findByURI(sessionIdentifier.getAlias(), true);
                final Node resourceToFollow = fountainNeo.findByURI(uri, true);

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

    private void deltaFollowersCount(@Nonnull final Node node, final int delta) {
        deltaCount(LSDAttribute.FOLLOWERS_COUNT, node, delta);
    }

    private void deltaFollowsAliasCount(@Nonnull final Node node, final int delta) {
        deltaCount(LSDAttribute.FOLLOWS_ALIAS_COUNT, node, delta);
    }

    private void deltaFollowsResourcesCount(@Nonnull final Node node, final int delta) {
        deltaCount(LSDAttribute.FOLLOWS_RESOURCES_COUNT, node, delta);
    }

    private void deltaCount(@Nonnull final LSDAttribute attribute, @Nonnull final Node resourceToFollow, final int delta) {
        final int followerCount;
        final String attributeKey = attribute.getKeyName();
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
                final Node currentAlias = fountainNeo.findByURI(sessionIdentifier.getAlias(), true);
                final Node resourceToFollow = fountainNeo.findByURI(uri, true);
                deltaFollowersCount(resourceToFollow, -1);
                indexDAO.syncFollowerCount(resourceToFollow);

                if (uri.getScheme() == LiquidURIScheme.alias) {
                    for (final Relationship relationship : currentAlias.getRelationships(FountainRelationships.FOLLOW_ALIAS, Direction.OUTGOING)) {
                        if (relationship.getOtherNode(currentAlias).equals(resourceToFollow)) {
                            relationship.delete();
                            deltaFollowsAliasCount(currentAlias, -1);
                        }
                    }
                    return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
                } else {
                    for (final Relationship relationship : currentAlias.getRelationships(FountainRelationships.FOLLOW_CONTENT, Direction.OUTGOING)) {
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
    public void recordChat(final LiquidSessionIdentifier sessionIdentifier, final LiquidURI uri, final LSDEntity entity) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Nonnull
    @Override
    public ChangeReport getUpdateSummaryForAlias(@Nonnull final LiquidURI aliasURI, final long since) throws InterruptedException {
        final ChangeReport report = new ChangeReport();
        final Node aliasNode = fountainNeo.findByURI(aliasURI);
        final Traverser traverse = aliasNode.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
            @Override
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return currentPos.currentNode().getProperty(LSDAttribute.TYPE.getKeyName()).toString().startsWith(LSDDictionaryTypes.BOARD.asString());
            }
        }, FountainRelationships.FOLLOW_CONTENT, Direction.INCOMING);
        for (final org.neo4j.graphdb.Node node : traverse) {
            final Node currNode = new Node(node);
            final long updated = currNode.getUpdated().getTime();
            if (updated > since) {
                report.addChangedFollowedBoard(currNode.convertNodeToLSD(LiquidRequestDetailLevel.NORMAL, true));
            }
        }
        final List<BoardIndexEntity> ownedBoards = boardDao.getMyBoards(0, 10000, aliasURI.asString());
        for (final BoardIndexEntity ownedBoard : ownedBoards) {
            if (ownedBoard.getUpdated().getTime() > since) {
                report.addChangedOwnedBoard(fountainNeo.findByURI(new LiquidURI(ownedBoard.getUri())).convertNodeToLSD(LiquidRequestDetailLevel.NORMAL, true));
            }
        }
        final LatestContentFinder latestContentFinder = new LatestContentFinder(new LiquidSessionIdentifier(aliasURI), fountainNeo, aliasNode, since, 25, 5000, LiquidRequestDetailLevel.NORMAL, 50, userDAO);
        report.setLatestChanges(latestContentFinder.getNodes());

        return report;

    }

    @Nullable
    LSDEntity getAliasAsProfile(@Nonnull final LiquidSessionIdentifier sessionIdentifier, @Nonnull final LiquidURI uri, final LiquidRequestDetailLevel detail, final boolean internal) throws InterruptedException {
        final Node currentAlias = fountainNeo.findByURI(sessionIdentifier.getAlias());
        final Node node = fountainNeo.findByURI(uri, true);
        final LSDEntity result = node.convertNodeToLSD(detail, internal);

//        int follows = node.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, FountainRelationships.FOLLOW_ALIAS, Direction.OUTGOING).getAllNodes().size();
//        int followers = node.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, FountainRelationships.FOLLOW_ALIAS, Direction.INCOMING).getAllNodes().size();
//        result.setAttribute(LSDAttribute.FOLLOWS_ALIAS_COUNT, String.valueOf(follows));

        final boolean following = isFollowing(currentAlias, node);
        result.setAttribute(LSDAttribute.FOLLOWING, following);
        node.setPermissionFlagsOnEntity(sessionIdentifier, null, result);

        return result;
    }


}