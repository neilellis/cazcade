package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.*;
import cazcade.fountain.datastore.impl.graph.LatestContentFinder;
import cazcade.fountain.index.persistence.dao.BoardDAO;
import cazcade.fountain.index.persistence.entities.BoardIndexEntity;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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

    @Override
    public LSDTransferEntity followResourceTX(@Nonnull final LiquidSessionIdentifier sessionIdentifier,
                                              @Nonnull final LiquidURI uri, final LiquidRequestDetailLevel detail,
                                              final boolean internal) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<LSDTransferEntity>() {
            @Nullable
            @Override
            public LSDTransferEntity call() throws Exception {
                final LSDPersistedEntity currentAlias = fountainNeo.findByURI(sessionIdentifier.getAlias(), true);
                final LSDPersistedEntity resourceToFollow = fountainNeo.findByURI(uri, true);

                deltaFollowersCount(resourceToFollow, 1);
                indexDAO.syncFollowerCount(resourceToFollow);


                if (uri.getScheme() == LiquidURIScheme.alias) {
                    if (!isFollowing(currentAlias, resourceToFollow)) {
                        currentAlias.createRelationshipTo(resourceToFollow, FountainRelationships.FOLLOW_ALIAS);
                        deltaFollowsAliasCount(currentAlias, 1);
                    }
                    return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
                }
                else {
                    if (!isFollowing(currentAlias, resourceToFollow)) {
                        currentAlias.createRelationshipTo(resourceToFollow, FountainRelationships.FOLLOW_CONTENT);
                        deltaFollowsResourcesCount(currentAlias, 1);
                    }
                    indexDAO.syncFollowCounts(currentAlias);
                    return poolDAO.getPoolObjectTx(sessionIdentifier, uri, internal, false, detail);
                }
            }
        }
                                                       );
    }

    private void deltaFollowersCount(@Nonnull final LSDPersistedEntity persistedEntity, final int delta) {
        deltaCount(LSDAttribute.FOLLOWERS_COUNT, persistedEntity, delta);
    }

    private void deltaCount(@Nonnull final LSDAttribute attribute, @Nonnull final LSDPersistedEntity resourceToFollow,
                            final int delta) {
        final int followerCount;
        final String attributeKey = attribute.getKeyName();
        if (resourceToFollow.hasAttribute(attribute)) {
            followerCount = resourceToFollow.getIntegerAttribute(attribute);
        }
        else {
            followerCount = 0;
        }
        log.debug("Count is now {0} for {1}", followerCount + delta, attribute);
        resourceToFollow.setAttribute(attribute, followerCount + delta);
    }

    private void deltaFollowsAliasCount(@Nonnull final LSDPersistedEntity persistedEntity, final int delta) {
        deltaCount(LSDAttribute.FOLLOWS_ALIAS_COUNT, persistedEntity, delta);
    }

    @Nullable
    LSDTransferEntity getAliasAsProfile(@Nonnull final LiquidSessionIdentifier sessionIdentifier, @Nonnull final LiquidURI uri,
                                        final LiquidRequestDetailLevel detail, final boolean internal) throws InterruptedException {
        final LSDPersistedEntity currentAlias = fountainNeo.findByURI(sessionIdentifier.getAlias());
        final LSDPersistedEntity persistedEntity = fountainNeo.findByURI(uri, true);
        final LSDTransferEntity result = persistedEntity.convertNodeToLSD(detail, internal);

//        int follows = node.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, FountainRelationships.FOLLOW_ALIAS, Direction.OUTGOING).getAllNodes().size();
//        int followers = node.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, FountainRelationships.FOLLOW_ALIAS, Direction.INCOMING).getAllNodes().size();
//        result.setAttribute(LSDAttribute.FOLLOWS_ALIAS_COUNT, String.valueOf(follows));

        final boolean following = isFollowing(currentAlias, persistedEntity);
        result.setAttribute(LSDAttribute.FOLLOWING, following);
        persistedEntity.setPermissionFlagsOnEntity(sessionIdentifier, null, result);

        return result;
    }

    @Override
    public boolean isFollowing(@Nonnull final LSDPersistedEntity currentAlias, final LSDPersistedEntity persistedEntity)
            throws InterruptedException {
        fountainNeo.begin();
        try {
            boolean following = false;

            for (final FountainRelationship relationship : currentAlias.getRelationships(FountainRelationships.FOLLOW_ALIAS,
                                                                                         FountainRelationships.FOLLOW_CONTENT
                                                                                        )) {
                if (relationship.getEndNode().equals(persistedEntity)) {
                    following = true;
                }
            }
            return following;
        } finally {
            fountainNeo.end();
        }
    }

    private void deltaFollowsResourcesCount(@Nonnull final LSDPersistedEntity persistedEntity, final int delta) {
        deltaCount(LSDAttribute.FOLLOWS_RESOURCES_COUNT, persistedEntity, delta);
    }

    @Override
    public LSDTransferEntity getAliasAsProfileTx(@Nonnull final LiquidSessionIdentifier sessionIdentifier,
                                                 @Nonnull final LiquidURI uri, final boolean internal,
                                                 final LiquidRequestDetailLevel detail) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<LSDTransferEntity>() {
            @Nullable
            @Override
            public LSDTransferEntity call() throws Exception {
                return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
            }
        }
                                                       );
    }

    @Nullable
    @Override
    public Collection<LSDBaseEntity> getRosterNoTX(@Nonnull final LiquidURI uri, final boolean internal,
                                                   final LiquidSessionIdentifier identity, final LiquidRequestDetailLevel request)
            throws InterruptedException {
        fountainNeo.begin();
        try {
            final LSDPersistedEntity persistedEntity = fountainNeo.findByURI(uri);
            if (persistedEntity == null) {
                return null;
            }
            return getRosterNoTX(persistedEntity, internal, identity, request);
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable
    @Override
    public Collection<LSDBaseEntity> getRosterNoTX(@Nonnull final LiquidUUID target, final boolean internal,
                                                   final LiquidSessionIdentifier identity, final LiquidRequestDetailLevel detail)
            throws InterruptedException {
        fountainNeo.begin();
        try {
            final LSDPersistedEntity persistedEntity = fountainNeo.findByUUID(target);
            if (persistedEntity == null) {
                return null;
            }
            return getRosterNoTX(persistedEntity, internal, identity, detail);
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    @Override
    public ChangeReport getUpdateSummaryForAlias(@Nonnull final LiquidURI aliasURI, final long since) throws InterruptedException {
        final ChangeReport report = new ChangeReport();
        final LSDPersistedEntity aliasPersistedEntity = fountainNeo.findByURI(aliasURI);
        final Traverser traverse = aliasPersistedEntity.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE,
                                                                 new ReturnableEvaluator() {
                                                                     @Override
                                                                     public boolean isReturnableNode(
                                                                             @Nonnull final TraversalPosition currentPos) {
                                                                         return currentPos.currentNode()
                                                                                          .getProperty(
                                                                                                  LSDAttribute.TYPE.getKeyName()
                                                                                                      )
                                                                                          .toString()
                                                                                          .startsWith(LSDDictionaryTypes.BOARD
                                                                                                              .asString()
                                                                                                     );
                                                                     }
                                                                 }, FountainRelationships.FOLLOW_CONTENT, Direction.INCOMING
                                                                );
        for (final org.neo4j.graphdb.Node node : traverse) {
            final LSDPersistedEntity currPersistedEntity = new FountainEntityImpl(node);
            final long updated = currPersistedEntity.getUpdated().getTime();
            if (updated > since) {
                report.addChangedFollowedBoard(currPersistedEntity.convertNodeToLSD(LiquidRequestDetailLevel.NORMAL, true));
            }
        }
        final List<BoardIndexEntity> ownedBoards = boardDao.getMyBoards(0, 10000, aliasURI.asString());
        for (final BoardIndexEntity ownedBoard : ownedBoards) {
            if (ownedBoard.getUpdated().getTime() > since) {
                report.addChangedOwnedBoard(fountainNeo.findByURI(new LiquidURI(ownedBoard.getUri())).convertNodeToLSD(
                        LiquidRequestDetailLevel.NORMAL, true
                                                                                                                      )
                                           );
            }
        }
        final LatestContentFinder latestContentFinder = new LatestContentFinder(new LiquidSessionIdentifier(aliasURI), fountainNeo,
                                                                                aliasPersistedEntity, since, 25, 5000,
                                                                                LiquidRequestDetailLevel.NORMAL, 50, userDAO
        );
        report.setLatestChanges(latestContentFinder.getNodes());

        return report;
    }

    @Override
    public void recordChat(final LiquidSessionIdentifier sessionIdentifier, final LiquidURI uri, final LSDBaseEntity entity) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public LSDTransferEntity unfollowResourceTX(@Nonnull final LiquidSessionIdentifier sessionIdentifier,
                                                @Nonnull final LiquidURI uri, final LiquidRequestDetailLevel detail,
                                                final boolean internal) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<LSDTransferEntity>() {
            @Nullable
            @Override
            public LSDTransferEntity call() throws Exception {
                final LSDPersistedEntity currentAlias = fountainNeo.findByURI(sessionIdentifier.getAlias(), true);
                final LSDPersistedEntity resourceToFollow = fountainNeo.findByURI(uri, true);
                deltaFollowersCount(resourceToFollow, -1);
                indexDAO.syncFollowerCount(resourceToFollow);

                if (uri.getScheme() == LiquidURIScheme.alias) {
                    for (final FountainRelationship relationship : currentAlias.getRelationships(FountainRelationships.FOLLOW_ALIAS,
                                                                                                 Direction.OUTGOING
                                                                                                )) {
                        if (relationship.getOtherNode(currentAlias).equals(resourceToFollow)) {
                            relationship.delete();
                            deltaFollowsAliasCount(currentAlias, -1);
                        }
                    }
                    return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
                }
                else {
                    for (final FountainRelationship relationship : currentAlias.getRelationships(
                            FountainRelationships.FOLLOW_CONTENT, Direction.OUTGOING
                                                                                                )) {
                        if (relationship.getOtherNode(currentAlias).equals(resourceToFollow)) {
                            relationship.delete();
                            deltaFollowsResourcesCount(currentAlias, -1);
                        }
                    }
                    indexDAO.syncFollowCounts(currentAlias);
                    return poolDAO.getPoolObjectTx(sessionIdentifier, uri, internal, false, detail);
                }
            }
        }
                                                       );
    }

    @Nonnull
    Collection<LSDBaseEntity> getRosterNoTX(@Nonnull final LSDPersistedEntity poolPersistedEntity, final boolean internal,
                                            final LiquidSessionIdentifier identity, final LiquidRequestDetailLevel detail) {
        final ArrayList<LSDBaseEntity> entities = new ArrayList<LSDBaseEntity>();
        final Iterable<FountainRelationship> visitingSessions = poolPersistedEntity.getRelationships(FountainRelationships.VISITING,
                                                                                                     Direction.INCOMING
                                                                                                    );
        for (final FountainRelationship visitingSession : visitingSessions) {
            try {
                final LSDPersistedEntity sessionPersistedEntity = visitingSession.getOtherNode(poolPersistedEntity);
                if (sessionPersistedEntity.getUpdated() != null) {
                    final long updated = sessionPersistedEntity.getUpdated().getTime();
                    if (updated < System.currentTimeMillis() - FountainNeoImpl.SESSION_INACTIVE_MILLI) {
                        sessionPersistedEntity.setAttribute(LSDAttribute.ACTIVE, false);
                    }
                }
                if (sessionPersistedEntity.hasAttribute(LSDAttribute.ACTIVE) && sessionPersistedEntity.getBooleanAttribute(
                        LSDAttribute.ACTIVE
                                                                                                                          )) {
                    final LSDPersistedEntity aliasPersistedEntity = sessionPersistedEntity.getSingleRelationship(
                            FountainRelationships.OWNER, Direction.OUTGOING
                                                                                                                ).getOtherNode(
                            sessionPersistedEntity
                                                                                                                              );
                    //If the alias has no profile image, take it from the profile pool!
                    fountainNeo.putProfileInformationIntoAlias(aliasPersistedEntity);
                    final LSDBaseEntity aliasEntity = aliasPersistedEntity.convertNodeToLSD(detail, internal);
                    entities.add(aliasEntity);
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
        return entities;
    }
}