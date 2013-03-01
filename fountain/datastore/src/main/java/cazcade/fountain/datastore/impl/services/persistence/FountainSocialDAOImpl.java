/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.*;
import cazcade.fountain.datastore.impl.graph.LatestContentFinder;
import cazcade.fountain.index.persistence.dao.BoardDAO;
import cazcade.fountain.index.persistence.entities.BoardIndexEntity;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Attribute;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import org.neo4j.graphdb.ReturnableEvaluator;
import org.neo4j.graphdb.StopEvaluator;
import org.neo4j.graphdb.TraversalPosition;
import org.neo4j.graphdb.Traverser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import static cazcade.fountain.datastore.impl.FountainRelationships.*;
import static cazcade.liquid.api.lsd.Types.T_BOARD;
import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;

public class FountainSocialDAOImpl implements FountainSocialDAO {
    @Nonnull
    private static final Logger log = Logger.getLogger(FountainSocialDAOImpl.class);
    @Autowired
    private FountainNeo              fountainNeo;
    @Autowired
    private FountainPoolDAO          poolDAO;
    @Autowired
    private FountainUserDAO          userDAO;
    @Autowired
    private BoardDAO                 boardDao;
    @Autowired
    private FountainIndexServiceImpl indexDAO;

    @Override
    public TransferEntity followResourceTX(@Nonnull final SessionIdentifier sessionIdentifier, @Nonnull final LURI uri, final RequestDetailLevel detail, final boolean internal) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<TransferEntity>() {
            @Nullable @Override
            public TransferEntity call() throws Exception {
                final PersistedEntity currentAlias = fountainNeo.findOrFail(sessionIdentifier.alias());
                final PersistedEntity resourceToFollow = fountainNeo.findOrFail(uri);

                deltaFollowersCount(resourceToFollow, 1);
                indexDAO.syncFollowerCount(resourceToFollow);


                if (uri.scheme() == LiquidURIScheme.alias) {
                    if (!isFollowing(currentAlias, resourceToFollow)) {
                        currentAlias.relate(resourceToFollow, FOLLOW_ALIAS);
                        deltaFollowsAliasCount(currentAlias, 1);
                    }
                    return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
                } else {
                    if (!isFollowing(currentAlias, resourceToFollow)) {
                        currentAlias.relate(resourceToFollow, FOLLOW_CONTENT);
                        deltaFollowsResourcesCount(currentAlias, 1);
                    }
                    indexDAO.syncFollowCounts(currentAlias);
                    return poolDAO.getPoolObjectTx(sessionIdentifier, uri, internal, false, detail);
                }
            }
        });
    }

    @Override
    public TransferEntity getAliasAsProfileTx(@Nonnull final SessionIdentifier sessionIdentifier, @Nonnull final LURI uri, final boolean internal, final RequestDetailLevel detail) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<TransferEntity>() {
            @Nullable @Override
            public TransferEntity call() throws Exception {
                return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
            }
        });
    }

    @Nullable @Override
    public Collection<Entity> getRosterNoTX(@Nonnull final LURI uri, final boolean internal, final SessionIdentifier identity, final RequestDetailLevel request) throws InterruptedException {
        fountainNeo.begin();
        try {
            final PersistedEntity persistedEntity = fountainNeo.find(uri);
            if (persistedEntity == null) {
                return null;
            }
            return getRosterNoTX(persistedEntity, internal, identity, request);
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    Collection<Entity> getRosterNoTX(@Nonnull final PersistedEntity pool, final boolean internal, final SessionIdentifier identity, final RequestDetailLevel detail) {
        final ArrayList<Entity> entities = new ArrayList<Entity>();
        Iterable<FountainRelationship> relationships = pool.relationships(VISITING, INCOMING);
        for (final FountainRelationship visitingSession : relationships) {
            try {
                final PersistedEntity session = visitingSession.other(pool);
                if (session.hasUpdated()) {
                    final Date updatedDate = session.updated();
                    final long updated = updatedDate.getTime();
                    if (updated < System.currentTimeMillis() - FountainNeoImpl.SESSION_INACTIVE_MILLI) {
                        session.$(Dictionary.ACTIVE, false);
                    }
                }
                if (session.has(Dictionary.ACTIVE) && session.$bool(Dictionary.ACTIVE)) {
                    final FountainRelationship relationship = session.relationship(OWNER, OUTGOING);
                    final PersistedEntity alias = relationship.other(session);
                    //If the alias has no profile image, take it from the profile pool!
                    fountainNeo.putProfileInformationIntoAlias(alias);
                    entities.add(alias.toTransfer(detail, internal));
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
        return entities;
    }

    @Nullable @Override
    public Collection<Entity> getRosterNoTX(@Nonnull final LiquidUUID target, final boolean internal, final SessionIdentifier identity, final RequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final PersistedEntity persistedEntity = fountainNeo.find(target);
            return getRosterNoTX(persistedEntity, internal, identity, detail);
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull @Override
    public ChangeReport getUpdateSummaryForAlias(@Nonnull final LURI aliasURI, final long since) throws Exception {
        final ChangeReport report = new ChangeReport();
        final PersistedEntity aliasPersistedEntity = fountainNeo.findOrFail(aliasURI);
        final Traverser traverse = aliasPersistedEntity.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.DEPTH_ONE, new ReturnableEvaluator() {
            @Override
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return currentPos.currentNode().getProperty(Dictionary.TYPE.getKeyName()).toString().startsWith(T_BOARD.asString());
            }
        }, FOLLOW_CONTENT, INCOMING);
        for (final org.neo4j.graphdb.Node node : traverse) {
            final PersistedEntity currPersistedEntity = new FountainEntity(node);
            final Date updatedDate = currPersistedEntity.updated();
            final long updated = updatedDate.getTime();
            if (updated > since) {
                report.addChangedFollowedBoard(currPersistedEntity.toTransfer(RequestDetailLevel.NORMAL, true));
            }
        }
        final List<BoardIndexEntity> ownedBoards = boardDao.getMyBoards(0, 10000, aliasURI.asString());
        for (final BoardIndexEntity ownedBoard : ownedBoards) {
            if (ownedBoard.getUpdated().getTime() > since) {
                try {
                    report.addChangedOwnedBoard(fountainNeo.findOrFail(new LURI(ownedBoard.getUri()))
                                                           .toTransfer(RequestDetailLevel.NORMAL, true));
                } catch (EntityNotFoundException enfe) {
                    log.error(enfe);
                }
            }
        }
        final LatestContentFinder latestContentFinder = new LatestContentFinder(new SessionIdentifier(aliasURI), fountainNeo, aliasPersistedEntity, since, 25, 5000, RequestDetailLevel.NORMAL, 50, userDAO);
        report.setLatestChanges(latestContentFinder.getNodes());

        return report;
    }

    @Override
    public boolean isFollowing(@Nonnull final PersistedEntity currentAlias, final PersistedEntity persistedEntity) throws InterruptedException {
        fountainNeo.begin();
        try {
            boolean following = false;

            Iterable<FountainRelationship> relationships = currentAlias.relationships(FOLLOW_ALIAS, FOLLOW_CONTENT);
            for (final FountainRelationship relationship : relationships) {
                if (relationship.end().equals(persistedEntity)) {
                    following = true;
                }
            }
            return following;
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public void recordChat(final SessionIdentifier sessionIdentifier, final LURI uri, final Entity entity) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TransferEntity unfollowResourceTX(@Nonnull final SessionIdentifier sessionIdentifier, @Nonnull final LURI uri, final RequestDetailLevel detail, final boolean internal) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<TransferEntity>() {
            @Nullable @Override
            public TransferEntity call() throws Exception {
                final PersistedEntity currentAlias = fountainNeo.findOrFail(sessionIdentifier.alias());
                final PersistedEntity resourceToFollow = fountainNeo.findOrFail(uri);
                deltaFollowersCount(resourceToFollow, -1);
                indexDAO.syncFollowerCount(resourceToFollow);

                if (uri.scheme() == LiquidURIScheme.alias) {
                    Iterable<FountainRelationship> relationships = currentAlias.relationships(FOLLOW_ALIAS, OUTGOING);
                    for (final FountainRelationship relationship : relationships) {
                        if (relationship.other(currentAlias).equals(resourceToFollow)) {
                            relationship.delete();
                            deltaFollowsAliasCount(currentAlias, -1);
                        }
                    }
                    return getAliasAsProfile(sessionIdentifier, uri, detail, internal);
                } else {
                    Iterable<FountainRelationship> relationships = currentAlias.relationships(FOLLOW_CONTENT, OUTGOING);
                    for (final FountainRelationship relationship : relationships) {
                        if (relationship.other(currentAlias).equals(resourceToFollow)) {
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

    private void deltaFollowsResourcesCount(@Nonnull final PersistedEntity persistedEntity, final int delta) {
        deltaCount(Dictionary.FOLLOWS_RESOURCES_COUNT, persistedEntity, delta);
    }

    @Nullable
    TransferEntity getAliasAsProfile(@Nonnull final SessionIdentifier sessionIdentifier, @Nonnull final LURI uri, final RequestDetailLevel detail, final boolean internal) throws InterruptedException {
        final PersistedEntity currentAlias = fountainNeo.findOrFail(sessionIdentifier.alias());
        final PersistedEntity persistedEntity = fountainNeo.findOrFail(uri);
        final TransferEntity result = persistedEntity.toTransfer(detail, internal);

        //        int follows = node.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, FountainRelationships.FOLLOW_ALIAS, Direction.OUTGOING).getAllNodes().size();
        //        int followers = node.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.DEPTH_ONE, ReturnableEvaluator.ALL_BUT_START_NODE, FountainRelationships.FOLLOW_ALIAS, Direction.INCOMING).getAllNodes().size();
        //        result.$(Attribute.FOLLOWS_ALIAS_COUNT, String.valueOf(follows));

        final boolean following = isFollowing(currentAlias, persistedEntity);
        result.$(Dictionary.FOLLOWING, following);
        persistedEntity.setPermissionFlagsOnEntity(sessionIdentifier, null, result);

        return result;
    }

    private void deltaFollowsAliasCount(@Nonnull final PersistedEntity persistedEntity, final int delta) {
        deltaCount(Dictionary.FOLLOWS_ALIAS_COUNT, persistedEntity, delta);
    }

    private void deltaFollowersCount(@Nonnull final PersistedEntity persistedEntity, final int delta) {
        deltaCount(Dictionary.FOLLOWERS_COUNT, persistedEntity, delta);
    }

    private void deltaCount(@Nonnull final Attribute attribute, @Nonnull final PersistedEntity resourceToFollow, final int delta) {
        resourceToFollow.$(attribute, (resourceToFollow.has(attribute) ? resourceToFollow.$i(attribute) : 0) + delta);
    }
}