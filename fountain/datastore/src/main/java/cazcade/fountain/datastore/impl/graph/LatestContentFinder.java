/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.graph;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.*;
import cazcade.fountain.datastore.impl.services.persistence.FountainEntity;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.impl.UUIDFactory;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.neo4j.graphdb.*;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Starting from a particular node, walk the graph based on a predefined algorithm.
 * <p/>
 * The algorithm basically looks for the most strongly related content first, such as children, link children and owned content. Then drills down to
 * children, then starts going back up to parents. This algorithm will need constant review and tuning to get right of course. It pretty much guarantees to find 'n' nodes of interest no matter how many nodes it needs to traverse.
 *
 * @author neilellis@cazcade.com
 */
@SuppressWarnings({"PublicMethodNotExposedInInterface"})
public class LatestContentFinder {
    public static final  int     CACHE_ERROR_MARGIN          = 5000;
    public static final  boolean INCLUDE_SESSION_INFORMATION = false;
    @Nonnull
    public static final  String  GLOBAL_SKIP                 = "global.skip";
    @Nonnull
    private static final Logger  log                         = Logger.getLogger(LatestContentFinder.class);

    @Nonnull
    private static final Cache nodeCache;
    @Nonnull
    private final Map<String, TransferEntity> nodes      = new HashMap<String, TransferEntity>();
    @Nonnull
    private final Set<Long>                   visited    = new HashSet<Long>();
    @Nonnull
    private       Set<Long>                   skip       = new HashSet<Long>();
    @Nonnull
    private       Set<Long>                   globalSkip = new HashSet<Long>();
    @Nonnull
    private final SessionIdentifier  identity;
    private final long               since;
    private final int                maxReturnNodes;
    private final int                maxTraversal;
    @Nonnull
    private final RequestDetailLevel detail;
    private final int                maxDepth;
    @Nonnull
    private final FountainUserDAO    userDAO;
    private final long minAge = System.currentTimeMillis() - 7 * 24 * 3600 * 1000;

    static {
        if (!CacheManager.getInstance().cacheExists("latestcontent")) {
            CacheManager.getInstance().addCache("latestcontent");
        }
        nodeCache = CacheManager.getInstance().getCache("latestcontent");
    }

    public LatestContentFinder(@Nonnull final SessionIdentifier identity, @Nonnull final FountainNeo fountainNeo, @Nonnull final PersistedEntity startPersistedEntity, final long since, final int maxReturnNodes, final int maxTraversal, final RequestDetailLevel detail, final int maxDepth, final FountainUserDAO userDAO) throws Exception {
        this.identity = identity;
        this.userDAO = userDAO;
        this.since = since;
        this.maxReturnNodes = maxReturnNodes;
        this.maxTraversal = maxTraversal;
        this.detail = detail;
        this.maxDepth = maxDepth;

        final Element localSkipElement = nodeCache.get(getCacheKey());
        if (localSkipElement != null) {
            //The plus 5 seconds is because we need a margin of error
            if (localSkipElement.getCreationTime() < since + CACHE_ERROR_MARGIN) {
                //noinspection unchecked
                skip = (Set<Long>) localSkipElement.getValue();
            } else {
                log.debug("No valid skip available");
            }
        }


        final Element globalSkipElement = nodeCache.get(GLOBAL_SKIP);
        if (globalSkipElement != null) {
            //noinspection unchecked
            globalSkip = (Set<Long>) globalSkipElement.getValue();
        }

        //        findContentNodesNewMethod(startNode, true, false, fountainNeo.find(identity.aliasURI()), detail, 0);
        final PersistedEntity aliasPersistedEntity = fountainNeo.find(identity.aliasURI());
        if (aliasPersistedEntity == null) {
            throw new EntityNotFoundException("Could not locate alias %s", identity.aliasURI());
        }
        findContentNodesNewMethod(startPersistedEntity, aliasPersistedEntity, detail);

        final Element element = new Element(getCacheKey(), skip);
        nodeCache.put(element);
        nodeCache.put(new Element(GLOBAL_SKIP, globalSkip));
    }

    @SuppressWarnings({"OverlyComplexAnonymousInnerClass"})
    private void findContentNodesNewMethod(@Nonnull final PersistedEntity startPersistedEntity, @Nonnull final PersistedEntity myAliasPersistedEntity, @Nonnull final RequestDetailLevel resultDetail) throws Exception {
        final int[] count = new int[1];
        final LiquidURI currentAliasURI = identity.aliasURI();

        final Traverser traverser = startPersistedEntity.traverse(Traverser.Order.BREADTH_FIRST, new StopEvaluator() {
                    @Override
                    public boolean isStopNode(@Nonnull final TraversalPosition currentPos) {
                        final PersistedEntity currentPersistedEntity = new FountainEntity(currentPos.currentNode());
                        if (isUnlisted(currentPersistedEntity)) {
                            return true;
                        }

                        if (currentPersistedEntity.has(FountainRelationships.COMMENT, Direction.INCOMING)) {
                            final FountainRelationship singleRelationship = currentPersistedEntity.relationship(FountainRelationships.COMMENT, Direction.INCOMING);
                            assert singleRelationship != null;
                            if (isUnlisted(singleRelationship.other(currentPersistedEntity))) {
                                return true;
                            }
                        }
                        final boolean result = currentPos.returnedNodesCount() >= maxReturnNodes * 10 ||
                                               count[0] >= maxTraversal ||
                                               currentPos.depth() > maxDepth;
                        count[0]++;
                        return result;
                    }
                }, new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                        final PersistedEntity currentPersistedEntity = new FountainEntity(currentPos.currentNode());
                        if (!skip.contains(currentPersistedEntity.getPersistenceId()) && !globalSkip.contains(currentPersistedEntity
                                .getPersistenceId())) {
                            try {
                                if (currentPersistedEntity.has$(Dictionary.URI)) {
                                    if (!currentPersistedEntity.hasURI()) {
                                        throw new NullPointerException("Attempted to use a null uri in 'isReturnableNode' in LatestContentFinder.");
                                    }
                                    final LiquidURI uri = currentPersistedEntity.uri();
                                    final LiquidURIScheme scheme = uri.scheme();
                                    //noinspection PointlessBooleanExpression,ConstantConditions
                                    if (scheme == LiquidURIScheme.session && INCLUDE_SESSION_INFORMATION ||
                                        scheme == LiquidURIScheme.comment ||
                                        scheme == LiquidURIScheme.pool) {
                                        if (currentPersistedEntity.isLatestVersion()) {
                                            return validForThisRequest(currentPersistedEntity, uri, myAliasPersistedEntity, currentAliasURI);
                                        } else {
                                            log.debug("FountainEntity not latest version: " + uri);
                                        }
                                    } else {
                                        log.debug("FountainEntity is of the wrong type: " + uri);
                                    }
                                } else {
                                    log.debug("No  URI key");
                                }
                            } catch (InterruptedException e) {
                                log.error(e);
                            }
                            globalSkip.add(currentPersistedEntity.getPersistenceId());
                            return false;
                        } else {
                            //skipped
                            return false;
                        }
                    }
                }, FountainRelationships.CHILD, Direction.OUTGOING, FountainRelationships.LINKED_CHILD, Direction.OUTGOING, FountainRelationships.OWNER, Direction.INCOMING, FountainRelationships.COMMENT, Direction.OUTGOING, FountainRelationships.PREVIOUS, Direction.OUTGOING, FountainRelationships.VISITING, Direction.BOTH, FountainRelationships.FOLLOW_CONTENT, Direction.OUTGOING, FountainRelationships.FOLLOW_ALIAS, Direction.OUTGOING
                                                                 );
        final Collection<org.neo4j.graphdb.Node> candidateNodes = traverser.getAllNodes();
        log.debug("Found " + candidateNodes.size() + " candidate nodes:");
        for (final org.neo4j.graphdb.Node cNode : candidateNodes) {
            final PersistedEntity candidatePersistedEntity = new FountainEntity(cNode);
            if (!candidatePersistedEntity.hasURI()) {
                throw new NullPointerException("Attempted to use a null uri in 'findContentNodesNewMethod' in LatestContentFinder.");
            }
            final LiquidURI uri = candidatePersistedEntity.uri();
            final TransferEntity entity;
            //noinspection PointlessBooleanExpression,ConstantConditions
            if (candidatePersistedEntity.canBe(Types.T_SESSION) && INCLUDE_SESSION_INFORMATION) {
                entity = fromSessionNode(candidatePersistedEntity, resultDetail);
            } else if (candidatePersistedEntity.canBe(Types.T_COMMENT)) {
                entity = fromComment(candidatePersistedEntity, resultDetail);
            } else if (uri.scheme() == LiquidURIScheme.pool) {
                entity = fromObjectNode(candidatePersistedEntity, resultDetail);
            } else {
                log.debug("FountainEntity {0} was filtered because of type {1}", uri, candidatePersistedEntity.type());
                continue;
            }
            if (entity.has$(Dictionary.TITLE)) {
                final String key = entity.default$sub(Dictionary.AUTHOR_A, Dictionary.NAME, "")
                                   + ':'
                                   + entity.default$(Dictionary.SOURCE, "");
                if (nodes.containsKey(key)) {
                    if (entity.wasPublishedAfter(nodes.get(key))) {
                        nodes.put(key, entity);
                        log.debug("FountainEntity replaced with new version: " + key);
                    }
                    log.debug("FountainEntity filtered, key not unique: " + key);
                } else {
                    if (nodes.size() < maxReturnNodes) {
                        nodes.put(key, entity);
                    }
                }
            } else {
                log.debug("FountainEntity had no title, so filtered.");
            }
        }
        log.debug("Filtered to " + nodes.size() + " nodes. Max nodes is " + maxReturnNodes);
    }

    @SuppressWarnings({"MethodOnlyUsedFromInnerClass"})
    private static boolean isUnlisted(@Nonnull final Entity currentNode) {
        return (currentNode.canBe(Types.T_POOL2D) || currentNode.canBe(Types.T_BOARD))
               && !currentNode.default$bool(Dictionary.LISTED, false);
    }

    @SuppressWarnings({"MethodOnlyUsedFromInnerClass"})
    private boolean validForThisRequest(@Nonnull final PersistedEntity currentPersistedEntity, @Nonnull final LiquidURI uri, @Nonnull final PersistedEntity myAliasPersistedEntity, @Nonnull final LiquidURI currentAliasURI) throws InterruptedException {
        final LiquidURIScheme scheme = uri.scheme();
        final Date updated = currentPersistedEntity.updated();
        final Long nodeAge = updated.getTime();
        if (nodeAge >= since) {
            if (currentPersistedEntity.isAuthorized(identity, Permission.VIEW_PERM)) {
                final boolean authoredByMe = currentPersistedEntity.isAuthor(myAliasPersistedEntity);
                if (!authoredByMe) {
                    if (scheme == LiquidURIScheme.session) {
                        if (!uri.equals(currentAliasURI) && !CommonConstants.ANONYMOUS_ALIAS.equals(uri.asString())) {
                            if (currentPersistedEntity.$bool(Dictionary.ACTIVE)
                                && currentPersistedEntity.has(FountainRelationships.VISITING, Direction.OUTGOING)) {
                                return true;
                            } else {
                                log.debug("FountainEntity was a session but not of interest: " + uri);
                            }
                        } else {
                            log.debug("FountainEntity was a session but was own or anonymous: " + uri);
                        }
                    } else if (uri.asString().startsWith("pool") || scheme == LiquidURIScheme.comment) {
                        if (currentPersistedEntity.isListed()) {
                            return true;
                        } else {
                            log.debug("FountainEntity not listed: " + uri);
                            globalSkip.add(currentPersistedEntity.getPersistenceId());
                            return false;
                        }
                    } else {
                        throw new IllegalStateException("Should not reach this line.");
                    }
                } else {
                    log.debug("FountainEntity authored by me: " + uri);
                }
            } else {
                log.debug("FountainEntity is not authorized: " + uri);
            }
        } else {
            if (nodeAge < minAge) {
                globalSkip.add(currentPersistedEntity.getPersistenceId());
                log.debug("FountainEntity is too old to be of interest " + nodeAge + " < " + minAge);
            } else {
                log.debug("FountainEntity is too old for this request " + nodeAge + " < " + since);
            }
        }
        skip.add(currentPersistedEntity.getPersistenceId());
        return false;
    }

    @Nonnull
    private TransferEntity fromSessionNode(@Nonnull final PersistedEntity sessionPersistedEntity, @Nonnull final RequestDetailLevel resultDetail) throws Exception {
        final TransferEntity entity = SimpleEntity.createNewTransferEntity(Types.T_PRESENCE_UPDATE, UUIDFactory.randomUUID());
        final FountainRelationship ownerRelationship = sessionPersistedEntity.relationship(FountainRelationships.OWNER, Direction.OUTGOING);
        if (ownerRelationship == null) {
            throw new NullPointerException("Attempted to use a null ownerRelationship in 'fromSessionNode' in LatestContentFinder.");
        }
        final PersistedEntity aliasPersistedEntity = ownerRelationship.other(sessionPersistedEntity);
        entity.child(Dictionary.AUTHOR_A, userDAO.getAliasFromNode(ownerRelationship.other(sessionPersistedEntity), false, resultDetail), true);
        final String aliasName = ownerRelationship.other(sessionPersistedEntity).$(Dictionary.NAME);
        final String aliasFullName = ownerRelationship.other(sessionPersistedEntity).$(Dictionary.FULL_NAME);
        entity.$(Dictionary.TITLE, aliasName);
        final Iterable<FountainRelationship> vistingRelationships = sessionPersistedEntity.relationships(FountainRelationships.VISITING, Direction.OUTGOING);
        for (final FountainRelationship vistingRelationship : vistingRelationships) {
            final PersistedEntity poolPersistedEntity = vistingRelationship.other(sessionPersistedEntity);
            final LiquidURI poolUri = poolPersistedEntity.uri();

            entity.$(Dictionary.TITLE, '@' + aliasName + " was spotted in " + poolUri);
            entity.$(Dictionary.TEXT_BRIEF, aliasFullName + " was spotted in " + poolUri);
            entity.$(Dictionary.TEXT_EXTENDED, aliasFullName + " was spotted in " + poolUri);

            if (vistingRelationship.has(Dictionary.UPDATED.getKeyName())) {
                entity.$(Dictionary.PUBLISHED, (String) vistingRelationship.$(Dictionary.UPDATED.getKeyName()));
                final long updated = Long.valueOf(vistingRelationship.$(Dictionary.UPDATED.getKeyName()).toString());
                if (updated >= since) {
                    //it's stale so add to skip list now
                    skip.add(vistingRelationship.other(sessionPersistedEntity).getPersistenceId());
                }
            }
            entity.$(Dictionary.SOURCE, poolUri);
            entity.$(aliasPersistedEntity, Dictionary.IMAGE_URL);
            entity.$(aliasPersistedEntity, Dictionary.ICON_URL);
            //The URI keeps it unique in the stream
            entity.$(Dictionary.URI, new LiquidURI(LiquidURIScheme.status, "presence:" + aliasName).asString());
        }
        return entity;
    }

    @Nonnull
    private TransferEntity fromComment(@Nonnull final PersistedEntity persistedEntity, @Nonnull final RequestDetailLevel resultDetail) throws Exception {
        final TransferEntity entity = SimpleEntity.createNewTransferEntity(Types.T_COMMENT_UPDATE, UUIDFactory.randomUUID());
        entity.$(persistedEntity, Dictionary.TEXT_BRIEF);
        entity.published(persistedEntity.updated());
        entity.$(persistedEntity, Dictionary.TEXT_EXTENDED);

        final LiquidURI objectURI = persistedEntity.uri();
        final FountainRelationship authorRelationship = persistedEntity.relationship(FountainRelationships.AUTHOR, Direction.OUTGOING);
        TransferEntity aliasEntity = null;
        if (authorRelationship != null) {
            final PersistedEntity authorPersistedEntity = authorRelationship.other(persistedEntity);
            aliasEntity = userDAO.getAliasFromNode(authorPersistedEntity, false, resultDetail);
            entity.child(Dictionary.AUTHOR_A, aliasEntity, true);
            //            entity.$(Attribute.TITLE, (String) authorNode.$(Attribute.NAME.getKeyName()));
            //            if (authorNode.has(Attribute.IMAGE_URL.getKeyName())) {
            //                entity.$(Attribute.ICON_URL, (String) authorNode.$(Attribute.IMAGE_URL.getKeyName()));
            //            }
        }
        PersistedEntity poolOrObjectPersistedEntity = null;
        final Traverser traverse = persistedEntity.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            @Override
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return !new FountainEntity(currentPos.currentNode()).canBe(Types.T_COMMENT);
            }
        }, FountainRelationships.PREVIOUS, Direction.INCOMING, FountainRelationships.COMMENT, Direction.INCOMING);
        final Collection<org.neo4j.graphdb.Node> nonCommentNodes = traverse.getAllNodes();
        if (!nonCommentNodes.isEmpty()) {
            poolOrObjectPersistedEntity = new FountainEntity(nonCommentNodes.iterator().next());
        }
        if (poolOrObjectPersistedEntity != null) {
            entity.$(Dictionary.SOURCE, poolOrObjectPersistedEntity.uri());
            entity.$(poolOrObjectPersistedEntity, Dictionary.IMAGE_URL);
            entity.$(poolOrObjectPersistedEntity, Dictionary.ICON_URL);
            if (aliasEntity != null && poolOrObjectPersistedEntity.isListed()) {
                entity.$(Dictionary.TITLE, String.format("@%s commented on '%s'", aliasEntity.$(Dictionary.NAME), poolOrObjectPersistedEntity
                        .default$(Dictionary.TITLE, poolOrObjectPersistedEntity.default$(Dictionary.NAME, "Unknown"))));
            }
        }
        //The URI keeps it unique in the stream
        entity.$(Dictionary.URI, new LiquidURI(LiquidURIScheme.status, "comment:" + objectURI).asString());
        return entity;
    }

    @Nonnull
    private TransferEntity fromObjectNode(@Nonnull final PersistedEntity persistedEntity, @Nonnull final RequestDetailLevel resultDetail) throws Exception {
        final TransferEntity entity = SimpleEntity.createNewTransferEntity(Types.T_OBJECT_UPDATE, UUIDFactory.randomUUID());
        PersistedEntity boardPersistedEntity = null;
        final Iterator<org.neo4j.graphdb.Node> parentIterator = persistedEntity.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            @Override
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return new FountainEntity(currentPos.currentNode()).canBe(Types.T_BOARD);
            }
        }, FountainRelationships.CHILD, Direction.INCOMING).iterator();
        if (parentIterator.hasNext()) {
            boardPersistedEntity = new FountainEntity(parentIterator.next());
        }
        if (persistedEntity.has$(Dictionary.MOTIVE_TEXT)) {
            final String motiveText = persistedEntity.$(Dictionary.MOTIVE_TEXT);
            if (motiveText.isEmpty() && persistedEntity.has$(Dictionary.TEXT_EXTENDED)) {
                final String extendedText = persistedEntity.$(Dictionary.TEXT_EXTENDED);
                entity.$(Dictionary.TEXT_BRIEF, extendedText);
                entity.$(Dictionary.TEXT_EXTENDED, extendedText);
            } else {
                entity.$(Dictionary.TEXT_BRIEF, "updated");
                entity.$(Dictionary.TEXT_EXTENDED, "updated");
            }
            entity.$(Dictionary.TEXT_BRIEF, motiveText);
            entity.$(Dictionary.TEXT_EXTENDED, motiveText);
        }
        entity.published(persistedEntity.updated());
        entity.$(Dictionary.SOURCE, persistedEntity.uri());
        if (persistedEntity.has$(Dictionary.IMAGE_URL)) {
            entity.$(persistedEntity, Dictionary.IMAGE_URL);
        } else if (boardPersistedEntity != null && persistedEntity.has$(Dictionary.ICON_URL)) {
            entity.$(Dictionary.IMAGE_URL, boardPersistedEntity.$(Dictionary.IMAGE_URL));
        }
        //The author of this update is the owner of the entity :-)
        final FountainRelationship editorRelationship = persistedEntity.relationship(FountainRelationships.EDITOR, Direction.OUTGOING);
        if (editorRelationship != null) {
            final PersistedEntity ownerPersistedEntity = editorRelationship.other(persistedEntity);
            final TransferEntity authorEntity = userDAO.getAliasFromNode(ownerPersistedEntity, false, resultDetail);
            entity.child(Dictionary.AUTHOR_A, authorEntity, true);
            //            entity.$(Attribute.TITLE, (String) ownerNode.$(Attribute.NAME.getKeyName()));
            //            if (ownerNode.has(Attribute.IMAGE_URL.getKeyName())) {
            //                entity.$(Attribute.ICON_URL, (String) ownerNode.$(Attribute.IMAGE_URL.getKeyName()));
            //            }
            if (boardPersistedEntity != null) {
                entity.$(Dictionary.SOURCE, boardPersistedEntity.uri());
                entity.$(Dictionary.TITLE, String.format("@%s made changes to '%s'", authorEntity.$(Dictionary.NAME), boardPersistedEntity
                        .default$(Dictionary.TITLE, boardPersistedEntity.default$(Dictionary.NAME, "Unknown"))));
            }
        }


        final FountainRelationship viewRelationship = persistedEntity.relationship(FountainRelationships.VIEW, Direction.OUTGOING);
        if (viewRelationship != null) {
            entity.child(Dictionary.VIEW_ENTITY, viewRelationship.other(persistedEntity).toTransfer(resultDetail, false), true);
        }

        //The URI keeps it unique in the stream
        entity.$(Dictionary.URI, new LiquidURI(LiquidURIScheme.status, "object:" + persistedEntity.uri()).asString());
        return entity;
    }

    @Nonnull
    public String getCacheKey() {
        return identity.toString() + ':' + ':' + maxTraversal + ':' + detail + ':' + maxDepth;
    }

    @Nonnull
    public Collection<TransferEntity> getNodes() {
        return nodes.values();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LatestContentFinder");
        sb.append("{nodes=").append(nodes);
        sb.append(", visited=").append(visited);
        sb.append(", skip=").append(skip);
        sb.append(", globalSkip=").append(globalSkip);
        sb.append(", identity=").append(identity);
        sb.append(", since=").append(since);
        sb.append(", maxReturnNodes=").append(maxReturnNodes);
        sb.append(", maxTraversal=").append(maxTraversal);
        sb.append(", detail=").append(detail);
        sb.append(", maxDepth=").append(maxDepth);
        sb.append(", minAge=").append(minAge);
        sb.append('}');
        return sb.toString();
    }
}
