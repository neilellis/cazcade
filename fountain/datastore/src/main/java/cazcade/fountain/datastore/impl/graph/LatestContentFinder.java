package cazcade.fountain.datastore.impl.graph;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.FountainUserDAO;
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

import java.util.*;

/**
 * Starting from a particular node, walk the graph based on a predefined algorithm.
 * <p/>
 * The algorithm basically looks for the most strongly related content first, such as children, link children and owned content. Then drills down to
 * children, then starts going back up to parents. This algorithm will need constant review and tuning to get right of course. It pretty much guarantees to find 'n' nodes of interest no matter how many nodes it needs to traverse.
 *
 * @author neilellis@cazcade.com
 */
public class LatestContentFinder {
    private final static Logger log = Logger.getLogger(LatestContentFinder.class);
    public static final int CACHE_ERROR_MARGIN = 5000;
    public static final boolean INCLUDE_SESSION_INFORMATION = false;
    private Map<Long, LSDEntity> nodes = new HashMap<Long, LSDEntity>();
    private Set<Long> visited = new HashSet<Long>();
    private Set<Long> skip = new HashSet<Long>();
    private LiquidSessionIdentifier identity;
    private FountainNeo fountainNeo;
    private long since;
    private int maxReturnNodes;
    private int maxTraversal;
    private LiquidRequestDetailLevel detail;
    private int maxDepth;

    private static Cache nodeCache;
    private FountainUserDAO userDAO;

    static {
        if (!CacheManager.getInstance().cacheExists("latestcontent")) {
            CacheManager.getInstance().addCache("latestcontent");
        }
        nodeCache = CacheManager.getInstance().getCache("latestcontent");

    }

    public LatestContentFinder(LiquidSessionIdentifier identity, FountainNeo fountainNeo, Node startNode, long since, int maxReturnNodes, int maxTraversal, LiquidRequestDetailLevel detail, int maxDepth, FountainUserDAO theUserDAO) throws InterruptedException {
        this.identity = identity;
        this.fountainNeo = fountainNeo;
        this.userDAO = theUserDAO;
        this.since = since;
        this.maxReturnNodes = maxReturnNodes;
        this.maxTraversal = maxTraversal;
        this.detail = detail;
        this.maxDepth = maxDepth;
        final Element element = nodeCache.get(getCacheKey());
        if (element != null) {
            //The plus 5 seconds is because we need a margin of error
            if (element.getCreationTime() < since + CACHE_ERROR_MARGIN) {
                skip = (Set<Long>) element.getValue();
            }
        }
//        findContentNodesNewMethod(startNode, true, false, fountainNeo.findByURI(identity.getAliasURL()), detail, 0);
        findContentNodesNewMethod(startNode, fountainNeo.findByURI(identity.getAliasURL()), detail);
        nodeCache.put(new Element(getCacheKey(), skip, 0));
    }

    public String getCacheKey() {
        return identity.getSession().toString() + ":" + ":" + maxTraversal + ":" + detail + ":" + maxDepth;
    }

    public void findContentNodesNewMethod(final Node startNode, final Node ownerNode, final LiquidRequestDetailLevel detail) throws InterruptedException {
        final int[] count = new int[1];
        final String currentAliasURI = identity.getAliasURL().asString();

        final Traverser traverser = startNode.traverse(Traverser.Order.BREADTH_FIRST, new StopEvaluator() {
                    @Override
                    public boolean isStopNode(TraversalPosition currentPos) {
                        return currentPos.returnedNodesCount() >= maxReturnNodes * 4 || count[0]++ >= maxTraversal;
                    }
                }, new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(TraversalPosition currentPos) {
                        Node currentNode = currentPos.currentNode();
                        if (!skip.contains(currentNode.getId())) {
                            try {
                                if (currentNode.hasProperty(LSDAttribute.UPDATED.getKeyName()) && currentNode.hasProperty(LSDAttribute.URI.getKeyName())) {
                                    long updated = Long.valueOf(currentNode.getProperty(LSDAttribute.UPDATED.getKeyName()).toString());
                                    String uri = (String) currentNode.getProperty(LSDAttribute.URI.getKeyName());
                                    if (updated >= since) {
                                        boolean kosher = uri.startsWith(LiquidURIScheme.session.toString()) ||
                                                uri.startsWith(LiquidURIScheme.comment.toString()) ||
                                                uri.startsWith(LiquidURIScheme.pool.toString());
                                        if (!kosher) {
                                            //wrong type so add to skip list
                                            skip.add(currentNode.getId());
                                            return false;
                                        } else {
                                            if (fountainNeo.isAuthorized(currentNode, identity, LiquidPermission.VIEW)) {
                                                boolean ownedByMe = fountainNeo.isOwnerNoTX(ownerNode, currentNode);
                                                if (!uri.equals(currentAliasURI) && !CommonConstants.ANONYMOUS_ALIAS.equals(uri) && uri.startsWith(LiquidURIScheme.session.toString())) {
                                                    return !ownedByMe && currentNode.hasProperty(LSDAttribute.ACTIVE.getKeyName())
                                                            && currentNode.getProperty(LSDAttribute.ACTIVE.getKeyName()).equals("true")
                                                            && currentNode.hasRelationship(FountainRelationships.VISITING, Direction.OUTGOING);

                                                } else if (!ownedByMe && uri.startsWith("pool")) {
                                                    return true;
                                                } else if (uri.startsWith(LiquidURIScheme.comment.toString())) {
                                                    return true;
                                                }
                                            } else {
                                                skip.add(currentNode.getId());
                                            }

                                        }
                                    } else if (uri.startsWith(LiquidURIScheme.pool.toString())) {
                                        //pool nodes never get updated so we can add this to the skip list.
                                        skip.add(currentNode.getId());
                                    }
                                } else {
                                    //not valid, add to the skip list
                                    skip.add(currentNode.getId());
                                }

                            } catch (InterruptedException e) {
                                log.error(e.getMessage(), e);
                            }
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
//                FountainRelationships.COMMENT, Direction.OUTGOING,
//                FountainRelationships.VISITING, Direction.BOTH,
                FountainRelationships.FOLLOW_CONTENT, Direction.OUTGOING,
                FountainRelationships.FOLLOW_ALIAS, Direction.OUTGOING
        );
        final Collection<Node> candidateNodes = traverser.getAllNodes();
        log.debug("Found " + candidateNodes.size() + " nodes:");
        for (Node candidateNode : candidateNodes) {
            String type = (String) candidateNode.getProperty(LSDAttribute.TYPE.getKeyName());
            boolean ownedByMe = fountainNeo.isOwnerNoTX(ownerNode, candidateNode);

            if (type.startsWith(LSDDictionaryTypes.SESSION.getValue()) && INCLUDE_SESSION_INFORMATION) {
                nodes.put(candidateNode.getId(), fromSessionNode(candidateNode, detail));
            } else if (type.startsWith(LSDDictionaryTypes.COMMENT.getValue())) {
                nodes.put(candidateNode.getId(), fromComment(candidateNode, detail));
            } else if (!ownedByMe && candidateNode.hasProperty(LSDAttribute.URI.getKeyName()) && candidateNode.getProperty(LSDAttribute.URI.getKeyName()).toString().startsWith("pool")) {
                nodes.put(candidateNode.getId(), fromObjectNode(candidateNode, detail));
            }
        }
        log.debug("Filtered to " + nodes.size() + " nodes:");
    }

    public void findContentNodes(Node currentNode, boolean iterateChildren, boolean revisit, Node ownerNode, LiquidRequestDetailLevel detail, final int depth) throws InterruptedException {

        //be careful to access nothing but the node id of a node so as not to trigger the paging in by neo4j of the nodes properties
        if (depth > maxDepth) {
            return;
        }
        if (!revisit && visited.contains(currentNode.getId())) {
            return;
        } else {
            visited.add(currentNode.getId());
        }
        if (visited.size() == maxTraversal) {
            return;
        }
        if (nodes.size() == maxReturnNodes) {
            return;
        }

        //after the skip check is made references to node properties is okay
        if (!skip.contains(currentNode.getId())) {
            skip.add(currentNode.getId());
            if (fountainNeo.isAuthorized(currentNode, this.identity, LiquidPermission.VIEW)) {
                if (currentNode.hasProperty(LSDAttribute.UPDATED.getKeyName())) {
                    long updated = Long.valueOf(currentNode.getProperty(LSDAttribute.UPDATED.getKeyName()).toString());
                    boolean ownedByMe = fountainNeo.isOwnerNoTX(ownerNode, currentNode);
                    if (updated >= since) {
                        String type = (String) currentNode.getProperty(LSDAttribute.TYPE.getKeyName());
                        if (type.startsWith(LSDDictionaryTypes.SESSION.getValue())) {
                            if (!ownedByMe && currentNode.hasProperty(LSDAttribute.ACTIVE.getKeyName())
                                    && currentNode.getProperty(LSDAttribute.ACTIVE.getKeyName()).equals("true")
                                    && currentNode.hasRelationship(FountainRelationships.VISITING, Direction.OUTGOING)) {
                                nodes.put(currentNode.getId(), fromSessionNode(currentNode, detail));
                            }

                        } else if (type.startsWith(LSDDictionaryTypes.COMMENT.getValue())) {
                            nodes.put(currentNode.getId(), fromComment(currentNode, detail));
                        } else if (!ownedByMe && currentNode.hasProperty(LSDAttribute.URI.getKeyName()) && currentNode.getProperty(LSDAttribute.URI.getKeyName()).toString().startsWith("pool")) {
                            nodes.put(currentNode.getId(), fromObjectNode(currentNode, detail));
                        }

                    }
                }
            }
        }


//        if (!iterateChildren) {
//            return;
//        }
        final Iterable<Relationship> children = currentNode.getRelationships(FountainRelationships.CHILD, Direction.OUTGOING);
        final Iterable<Relationship> linkedChildren = currentNode.getRelationships(FountainRelationships.LINKED_CHILD, Direction.OUTGOING);
        final Iterable<Relationship> owneds /*I know it's not a word, okay!*/ = currentNode.getRelationships(FountainRelationships.OWNER, Direction.OUTGOING);
        final Iterable<Relationship> comments = currentNode.getRelationships(FountainRelationships.COMMENT, Direction.OUTGOING);

        final Iterable<Relationship> parents = currentNode.getRelationships(FountainRelationships.CHILD, Direction.INCOMING);
        final Iterable<Relationship> owners = currentNode.getRelationships(FountainRelationships.OWNER, Direction.INCOMING);
        final Iterable<Relationship> visitors = currentNode.getRelationships(FountainRelationships.VISITING, Direction.INCOMING);

        for (Relationship child : children) {
            findContentNodes(child.getOtherNode(currentNode), false, false, ownerNode, detail, depth + 1);
        }
        for (Relationship linkedChild : linkedChildren) {
            findContentNodes(linkedChild.getOtherNode(currentNode), false, false, ownerNode, detail, depth + 1);
        }
        for (Relationship owned : owneds) {
            findContentNodes(owned.getOtherNode(currentNode), false, false, ownerNode, detail, depth + 1);
        }
        for (Relationship comment : comments) {
            findContentNodes(comment.getOtherNode(currentNode), false, false, ownerNode, detail, depth + 1);
        }
        for (Relationship child : children) {
            findContentNodes(child.getOtherNode(currentNode), true, true, ownerNode, detail, depth + 1);
        }
        for (Relationship linkedChild : linkedChildren) {
            findContentNodes(linkedChild.getOtherNode(currentNode), true, true, ownerNode, detail, depth + 1);
        }
        for (Relationship owned : owneds) {
            findContentNodes(owned.getOtherNode(currentNode), true, true, ownerNode, detail, depth + 1);
        }
        for (Relationship parent : parents) {
            findContentNodes(parent.getOtherNode(currentNode), true, false, ownerNode, detail, depth + 1);
        }
        for (Relationship visitor : visitors) {
            if (visitor.hasProperty(LSDAttribute.UPDATED.getKeyName())) {
                long updated = Long.valueOf(visitor.getProperty(LSDAttribute.UPDATED.getKeyName()).toString());
                if (updated >= since) {
                    findContentNodes(visitor.getOtherNode(currentNode), true, false, ownerNode, detail, depth + 1);
                }
            }
        }
        for (Relationship owner : owners) {
            findContentNodes(owner.getOtherNode(currentNode), true, false, ownerNode, detail, depth + 1);
        }

    }

    private LSDEntity fromComment(Node node, LiquidRequestDetailLevel detail) throws InterruptedException {
        final LSDEntity entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.COMMENT_UPDATE, UUIDFactory.randomUUID());
        if (node.hasProperty(LSDAttribute.TEXT_EXTENDED.getKeyName())) {
            entity.setAttribute(LSDAttribute.TEXT_EXTENDED, (String) node.getProperty(LSDAttribute.TEXT_EXTENDED.getKeyName()));
        }
        if (node.hasProperty(LSDAttribute.TEXT_BRIEF.getKeyName())) {
            entity.setAttribute(LSDAttribute.TEXT_BRIEF, (String) node.getProperty(LSDAttribute.TEXT_BRIEF.getKeyName()));
        }
        if (node.hasProperty(LSDAttribute.UPDATED.getKeyName())) {
            entity.setAttribute(LSDAttribute.PUBLISHED, (String) node.getProperty(LSDAttribute.UPDATED.getKeyName()));
        }
        final String objectURI = (String) node.getProperty(LSDAttribute.URI.getKeyName());
        Relationship authorRelationship = node.getSingleRelationship(FountainRelationships.AUTHOR, Direction.OUTGOING);
        if (authorRelationship != null) {
            final Node authorNode = authorRelationship.getOtherNode(node);
            entity.addSubEntity(LSDAttribute.AUTHOR, userDAO.getAliasFromNode(authorNode, false, detail), true);
            entity.setAttribute(LSDAttribute.TITLE, (String) authorNode.getProperty(LSDAttribute.NAME.getKeyName()));
            if (authorNode.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
                entity.setAttribute(LSDAttribute.ICON_URL, (String) authorNode.getProperty(LSDAttribute.IMAGE_URL.getKeyName()));
            }
        }
        Relationship targetRelationship = node.getSingleRelationship(FountainRelationships.COMMENT, Direction.INCOMING);
        if (targetRelationship == null) {
            targetRelationship = node.getSingleRelationship(FountainRelationships.PREVIOUS, Direction.INCOMING);
        }
        if (targetRelationship != null) {
            Node targetNode = targetRelationship.getOtherNode(node);
            final String targetURI = (String) targetNode.getProperty(LSDAttribute.URI.getKeyName());
            entity.setAttribute(LSDAttribute.SOURCE, targetURI);
            if (targetNode.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
                entity.setAttribute(LSDAttribute.IMAGE_URL, (String) targetNode.getProperty(LSDAttribute.IMAGE_URL.getKeyName()));
            }

        }
        //The URI keeps it unique in the stream
        entity.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.status, "comment:" + objectURI).asString());
        return entity;

    }


    private LSDEntity fromObjectNode(Node node, LiquidRequestDetailLevel detail) throws InterruptedException {
        final LSDEntity entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.OBJECT_UPDATE, UUIDFactory.randomUUID());
        if (node.hasProperty(LSDAttribute.MOTIVE_TEXT.getKeyName())) {
            final String motiveText = (String) node.getProperty(LSDAttribute.MOTIVE_TEXT.getKeyName());
            if (motiveText.isEmpty() && node.hasProperty(LSDAttribute.TEXT_EXTENDED.getKeyName())) {
                final String extendedText = (String) node.getProperty(LSDAttribute.TEXT_EXTENDED.getKeyName());
                entity.setAttribute(LSDAttribute.TEXT_BRIEF, extendedText);
                entity.setAttribute(LSDAttribute.TEXT_EXTENDED, extendedText);
            } else {
                entity.setAttribute(LSDAttribute.TEXT_BRIEF, "updated");
                entity.setAttribute(LSDAttribute.TEXT_EXTENDED, "updated");
            }
            entity.setAttribute(LSDAttribute.TEXT_BRIEF, motiveText);
            entity.setAttribute(LSDAttribute.TEXT_EXTENDED, motiveText);
        }
        if (node.hasProperty(LSDAttribute.UPDATED.getKeyName())) {
            entity.setAttribute(LSDAttribute.PUBLISHED, (String) node.getProperty(LSDAttribute.UPDATED.getKeyName()));
        }
        final String objectURI = (String) node.getProperty(LSDAttribute.URI.getKeyName());
        entity.setAttribute(LSDAttribute.SOURCE, objectURI);
        if (node.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
            entity.setAttribute(LSDAttribute.IMAGE_URL, (String) node.getProperty(LSDAttribute.IMAGE_URL.getKeyName()));
        }
        //The author of this update is the owner of the entity :-)
        Relationship editorRelationship = node.getSingleRelationship(FountainRelationships.EDITOR, Direction.OUTGOING);
        if (editorRelationship != null) {
            final Node ownerNode = editorRelationship.getOtherNode(node);
            entity.addSubEntity(LSDAttribute.AUTHOR, userDAO.getAliasFromNode(ownerNode, false, detail), true);
            entity.setAttribute(LSDAttribute.TITLE, (String) ownerNode.getProperty(LSDAttribute.NAME.getKeyName()));
            if (ownerNode.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
                entity.setAttribute(LSDAttribute.ICON_URL, (String) ownerNode.getProperty(LSDAttribute.IMAGE_URL.getKeyName()));
            }
        }
        Relationship viewRelationship = node.getSingleRelationship(FountainRelationships.VIEW, Direction.OUTGOING);
        if (viewRelationship != null) {
            entity.addSubEntity(LSDAttribute.VIEW, fountainNeo.convertNodeToLSD(viewRelationship.getOtherNode(node), detail, false), true);
        }

        //The URI keeps it unique in the stream
        entity.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.status, "object:" + objectURI).asString());
        return entity;

    }


    private LSDEntity fromSessionNode(Node sessionNode, LiquidRequestDetailLevel detail) throws InterruptedException {
        final LSDEntity entity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.PRESENCE_UPDATE, UUIDFactory.randomUUID());
        Relationship ownerRelationship = sessionNode.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
        Node aliasNode = ownerRelationship.getOtherNode(sessionNode);
        entity.addSubEntity(LSDAttribute.AUTHOR, userDAO.getAliasFromNode(ownerRelationship.getOtherNode(sessionNode), false, detail), true);
        final String aliasName = (String) ownerRelationship.getOtherNode(sessionNode).getProperty(LSDAttribute.NAME.getKeyName());
        final String aliasFullName = (String) ownerRelationship.getOtherNode(sessionNode).getProperty(LSDAttribute.FULL_NAME.getKeyName());
        entity.setAttribute(LSDAttribute.TITLE, aliasName);
        Iterable<Relationship> vistingRelationships = sessionNode.getRelationships(FountainRelationships.VISITING, Direction.OUTGOING);
        for (Relationship vistingRelationship : vistingRelationships) {
            Node poolNode = vistingRelationship.getOtherNode(sessionNode);
            final String poolUri = (String) poolNode.getProperty(LSDAttribute.URI.getKeyName());

            entity.setAttribute(LSDAttribute.TEXT_BRIEF, aliasFullName + " was spotted in " + poolUri);
            entity.setAttribute(LSDAttribute.TEXT_EXTENDED, aliasFullName + " was spotted in " + poolUri);

            if (vistingRelationship.hasProperty(LSDAttribute.UPDATED.getKeyName())) {
                entity.setAttribute(LSDAttribute.PUBLISHED, (String) vistingRelationship.getProperty(LSDAttribute.UPDATED.getKeyName()));
                long updated = Long.valueOf(vistingRelationship.getProperty(LSDAttribute.UPDATED.getKeyName()).toString());
                if (updated >= since) {
                    //it's stale so add to skip list now
                    skip.add(vistingRelationship.getOtherNode(sessionNode).getId());
                }
            }
            if (aliasNode.hasProperty(LSDAttribute.URI.getKeyName())) {
                entity.setAttribute(LSDAttribute.SOURCE, poolUri);
            }
            if (aliasNode.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
                entity.setAttribute(LSDAttribute.IMAGE_URL, (String) aliasNode.getProperty(LSDAttribute.IMAGE_URL.getKeyName()));
            }
            if (aliasNode.hasProperty(LSDAttribute.ICON_URL.getKeyName())) {
                entity.setAttribute(LSDAttribute.ICON_URL, (String) aliasNode.getProperty(LSDAttribute.ICON_URL.getKeyName()));
            }
            //The URI keeps it unique in the stream
            entity.setAttribute(LSDAttribute.URI, new LiquidURI(LiquidURIScheme.status, "presence:" + aliasName).asString());
            entity.setAttribute(LSDAttribute.SOURCE, poolUri);

        }
        return entity;

    }

    public Collection<LSDEntity> getNodes() {
        return nodes.values();
    }
}
