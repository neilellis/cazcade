package cazcade.fountain.datastore.impl.graph;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.*;
import cazcade.fountain.datastore.impl.services.persistence.FountainEntityImpl;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
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
    @Nonnull
    private static final Logger log = Logger.getLogger(LatestContentFinder.class);
    public static final int CACHE_ERROR_MARGIN = 5000;
    public static final boolean INCLUDE_SESSION_INFORMATION = false;
    @Nonnull
    public static final String GLOBAL_SKIP = "global.skip";
    @Nonnull
    private final Map<String, LSDEntity> nodes = new HashMap<String, LSDEntity>();
    @Nonnull
    private final Set<Long> visited = new HashSet<Long>();
    @Nonnull
    private Set<Long> skip = new HashSet<Long>();
    @Nonnull
    private Set<Long> globalSkip = new HashSet<Long>();
    @Nonnull
    private final LiquidSessionIdentifier identity;
    private final long since;
    private final int maxReturnNodes;
    private final int maxTraversal;
    @Nonnull
    private final LiquidRequestDetailLevel detail;
    private final int maxDepth;

    @Nonnull
    private static final Cache nodeCache;
    @Nonnull
    private final FountainUserDAO userDAO;
    private final long minAge = System.currentTimeMillis() - 7 * 24 * 3600 * 1000;

    static {
        if (!CacheManager.getInstance().cacheExists("latestcontent")) {
            CacheManager.getInstance().addCache("latestcontent");
        }
        nodeCache = CacheManager.getInstance().getCache("latestcontent");

    }

    public LatestContentFinder(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final FountainNeo fountainNeo, @Nonnull final FountainEntity startFountainEntity, final long since, final int maxReturnNodes, final int maxTraversal, final LiquidRequestDetailLevel detail, final int maxDepth, final FountainUserDAO userDAO) throws InterruptedException {
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

//        findContentNodesNewMethod(startNode, true, false, fountainNeo.findByURI(identity.getAliasURL()), detail, 0);
        final FountainEntity aliasFountainEntity = fountainNeo.findByURI(identity.getAliasURL());
        if (aliasFountainEntity == null) {
            throw new EntityNotFoundException("Could not locate alias %s", identity.getAliasURL());
        }
        findContentNodesNewMethod(startFountainEntity, aliasFountainEntity, detail);

        final Element element = new Element(getCacheKey(), skip);
        nodeCache.put(element);
        nodeCache.put(new Element(GLOBAL_SKIP, globalSkip));
    }

    @Nonnull
    public String getCacheKey() {
        return identity.toString() + ':' + ':' + maxTraversal + ':' + detail + ':' + maxDepth;
    }

    @SuppressWarnings({"OverlyComplexAnonymousInnerClass"})
    private void findContentNodesNewMethod(@Nonnull final FountainEntity startFountainEntity, @Nonnull final FountainEntity myAliasFountainEntity, @Nonnull final LiquidRequestDetailLevel resultDetail) throws InterruptedException {
        final int[] count = new int[1];
        final LiquidURI currentAliasURI = identity.getAliasURL();

        final Traverser traverser = startFountainEntity.traverse(Traverser.Order.BREADTH_FIRST, new StopEvaluator() {
                    @Override
                    public boolean isStopNode(@Nonnull final TraversalPosition currentPos) {
                        final FountainEntity currentFountainEntity = new FountainEntityImpl(currentPos.currentNode());
                        if (isUnlisted(currentFountainEntity)) {
                            return true;
                        }

                        if (currentFountainEntity.hasRelationship(FountainRelationships.COMMENT, Direction.INCOMING)) {
                            final FountainRelationship singleRelationship = currentFountainEntity.getSingleRelationship(FountainRelationships.COMMENT, Direction.INCOMING);
                            assert singleRelationship != null;
                            if (isUnlisted(singleRelationship.getOtherNode(currentFountainEntity))) {
                                return true;
                            }
                        }
                        final boolean result = currentPos.returnedNodesCount() >= maxReturnNodes * 10 || count[0] >= maxTraversal || currentPos.depth() > maxDepth;
                        count[0]++;
                        return result;
                    }
                }, new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {

                        final FountainEntity currentFountainEntity = new FountainEntityImpl(currentPos.currentNode());
                        if (!skip.contains(currentFountainEntity.getPersistenceId()) && !globalSkip.contains(currentFountainEntity.getPersistenceId())) {
                            try {
                                if (currentFountainEntity.hasAttribute(LSDAttribute.URI)) {
                                    final LiquidURI uri = currentFountainEntity.getURI();
                                    if (uri == null) {
                                        throw new NullPointerException("Attempted to use a null uri in 'isReturnableNode' in LatestContentFinder.");
                                    }
                                    final LiquidURIScheme scheme = uri.getScheme();
                                    //noinspection PointlessBooleanExpression,ConstantConditions
                                    if (scheme == LiquidURIScheme.session && INCLUDE_SESSION_INFORMATION ||
                                            scheme == LiquidURIScheme.comment ||
                                            scheme == LiquidURIScheme.pool) {
                                        if (currentFountainEntity.isLatestVersion()) {
                                            return validForThisRequest(currentFountainEntity, uri, myAliasFountainEntity, currentAliasURI);
                                        } else {
                                            log.debug("FountainEntityImpl not latest version: " + uri);
                                        }
                                    } else {
                                        log.debug("FountainEntityImpl is of the wrong type: " + uri);

                                    }
                                } else {
                                    log.debug("No  URI key");
                                }
                            } catch (InterruptedException e) {
                                log.error(e.getMessage(), e);
                            }
                            globalSkip.add(currentFountainEntity.getPersistenceId());
                            return false;
                        } else {
                            //skipped
                            return false;
                        }
                    }
                },
                FountainRelationships.CHILD, Direction.OUTGOING,
                FountainRelationships.LINKED_CHILD, Direction.OUTGOING,
                FountainRelationships.OWNER, Direction.INCOMING,
                FountainRelationships.COMMENT, Direction.OUTGOING,
                FountainRelationships.PREVIOUS, Direction.OUTGOING,
                FountainRelationships.VISITING, Direction.BOTH,
                FountainRelationships.FOLLOW_CONTENT, Direction.OUTGOING,
                FountainRelationships.FOLLOW_ALIAS, Direction.OUTGOING
        );
        final Collection<org.neo4j.graphdb.Node> candidateNodes = traverser.getAllNodes();
        log.debug("Found " + candidateNodes.size() + " candidate nodes:");
        for (final org.neo4j.graphdb.Node cNode : candidateNodes) {
            final FountainEntity candidateFountainEntity = new FountainEntityImpl(cNode);
            final LiquidURI uri = candidateFountainEntity.getURI();
            if (uri == null) {
                throw new NullPointerException("Attempted to use a null uri in 'findContentNodesNewMethod' in LatestContentFinder.");
            }
            final LSDEntity entity;
            //noinspection PointlessBooleanExpression,ConstantConditions
            if (candidateFountainEntity.canBe(LSDDictionaryTypes.SESSION) && INCLUDE_SESSION_INFORMATION) {
                entity = fromSessionNode(candidateFountainEntity, resultDetail);
            } else if (candidateFountainEntity.canBe(LSDDictionaryTypes.COMMENT)) {
                entity = fromComment(candidateFountainEntity, resultDetail);
            } else if (uri.getScheme() == LiquidURIScheme.pool) {
                entity = fromObjectNode(candidateFountainEntity, resultDetail);
            } else {
                log.debug("FountainEntityImpl {0} was filtered because of type {1}", uri, candidateFountainEntity.getTypeDef());
                continue;
            }
            if (entity.hasAttribute(LSDAttribute.TITLE)) {
                final String key = entity.getSubAttribute(LSDAttribute.AUTHOR, LSDAttribute.NAME, "") + ':' + entity.getAttribute(LSDAttribute.SOURCE, "");
                if (nodes.containsKey(key)) {
                    if (entity.wasPublishedAfter(nodes.get(key))) {
                        nodes.put(key, entity);
                        log.debug("FountainEntityImpl replaced with new version: " + key);
                    }
                    log.debug("FountainEntityImpl filtered, key not unique: " + key);
                } else {
                    if (nodes.size() < maxReturnNodes) {
                        nodes.put(key, entity);
                    }
                }
            } else {
                log.debug("FountainEntityImpl had no title, so filtered.");
            }
        }
        log.debug("Filtered to " + nodes.size() + " nodes. Max nodes is " + maxReturnNodes);
    }

    @SuppressWarnings({"MethodOnlyUsedFromInnerClass"})
    private static boolean isUnlisted(@Nonnull final LSDEntity currentNode) {

        return (currentNode.canBe(LSDDictionaryTypes.POOL2D) || currentNode.canBe(LSDDictionaryTypes.BOARD)) && !currentNode.getBooleanAttribute(LSDAttribute.LISTED, false);
    }

    @SuppressWarnings({"MethodOnlyUsedFromInnerClass"})
    private boolean validForThisRequest(@Nonnull final FountainEntity currentFountainEntity, @Nonnull final LiquidURI uri, @Nonnull final FountainEntity myAliasFountainEntity, @Nonnull final LiquidURI currentAliasURI) throws InterruptedException {
        final LiquidURIScheme scheme = uri.getScheme();
        final Date updated = currentFountainEntity.getUpdated();
        if (updated == null) {
            throw new NullPointerException("Attempted to use a null updated in 'validForThisRequest' in LatestContentFinder.");
        }
        final Long nodeAge = updated.getTime();
        if (nodeAge >= since) {
            if (currentFountainEntity.isAuthorized(identity, LiquidPermission.VIEW)) {
                final boolean authoredByMe = currentFountainEntity.isAuthor(myAliasFountainEntity);
                if (!authoredByMe) {
                    if (scheme == LiquidURIScheme.session) {
                        if (!uri.equals(currentAliasURI) && !CommonConstants.ANONYMOUS_ALIAS.equals(uri.asString())) {
                            if (currentFountainEntity.getBooleanAttribute(LSDAttribute.ACTIVE)
                                    && currentFountainEntity.hasRelationship(FountainRelationships.VISITING, Direction.OUTGOING)) {
                                return true;
                            } else {
                                log.debug("FountainEntityImpl was a session but not of interest: " + uri);
                            }
                        } else {
                            log.debug("FountainEntityImpl was a session but was own or anonymous: " + uri);
                        }
                    } else if (uri.asString().startsWith("pool") || scheme == LiquidURIScheme.comment) {
                        if (currentFountainEntity.isListed()) {
                            return true;
                        } else {
                            log.debug("FountainEntityImpl not listed: " + uri);
                            globalSkip.add(currentFountainEntity.getPersistenceId());
                            return false;
                        }
                    } else {
                        throw new IllegalStateException("Should not reach this line.");
                    }
                } else {
                    log.debug("FountainEntityImpl authored by me: " + uri);

                }
            } else {
                log.debug("FountainEntityImpl is not authorized: " + uri);

            }
        } else {
            if (nodeAge < minAge) {
                globalSkip.add(currentFountainEntity.getPersistenceId());
                log.debug("FountainEntityImpl is too old to be of interest " + nodeAge + " < " + minAge);
            } else {
                log.debug("FountainEntityImpl is too old for this request " + nodeAge + " < " + since);
            }
        }
        skip.add(currentFountainEntity.getPersistenceId());
        return false;
    }


    @Nonnull
    private LSDEntity fromComment(@Nonnull final FountainEntity fountainEntity, @Nonnull final LiquidRequestDetailLevel resultDetail) throws InterruptedException {
        final LSDEntity entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.COMMENT_UPDATE, UUIDFactory.randomUUID());
        entity.setAttribute(LSDAttribute.TEXT_BRIEF, fountainEntity.getAttribute(LSDAttribute.TEXT_BRIEF));
        entity.setPublished(fountainEntity.getUpdated());
        entity.setAttribute(LSDAttribute.TEXT_EXTENDED, fountainEntity.getAttribute(LSDAttribute.TEXT_EXTENDED));

        final LiquidURI objectURI = fountainEntity.getURI();
        final FountainRelationship authorRelationship = fountainEntity.getSingleRelationship(FountainRelationships.AUTHOR, Direction.OUTGOING);
        LSDEntity aliasEntity = null;
        if (authorRelationship != null) {
            final FountainEntity authorFountainEntity = authorRelationship.getOtherNode(fountainEntity);
            aliasEntity = userDAO.getAliasFromNode(authorFountainEntity, false, resultDetail);
            entity.addSubEntity(LSDAttribute.AUTHOR, aliasEntity, true);
//            entity.setAttribute(LSDAttribute.TITLE, (String) authorNode.getProperty(LSDAttribute.NAME.getKeyName()));
//            if (authorNode.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
//                entity.setAttribute(LSDAttribute.ICON_URL, (String) authorNode.getProperty(LSDAttribute.IMAGE_URL.getKeyName()));
//            }
        }
        FountainEntity poolOrObjectFountainEntity = null;
        final Traverser traverse = fountainEntity.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            @Override
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return !new FountainEntityImpl(currentPos.currentNode()).canBe(LSDDictionaryTypes.COMMENT);
            }
        }, FountainRelationships.PREVIOUS, Direction.INCOMING, FountainRelationships.COMMENT, Direction.INCOMING);
        final Collection<org.neo4j.graphdb.Node> nonCommentNodes = traverse.getAllNodes();
        if (!nonCommentNodes.isEmpty()) {
            poolOrObjectFountainEntity = new FountainEntityImpl(nonCommentNodes.iterator().next());
        }
        if (poolOrObjectFountainEntity != null) {
            entity.setAttribute(LSDAttribute.SOURCE, poolOrObjectFountainEntity.getURI());
            entity.copyAttribute(poolOrObjectFountainEntity, LSDAttribute.IMAGE_URL);
            entity.copyAttribute(poolOrObjectFountainEntity, LSDAttribute.ICON_URL);
            if (aliasEntity != null && poolOrObjectFountainEntity.isListed()) {
                entity.setAttribute(LSDAttribute.TITLE, String.format("@%s commented on '%s'", aliasEntity.getAttribute(LSDAttribute.NAME), poolOrObjectFountainEntity.getAttribute(LSDAttribute.TITLE, poolOrObjectFountainEntity.getAttribute(LSDAttribute.NAME, "Unknown"))));
            }

        }
        //The URI keeps it unique in the stream
        entity.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.status, "comment:" + objectURI).asString());
        return entity;

    }


    @Nonnull
    private LSDEntity fromObjectNode(@Nonnull final FountainEntity fountainEntity, @Nonnull final LiquidRequestDetailLevel resultDetail) throws InterruptedException {
        final LSDEntity entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.OBJECT_UPDATE, UUIDFactory.randomUUID());
        FountainEntity boardFountainEntity = null;
        final Iterator<org.neo4j.graphdb.Node> parentIterator = fountainEntity.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            @Override
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return new FountainEntityImpl(currentPos.currentNode()).canBe(LSDDictionaryTypes.BOARD);
            }
        }, FountainRelationships.CHILD, Direction.INCOMING).iterator();
        if (parentIterator.hasNext()) {
            boardFountainEntity = new FountainEntityImpl(parentIterator.next());
        }
        if (fountainEntity.hasAttribute(LSDAttribute.MOTIVE_TEXT)) {
            final String motiveText = fountainEntity.getAttribute(LSDAttribute.MOTIVE_TEXT);
            if (motiveText.isEmpty() && fountainEntity.hasAttribute(LSDAttribute.TEXT_EXTENDED)) {
                final String extendedText = fountainEntity.getAttribute(LSDAttribute.TEXT_EXTENDED);
                entity.setAttribute(LSDAttribute.TEXT_BRIEF, extendedText);
                entity.setAttribute(LSDAttribute.TEXT_EXTENDED, extendedText);
            } else {
                entity.setAttribute(LSDAttribute.TEXT_BRIEF, "updated");
                entity.setAttribute(LSDAttribute.TEXT_EXTENDED, "updated");
            }
            entity.setAttribute(LSDAttribute.TEXT_BRIEF, motiveText);
            entity.setAttribute(LSDAttribute.TEXT_EXTENDED, motiveText);
        }
        entity.setPublished(fountainEntity.getUpdated());
        entity.setAttribute(LSDAttribute.SOURCE, fountainEntity.getURI());
        if (fountainEntity.hasAttribute(LSDAttribute.IMAGE_URL)) {
            entity.copyAttribute(fountainEntity, LSDAttribute.IMAGE_URL);
        } else if (boardFountainEntity != null && fountainEntity.hasAttribute(LSDAttribute.ICON_URL)) {
            entity.setAttribute(LSDAttribute.IMAGE_URL, boardFountainEntity.getAttribute(LSDAttribute.IMAGE_URL));
        }
        //The author of this update is the owner of the entity :-)
        final FountainRelationship editorRelationship = fountainEntity.getSingleRelationship(FountainRelationships.EDITOR, Direction.OUTGOING);
        if (editorRelationship != null) {
            final FountainEntity ownerFountainEntity = editorRelationship.getOtherNode(fountainEntity);
            final LSDEntity authorEntity = userDAO.getAliasFromNode(ownerFountainEntity, false, resultDetail);
            entity.addSubEntity(LSDAttribute.AUTHOR, authorEntity, true);
//            entity.setAttribute(LSDAttribute.TITLE, (String) ownerNode.getProperty(LSDAttribute.NAME.getKeyName()));
//            if (ownerNode.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
//                entity.setAttribute(LSDAttribute.ICON_URL, (String) ownerNode.getProperty(LSDAttribute.IMAGE_URL.getKeyName()));
//            }
            if (boardFountainEntity != null && authorEntity != null) {
                entity.setAttribute(LSDAttribute.SOURCE, boardFountainEntity.getURI());
                entity.setAttribute(LSDAttribute.TITLE, String.format("@%s made changes to '%s'", authorEntity.getAttribute(LSDAttribute.NAME), boardFountainEntity.getAttribute(LSDAttribute.TITLE, boardFountainEntity.getAttribute(LSDAttribute.NAME, "Unknown"))));
            }
        }


        final FountainRelationship viewRelationship = fountainEntity.getSingleRelationship(FountainRelationships.VIEW, Direction.OUTGOING);
        if (viewRelationship != null) {
            entity.addSubEntity(LSDAttribute.VIEW, viewRelationship.getOtherNode(fountainEntity).convertNodeToLSD(resultDetail, false), true);
        }

        //The URI keeps it unique in the stream
        entity.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.status, "object:" + fountainEntity.getURI()).asString());
        return entity;

    }


    @Nonnull
    private LSDEntity fromSessionNode(@Nonnull final FountainEntity sessionFountainEntity, @Nonnull final LiquidRequestDetailLevel resultDetail) throws InterruptedException {
        final LSDEntity entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.PRESENCE_UPDATE, UUIDFactory.randomUUID());
        final FountainRelationship ownerRelationship = sessionFountainEntity.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
        if (ownerRelationship == null) {
            throw new NullPointerException("Attempted to use a null ownerRelationship in 'fromSessionNode' in LatestContentFinder.");
        }
        final FountainEntity aliasFountainEntity = ownerRelationship.getOtherNode(sessionFountainEntity);
        entity.addSubEntity(LSDAttribute.AUTHOR, userDAO.getAliasFromNode(ownerRelationship.getOtherNode(sessionFountainEntity), false, resultDetail), true);
        final String aliasName = ownerRelationship.getOtherNode(sessionFountainEntity).getAttribute(LSDAttribute.NAME);
        final String aliasFullName = ownerRelationship.getOtherNode(sessionFountainEntity).getAttribute(LSDAttribute.FULL_NAME);
        entity.setAttribute(LSDAttribute.TITLE, aliasName);
        final Iterable<FountainRelationship> vistingRelationships = sessionFountainEntity.getRelationships(FountainRelationships.VISITING, Direction.OUTGOING);
        for (final FountainRelationship vistingRelationship : vistingRelationships) {
            final FountainEntity poolFountainEntity = vistingRelationship.getOtherNode(sessionFountainEntity);
            final LiquidURI poolUri = poolFountainEntity.getURI();

            entity.setAttribute(LSDAttribute.TITLE, '@' + aliasName + " was spotted in " + poolUri);
            entity.setAttribute(LSDAttribute.TEXT_BRIEF, aliasFullName + " was spotted in " + poolUri);
            entity.setAttribute(LSDAttribute.TEXT_EXTENDED, aliasFullName + " was spotted in " + poolUri);

            if (vistingRelationship.hasProperty(LSDAttribute.UPDATED.getKeyName())) {
                entity.setAttribute(LSDAttribute.PUBLISHED, (String) vistingRelationship.getProperty(LSDAttribute.UPDATED.getKeyName()));
                final long updated = Long.valueOf(vistingRelationship.getProperty(LSDAttribute.UPDATED.getKeyName()).toString());
                if (updated >= since) {
                    //it's stale so add to skip list now
                    skip.add(vistingRelationship.getOtherNode(sessionFountainEntity).getPersistenceId());
                }
            }
            entity.setAttribute(LSDAttribute.SOURCE, poolUri);
            entity.copyAttribute(aliasFountainEntity, LSDAttribute.IMAGE_URL);
            entity.copyAttribute(aliasFountainEntity, LSDAttribute.ICON_URL);
            //The URI keeps it unique in the stream
            entity.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.status, "presence:" + aliasName).asString());

        }
        return entity;

    }

    @Nonnull
    public Collection<LSDEntity> getNodes() {
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
