package cazcade.fountain.datastore.impl.graph;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.*;
import cazcade.fountain.datastore.impl.services.persistence.FountainEntityImpl;
import cazcade.liquid.api.*;
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
    public static final int CACHE_ERROR_MARGIN = 5000;
    public static final boolean INCLUDE_SESSION_INFORMATION = false;
    @Nonnull
    public static final String GLOBAL_SKIP = "global.skip";
    @Nonnull
    private static final Logger log = Logger.getLogger(LatestContentFinder.class);

    @Nonnull
    private static final Cache nodeCache;
    @Nonnull
    private final Map<String, LSDTransferEntity> nodes = new HashMap<String, LSDTransferEntity>();
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
    private final FountainUserDAO userDAO;
    private final long minAge = System.currentTimeMillis() - 7 * 24 * 3600 * 1000;

    static {
        if (!CacheManager.getInstance().cacheExists("latestcontent")) {
            CacheManager.getInstance().addCache("latestcontent");
        }
        nodeCache = CacheManager.getInstance().getCache("latestcontent");
    }

    public LatestContentFinder(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final FountainNeo fountainNeo,
                               @Nonnull final LSDPersistedEntity startPersistedEntity, final long since, final int maxReturnNodes,
                               final int maxTraversal, final LiquidRequestDetailLevel detail, final int maxDepth,
                               final FountainUserDAO userDAO) throws InterruptedException {
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
            }
            else {
                log.debug("No valid skip available");
            }
        }


        final Element globalSkipElement = nodeCache.get(GLOBAL_SKIP);
        if (globalSkipElement != null) {
            //noinspection unchecked
            globalSkip = (Set<Long>) globalSkipElement.getValue();
        }

//        findContentNodesNewMethod(startNode, true, false, fountainNeo.findByURI(identity.getAliasURL()), detail, 0);
        final LSDPersistedEntity aliasPersistedEntity = fountainNeo.findByURI(identity.getAliasURL());
        if (aliasPersistedEntity == null) {
            throw new EntityNotFoundException("Could not locate alias %s", identity.getAliasURL());
        }
        findContentNodesNewMethod(startPersistedEntity, aliasPersistedEntity, detail);

        final Element element = new Element(getCacheKey(), skip);
        nodeCache.put(element);
        nodeCache.put(new Element(GLOBAL_SKIP, globalSkip));
    }

    @SuppressWarnings({"OverlyComplexAnonymousInnerClass"})
    private void findContentNodesNewMethod(@Nonnull final LSDPersistedEntity startPersistedEntity,
                                           @Nonnull final LSDPersistedEntity myAliasPersistedEntity,
                                           @Nonnull final LiquidRequestDetailLevel resultDetail) throws InterruptedException {
        final int[] count = new int[1];
        final LiquidURI currentAliasURI = identity.getAliasURL();

        final Traverser traverser = startPersistedEntity.traverse(Traverser.Order.BREADTH_FIRST, new StopEvaluator() {
                                                                      @Override
                                                                      public boolean isStopNode(
                                                                              @Nonnull final TraversalPosition currentPos) {
                                                                          final LSDPersistedEntity currentPersistedEntity = new FountainEntityImpl(
                                                                                  currentPos.currentNode()
                                                                          );
                                                                          if (isUnlisted(currentPersistedEntity)) {
                                                                              return true;
                                                                          }

                                                                          if (currentPersistedEntity.hasRelationship(
                                                                                  FountainRelationships.COMMENT, Direction.INCOMING
                                                                                                                    )) {
                                                                              final FountainRelationship singleRelationship = currentPersistedEntity
                                                                                      .getSingleRelationship(
                                                                                              FountainRelationships.COMMENT,
                                                                                              Direction.INCOMING
                                                                                                            );
                                                                              assert singleRelationship != null;
                                                                              if (isUnlisted(singleRelationship.getOtherNode(
                                                                                      currentPersistedEntity
                                                                                                                            )
                                                                                            )) {
                                                                                  return true;
                                                                              }
                                                                          }
                                                                          final boolean result = currentPos.returnedNodesCount() >=
                                                                                                 maxReturnNodes * 10 ||
                                                                                                 count[0] >= maxTraversal ||
                                                                                                 currentPos.depth() > maxDepth;
                                                                          count[0]++;
                                                                          return result;
                                                                      }
                                                                  }, new ReturnableEvaluator() {
                                                                      @Override
                                                                      public boolean isReturnableNode(
                                                                              @Nonnull final TraversalPosition currentPos) {
                                                                          final LSDPersistedEntity currentPersistedEntity = new FountainEntityImpl(
                                                                                  currentPos.currentNode()
                                                                          );
                                                                          if (!skip.contains(
                                                                                  currentPersistedEntity.getPersistenceId()
                                                                                            ) && !globalSkip.contains(
                                                                                  currentPersistedEntity.getPersistenceId()
                                                                                                                     )) {
                                                                              try {
                                                                                  if (currentPersistedEntity.hasAttribute(
                                                                                          LSDAttribute.URI
                                                                                                                         )) {
                                                                                      final LiquidURI uri = currentPersistedEntity.getURI();
                                                                                      if (uri == null) {
                                                                                          throw new NullPointerException(
                                                                                                  "Attempted to use a null uri in 'isReturnableNode' in LatestContentFinder."
                                                                                          );
                                                                                      }
                                                                                      final LiquidURIScheme scheme = uri.getScheme();
                                                                                      //noinspection PointlessBooleanExpression,ConstantConditions
                                                                                      if (scheme == LiquidURIScheme.session &&
                                                                                          INCLUDE_SESSION_INFORMATION ||
                                                                                          scheme == LiquidURIScheme.comment ||
                                                                                          scheme == LiquidURIScheme.pool) {
                                                                                          if (currentPersistedEntity.isLatestVersion()) {
                                                                                              return validForThisRequest(
                                                                                                      currentPersistedEntity, uri,
                                                                                                      myAliasPersistedEntity,
                                                                                                      currentAliasURI
                                                                                                                        );
                                                                                          }
                                                                                          else {
                                                                                              log.debug(
                                                                                                      "FountainEntityImpl not latest version: " +
                                                                                                      uri
                                                                                                       );
                                                                                          }
                                                                                      }
                                                                                      else {
                                                                                          log.debug(
                                                                                                  "FountainEntityImpl is of the wrong type: " +
                                                                                                  uri
                                                                                                   );
                                                                                      }
                                                                                  }
                                                                                  else {
                                                                                      log.debug("No  URI key");
                                                                                  }
                                                                              } catch (InterruptedException e) {
                                                                                  log.error(e.getMessage(), e);
                                                                              }
                                                                              globalSkip.add(
                                                                                      currentPersistedEntity.getPersistenceId()
                                                                                            );
                                                                              return false;
                                                                          }
                                                                          else {
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
            final LSDPersistedEntity candidatePersistedEntity = new FountainEntityImpl(cNode);
            final LiquidURI uri = candidatePersistedEntity.getURI();
            if (uri == null) {
                throw new NullPointerException("Attempted to use a null uri in 'findContentNodesNewMethod' in LatestContentFinder."
                );
            }
            final LSDTransferEntity entity;
            //noinspection PointlessBooleanExpression,ConstantConditions
            if (candidatePersistedEntity.canBe(LSDDictionaryTypes.SESSION) && INCLUDE_SESSION_INFORMATION) {
                entity = fromSessionNode(candidatePersistedEntity, resultDetail);
            }
            else if (candidatePersistedEntity.canBe(LSDDictionaryTypes.COMMENT)) {
                entity = fromComment(candidatePersistedEntity, resultDetail);
            }
            else if (uri.getScheme() == LiquidURIScheme.pool) {
                entity = fromObjectNode(candidatePersistedEntity, resultDetail);
            }
            else {
                log.debug("FountainEntityImpl {0} was filtered because of type {1}", uri, candidatePersistedEntity.getTypeDef());
                continue;
            }
            if (entity.hasAttribute(LSDAttribute.TITLE)) {
                final String key = entity.getSubAttribute(LSDAttribute.AUTHOR, LSDAttribute.NAME, "") + ':' + entity.getAttribute(
                        LSDAttribute.SOURCE, ""
                                                                                                                                 );
                if (nodes.containsKey(key)) {
                    if (entity.wasPublishedAfter(nodes.get(key))) {
                        nodes.put(key, entity);
                        log.debug("FountainEntityImpl replaced with new version: " + key);
                    }
                    log.debug("FountainEntityImpl filtered, key not unique: " + key);
                }
                else {
                    if (nodes.size() < maxReturnNodes) {
                        nodes.put(key, entity);
                    }
                }
            }
            else {
                log.debug("FountainEntityImpl had no title, so filtered.");
            }
        }
        log.debug("Filtered to " + nodes.size() + " nodes. Max nodes is " + maxReturnNodes);
    }

    @SuppressWarnings({"MethodOnlyUsedFromInnerClass"})
    private static boolean isUnlisted(@Nonnull final LSDBaseEntity currentNode) {
        return (currentNode.canBe(LSDDictionaryTypes.POOL2D) || currentNode.canBe(LSDDictionaryTypes.BOARD)) &&
               !currentNode.getBooleanAttribute(LSDAttribute.LISTED, false);
    }

    @SuppressWarnings({"MethodOnlyUsedFromInnerClass"})
    private boolean validForThisRequest(@Nonnull final LSDPersistedEntity currentPersistedEntity, @Nonnull final LiquidURI uri,
                                        @Nonnull final LSDPersistedEntity myAliasPersistedEntity,
                                        @Nonnull final LiquidURI currentAliasURI) throws InterruptedException {
        final LiquidURIScheme scheme = uri.getScheme();
        final Date updated = currentPersistedEntity.getUpdated();
        if (updated == null) {
            throw new NullPointerException("Attempted to use a null updated in 'validForThisRequest' in LatestContentFinder.");
        }
        final Long nodeAge = updated.getTime();
        if (nodeAge >= since) {
            if (currentPersistedEntity.isAuthorized(identity, LiquidPermission.VIEW)) {
                final boolean authoredByMe = currentPersistedEntity.isAuthor(myAliasPersistedEntity);
                if (!authoredByMe) {
                    if (scheme == LiquidURIScheme.session) {
                        if (!uri.equals(currentAliasURI) && !CommonConstants.ANONYMOUS_ALIAS.equals(uri.asString())) {
                            if (currentPersistedEntity.getBooleanAttribute(LSDAttribute.ACTIVE)
                                && currentPersistedEntity.hasRelationship(FountainRelationships.VISITING, Direction.OUTGOING)) {
                                return true;
                            }
                            else {
                                log.debug("FountainEntityImpl was a session but not of interest: " + uri);
                            }
                        }
                        else {
                            log.debug("FountainEntityImpl was a session but was own or anonymous: " + uri);
                        }
                    }
                    else if (uri.asString().startsWith("pool") || scheme == LiquidURIScheme.comment) {
                        if (currentPersistedEntity.isListed()) {
                            return true;
                        }
                        else {
                            log.debug("FountainEntityImpl not listed: " + uri);
                            globalSkip.add(currentPersistedEntity.getPersistenceId());
                            return false;
                        }
                    }
                    else {
                        throw new IllegalStateException("Should not reach this line.");
                    }
                }
                else {
                    log.debug("FountainEntityImpl authored by me: " + uri);
                }
            }
            else {
                log.debug("FountainEntityImpl is not authorized: " + uri);
            }
        }
        else {
            if (nodeAge < minAge) {
                globalSkip.add(currentPersistedEntity.getPersistenceId());
                log.debug("FountainEntityImpl is too old to be of interest " + nodeAge + " < " + minAge);
            }
            else {
                log.debug("FountainEntityImpl is too old for this request " + nodeAge + " < " + since);
            }
        }
        skip.add(currentPersistedEntity.getPersistenceId());
        return false;
    }

    @Nonnull
    private LSDTransferEntity fromSessionNode(@Nonnull final LSDPersistedEntity sessionPersistedEntity,
                                              @Nonnull final LiquidRequestDetailLevel resultDetail) throws InterruptedException {
        final LSDTransferEntity entity = LSDSimpleEntity.createNewTransferEntity(LSDDictionaryTypes.PRESENCE_UPDATE,
                                                                                 UUIDFactory.randomUUID()
                                                                                );
        final FountainRelationship ownerRelationship = sessionPersistedEntity.getSingleRelationship(FountainRelationships.OWNER,
                                                                                                    Direction.OUTGOING
                                                                                                   );
        if (ownerRelationship == null) {
            throw new NullPointerException("Attempted to use a null ownerRelationship in 'fromSessionNode' in LatestContentFinder."
            );
        }
        final LSDPersistedEntity aliasPersistedEntity = ownerRelationship.getOtherNode(sessionPersistedEntity);
        entity.addSubEntity(LSDAttribute.AUTHOR, userDAO.getAliasFromNode(ownerRelationship.getOtherNode(sessionPersistedEntity),
                                                                          false, resultDetail
                                                                         ), true
                           );
        final String aliasName = ownerRelationship.getOtherNode(sessionPersistedEntity).getAttribute(LSDAttribute.NAME);
        final String aliasFullName = ownerRelationship.getOtherNode(sessionPersistedEntity).getAttribute(LSDAttribute.FULL_NAME);
        entity.setAttribute(LSDAttribute.TITLE, aliasName);
        final Iterable<FountainRelationship> vistingRelationships = sessionPersistedEntity.getRelationships(
                FountainRelationships.VISITING, Direction.OUTGOING
                                                                                                           );
        for (final FountainRelationship vistingRelationship : vistingRelationships) {
            final LSDPersistedEntity poolPersistedEntity = vistingRelationship.getOtherNode(sessionPersistedEntity);
            final LiquidURI poolUri = poolPersistedEntity.getURI();

            entity.setAttribute(LSDAttribute.TITLE, '@' + aliasName + " was spotted in " + poolUri);
            entity.setAttribute(LSDAttribute.TEXT_BRIEF, aliasFullName + " was spotted in " + poolUri);
            entity.setAttribute(LSDAttribute.TEXT_EXTENDED, aliasFullName + " was spotted in " + poolUri);

            if (vistingRelationship.hasProperty(LSDAttribute.UPDATED.getKeyName())) {
                entity.setAttribute(LSDAttribute.PUBLISHED, (String) vistingRelationship.getProperty(
                        LSDAttribute.UPDATED.getKeyName()
                                                                                                    )
                                   );
                final long updated = Long.valueOf(vistingRelationship.getProperty(LSDAttribute.UPDATED.getKeyName()).toString());
                if (updated >= since) {
                    //it's stale so add to skip list now
                    skip.add(vistingRelationship.getOtherNode(sessionPersistedEntity).getPersistenceId());
                }
            }
            entity.setAttribute(LSDAttribute.SOURCE, poolUri);
            entity.copyAttribute(aliasPersistedEntity, LSDAttribute.IMAGE_URL);
            entity.copyAttribute(aliasPersistedEntity, LSDAttribute.ICON_URL);
            //The URI keeps it unique in the stream
            entity.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.status, "presence:" + aliasName).asString());
        }
        return entity;
    }

    @Nonnull
    private LSDTransferEntity fromComment(@Nonnull final LSDPersistedEntity persistedEntity,
                                          @Nonnull final LiquidRequestDetailLevel resultDetail) throws InterruptedException {
        final LSDTransferEntity entity = LSDSimpleEntity.createNewTransferEntity(LSDDictionaryTypes.COMMENT_UPDATE,
                                                                                 UUIDFactory.randomUUID()
                                                                                );
        entity.setAttribute(LSDAttribute.TEXT_BRIEF, persistedEntity.getAttribute(LSDAttribute.TEXT_BRIEF));
        entity.setPublished(persistedEntity.getUpdated());
        entity.setAttribute(LSDAttribute.TEXT_EXTENDED, persistedEntity.getAttribute(LSDAttribute.TEXT_EXTENDED));

        final LiquidURI objectURI = persistedEntity.getURI();
        final FountainRelationship authorRelationship = persistedEntity.getSingleRelationship(FountainRelationships.AUTHOR,
                                                                                              Direction.OUTGOING
                                                                                             );
        LSDTransferEntity aliasEntity = null;
        if (authorRelationship != null) {
            final LSDPersistedEntity authorPersistedEntity = authorRelationship.getOtherNode(persistedEntity);
            aliasEntity = userDAO.getAliasFromNode(authorPersistedEntity, false, resultDetail);
            entity.addSubEntity(LSDAttribute.AUTHOR, aliasEntity, true);
//            entity.setAttribute(LSDAttribute.TITLE, (String) authorNode.getProperty(LSDAttribute.NAME.getKeyName()));
//            if (authorNode.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
//                entity.setAttribute(LSDAttribute.ICON_URL, (String) authorNode.getProperty(LSDAttribute.IMAGE_URL.getKeyName()));
//            }
        }
        LSDPersistedEntity poolOrObjectPersistedEntity = null;
        final Traverser traverse = persistedEntity.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH,
                                                            new ReturnableEvaluator() {
                                                                @Override
                                                                public boolean isReturnableNode(
                                                                        @Nonnull final TraversalPosition currentPos) {
                                                                    return !new FountainEntityImpl(currentPos.currentNode()).canBe(
                                                                            LSDDictionaryTypes.COMMENT
                                                                                                                                  );
                                                                }
                                                            }, FountainRelationships.PREVIOUS, Direction.INCOMING,
                                                            FountainRelationships.COMMENT, Direction.INCOMING
                                                           );
        final Collection<org.neo4j.graphdb.Node> nonCommentNodes = traverse.getAllNodes();
        if (!nonCommentNodes.isEmpty()) {
            poolOrObjectPersistedEntity = new FountainEntityImpl(nonCommentNodes.iterator().next());
        }
        if (poolOrObjectPersistedEntity != null) {
            entity.setAttribute(LSDAttribute.SOURCE, poolOrObjectPersistedEntity.getURI());
            entity.copyAttribute(poolOrObjectPersistedEntity, LSDAttribute.IMAGE_URL);
            entity.copyAttribute(poolOrObjectPersistedEntity, LSDAttribute.ICON_URL);
            if (aliasEntity != null && poolOrObjectPersistedEntity.isListed()) {
                entity.setAttribute(LSDAttribute.TITLE, String.format("@%s commented on '%s'", aliasEntity.getAttribute(
                        LSDAttribute.NAME
                                                                                                                       ),
                                                                      poolOrObjectPersistedEntity.getAttribute(LSDAttribute.TITLE,
                                                                                                               poolOrObjectPersistedEntity
                                                                                                                       .getAttribute(
                                                                                                                               LSDAttribute.NAME,
                                                                                                                               "Unknown"
                                                                                                                                    )
                                                                                                              )
                                                                     )
                                   );
            }
        }
        //The URI keeps it unique in the stream
        entity.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.status, "comment:" + objectURI).asString());
        return entity;
    }

    @Nonnull
    private LSDTransferEntity fromObjectNode(@Nonnull final LSDPersistedEntity persistedEntity,
                                             @Nonnull final LiquidRequestDetailLevel resultDetail) throws InterruptedException {
        final LSDTransferEntity entity = LSDSimpleEntity.createNewTransferEntity(LSDDictionaryTypes.OBJECT_UPDATE,
                                                                                 UUIDFactory.randomUUID()
                                                                                );
        LSDPersistedEntity boardPersistedEntity = null;
        final Iterator<org.neo4j.graphdb.Node> parentIterator = persistedEntity.traverse(Traverser.Order.DEPTH_FIRST,
                                                                                         StopEvaluator.END_OF_GRAPH,
                                                                                         new ReturnableEvaluator() {
                                                                                             @Override
                                                                                             public boolean isReturnableNode(
                                                                                                     @Nonnull final TraversalPosition currentPos) {
                                                                                                 return new FountainEntityImpl(
                                                                                                         currentPos.currentNode()
                                                                                                 ).canBe(LSDDictionaryTypes.BOARD);
                                                                                             }
                                                                                         }, FountainRelationships.CHILD,
                                                                                         Direction.INCOMING
                                                                                        ).iterator();
        if (parentIterator.hasNext()) {
            boardPersistedEntity = new FountainEntityImpl(parentIterator.next());
        }
        if (persistedEntity.hasAttribute(LSDAttribute.MOTIVE_TEXT)) {
            final String motiveText = persistedEntity.getAttribute(LSDAttribute.MOTIVE_TEXT);
            if (motiveText.isEmpty() && persistedEntity.hasAttribute(LSDAttribute.TEXT_EXTENDED)) {
                final String extendedText = persistedEntity.getAttribute(LSDAttribute.TEXT_EXTENDED);
                entity.setAttribute(LSDAttribute.TEXT_BRIEF, extendedText);
                entity.setAttribute(LSDAttribute.TEXT_EXTENDED, extendedText);
            }
            else {
                entity.setAttribute(LSDAttribute.TEXT_BRIEF, "updated");
                entity.setAttribute(LSDAttribute.TEXT_EXTENDED, "updated");
            }
            entity.setAttribute(LSDAttribute.TEXT_BRIEF, motiveText);
            entity.setAttribute(LSDAttribute.TEXT_EXTENDED, motiveText);
        }
        entity.setPublished(persistedEntity.getUpdated());
        entity.setAttribute(LSDAttribute.SOURCE, persistedEntity.getURI());
        if (persistedEntity.hasAttribute(LSDAttribute.IMAGE_URL)) {
            entity.copyAttribute(persistedEntity, LSDAttribute.IMAGE_URL);
        }
        else if (boardPersistedEntity != null && persistedEntity.hasAttribute(LSDAttribute.ICON_URL)) {
            entity.setAttribute(LSDAttribute.IMAGE_URL, boardPersistedEntity.getAttribute(LSDAttribute.IMAGE_URL));
        }
        //The author of this update is the owner of the entity :-)
        final FountainRelationship editorRelationship = persistedEntity.getSingleRelationship(FountainRelationships.EDITOR,
                                                                                              Direction.OUTGOING
                                                                                             );
        if (editorRelationship != null) {
            final LSDPersistedEntity ownerPersistedEntity = editorRelationship.getOtherNode(persistedEntity);
            final LSDTransferEntity authorEntity = userDAO.getAliasFromNode(ownerPersistedEntity, false, resultDetail);
            entity.addSubEntity(LSDAttribute.AUTHOR, authorEntity, true);
//            entity.setAttribute(LSDAttribute.TITLE, (String) ownerNode.getProperty(LSDAttribute.NAME.getKeyName()));
//            if (ownerNode.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
//                entity.setAttribute(LSDAttribute.ICON_URL, (String) ownerNode.getProperty(LSDAttribute.IMAGE_URL.getKeyName()));
//            }
            if (boardPersistedEntity != null && authorEntity != null) {
                entity.setAttribute(LSDAttribute.SOURCE, boardPersistedEntity.getURI());
                entity.setAttribute(LSDAttribute.TITLE, String.format("@%s made changes to '%s'", authorEntity.getAttribute(
                        LSDAttribute.NAME
                                                                                                                           ),
                                                                      boardPersistedEntity.getAttribute(LSDAttribute.TITLE,
                                                                                                        boardPersistedEntity.getAttribute(
                                                                                                                LSDAttribute.NAME,
                                                                                                                "Unknown"
                                                                                                                                         )
                                                                                                       )
                                                                     )
                                   );
            }
        }


        final FountainRelationship viewRelationship = persistedEntity.getSingleRelationship(FountainRelationships.VIEW,
                                                                                            Direction.OUTGOING
                                                                                           );
        if (viewRelationship != null) {
            entity.addSubEntity(LSDAttribute.VIEW, viewRelationship.getOtherNode(persistedEntity).convertNodeToLSD(resultDetail,
                                                                                                                   false
                                                                                                                  ), true
                               );
        }

        //The URI keeps it unique in the stream
        entity.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.status, "object:" + persistedEntity.getURI()
        ).asString()
                           );
        return entity;
    }

    @Nonnull
    public String getCacheKey() {
        return identity.toString() + ':' + ':' + maxTraversal + ':' + detail + ':' + maxDepth;
    }

    @Nonnull
    public Collection<LSDTransferEntity> getNodes() {
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
