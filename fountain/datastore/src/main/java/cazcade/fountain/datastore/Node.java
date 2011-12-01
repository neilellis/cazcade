package cazcade.fountain.datastore;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.*;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.NodeCallback;
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
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import static cazcade.liquid.api.lsd.LSDAttribute.*;

/**
 * @author neilellis@cazcade.com
 */
@SuppressWarnings({"PublicMethodNotExposedInInterface"})
public class Node extends LSDSimpleEntity {

    @Nonnull
    private static final Logger log = Logger.getLogger(Node.class);

    private static final long serialVersionUID = -1697240636351951371L;


    private static Cache nodeAuthCache;

    static {
        if (!CacheManager.getInstance().cacheExists("nodeauth")) {
            CacheManager.getInstance().addCache("nodeauth");
        }
        nodeAuthCache = CacheManager.getInstance().getCache("nodeauth");

    }

    @Nonnull
    private final transient org.neo4j.graphdb.Node neoNode;

    public Node(@Nonnull final org.neo4j.graphdb.Node neoNode) {
        super(new NeoPropertyStore(neoNode));
        this.neoNode = neoNode;
    }


    public long getNeoId() {
        return neoNode.getId();
    }

    /**
     * This should rarely be called as we mostly do not delete nodes from Neo.
     * There are some exceptions (like session nodes). However even those exceptions
     * will be removed in future.
     */
    public void deleteNeo() {
        neoNode.delete();
    }


    @Nonnull
    public Iterable<Relationship> getRelationships() {
        final Iterable<org.neo4j.graphdb.Relationship> relationships = neoNode.getRelationships();
        return new RelationshipIterable(relationships);
    }


    @Nonnull
    public Iterable<Relationship> getRelationships(final FountainRelationships... types) {
        return new RelationshipIterable(neoNode.getRelationships(types));
    }


    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nonnull
    public Iterable<Relationship> getRelationships(final FountainRelationships type, final Direction dir) {
        return new RelationshipIterable(neoNode.getRelationships(type, dir));
    }


    @SuppressWarnings({"TypeMayBeWeakened"})
    public boolean hasRelationship(final FountainRelationships type, final Direction dir) {
        return neoNode.hasRelationship(type, dir);
    }


    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nullable
    public Relationship getSingleRelationship(final FountainRelationships type, final Direction dir) {
        final org.neo4j.graphdb.Relationship relationship = neoNode.getSingleRelationship(type, dir);
        if (relationship == null) {
            return null;
        }
        return new Relationship(relationship);
    }


    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nonnull
    public Relationship createRelationshipTo(@Nonnull final Node otherNode, final FountainRelationships type) {
        return new Relationship(neoNode.createRelationshipTo(otherNode.neoNode, type));
    }


    public Traverser traverse(final Traverser.Order traversalOrder, final StopEvaluator stopEvaluator, final ReturnableEvaluator returnableEvaluator, final FountainRelationships relationshipType, final Direction direction) {
        return neoNode.traverse(traversalOrder, stopEvaluator, returnableEvaluator, relationshipType, direction);
    }


    public Traverser traverse(final Traverser.Order traversalOrder, final StopEvaluator stopEvaluator, final ReturnableEvaluator returnableEvaluator, final RelationshipType firstRelationshipType, final Direction firstDirection, final RelationshipType secondRelationshipType, final Direction secondDirection) {
        return neoNode.traverse(traversalOrder, stopEvaluator, returnableEvaluator, firstRelationshipType, firstDirection, secondRelationshipType, secondDirection);
    }


    public Traverser traverse(final Traverser.Order traversalOrder, final StopEvaluator stopEvaluator, final ReturnableEvaluator returnableEvaluator, final Object... relationshipTypesAndDirections) {
        return neoNode.traverse(traversalOrder, stopEvaluator, returnableEvaluator, relationshipTypesAndDirections);
    }


    public String getProperty(@Nonnull final LSDAttribute key) {
        return getAttribute(key);
    }


    public String getProperty(@Nonnull final LSDAttribute key, @Nullable final String defaultValue) {
        return getAttribute(key, defaultValue);
    }


    public void setProperty(final LSDAttribute key, final String value) {
        setAttribute(key, value);
    }


    public void removeProperty(@Nonnull final LSDAttribute key) {
        remove(key);
    }


    public Iterable<String> getPropertyKeys() {
        return neoNode.getPropertyKeys();
    }


    @Nonnull
    public org.neo4j.graphdb.Node getNeoNode() {
        return neoNode;
    }

    @Nonnull
    public Node mergeProperties(@Nonnull final LSDEntity entity, final boolean update, final boolean ignoreType, @Nullable final Runnable onRenameAction) throws InterruptedException {
        if (hasAttribute(TYPE) && entity.hasAttribute(TYPE) && !ignoreType) {
            if (!isA(entity.getTypeDef())) {
                throw new CannotChangeTypeException("The entity type used to be %s the request was to change it to %s", getTypeDef(), entity.getTypeDef());
            }
        }
        final Map<String, String> map = entity.asMapForPersistence(ignoreType, update);
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals(ID.getKeyName())) {
                if (update) {
                } else {
                    if (hasAttribute(ID)) {
                        final String currentId = getUUID().toString();
                        if (!currentId.equals(entry.getValue())) {
                            throw new CannotChangeIdException("You cannot change the id of entity from %s to %s", currentId, entry.getValue());
                        }
                    } else {
                        //force lower case for IDs
                        setID(new LiquidUUID(entry.getValue()));
                    }
                }
            } else if (entry.getKey().equals(NAME.getKeyName()) && hasAttribute(NAME)) {
                //trigger a URL recalculation
                if (!entry.getValue().equals(getProperty(LSDAttribute.NAME))) {
                    setAttribute(LSDAttribute.NAME, entry.getValue());
                    if (onRenameAction != null) {
                        onRenameAction.run();
                    }
                }
            } else {
                setValue(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    @Nonnull
    public Node getLatestVersionFromFork() {
        log.debug("Getting latest version for {0}.", getAttribute(ID));
        if (hasRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING)) {
            final Relationship rel = getSingleRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING);
            if (rel != null) {
                return rel.getOtherNode(this).getLatestVersionFromFork();
            } else {
                return this;
            }
        } else {
            return this;
        }

    }

    public int popularity() {
        int score = 0;
        //noinspection UnusedDeclaration
        final Iterable<org.neo4j.graphdb.Relationship> relationships = neoNode.getRelationships();
        for (final org.neo4j.graphdb.Relationship relationship : relationships) {
            score++;
        }
        return score;
    }

    @Nonnull
    public Node parentNode() {
        final org.neo4j.graphdb.Relationship parentRelationship = neoNode.getSingleRelationship(FountainRelationships.CHILD, Direction.INCOMING);
        if (parentRelationship == null) {
            throw new OrphanedEntityException("The entity with URI %s has no parent.", getAttribute(URI));
        }
        return new Node(parentRelationship.getOtherNode(neoNode));
    }

    public void setIDIfNotSetOnNode() {
        if (!hasAttribute(ID) || getAttribute(ID).isEmpty()) {
            setProperty(ID, UUIDFactory.randomUUID().toString().toLowerCase());
        }
    }

    @Override
    public void timestamp() {
        super.timestamp();
        if (!hasAttribute(LSDAttribute.PUBLISHED)) {
            setProperty(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        }
    }

    public boolean isDeleted() throws InterruptedException {
        return hasAttribute(DELETED);
    }

    public void copyValuesToEntity(@Nonnull final LSDEntity entity, @Nonnull final LSDAttribute... attributes) {
        for (final LSDAttribute attribute : attributes) {
            if (hasAttribute(attribute)) {
                entity.copyAttribute(this, attribute);
            }
        }
    }

    @Nullable
    public LSDEntity convertNodeToLSD(final LiquidRequestDetailLevel detail, final boolean internal) throws InterruptedException {

        final LSDSimpleEntity entity = createEmpty();
        if (hasAttribute(TYPE)) {
            entity.setAttribute(LSDAttribute.TYPE, getAttribute(TYPE));
        } else {
            throw new DataStoreException("Node had no type");
        }
        if (detail == LiquidRequestDetailLevel.COMPLETE || detail == LiquidRequestDetailLevel.NORMAL || detail == LiquidRequestDetailLevel.FREE_TEXT_SEARCH_INDEX) {
            final Iterable<String> iterable = getPropertyKeys();
            for (final String key : iterable) {
                if (!key.startsWith("_")) {
                    final LSDAttribute attributeItem = LSDAttribute.valueOf(key);
                    if (attributeItem == null) {
                        //so we don't recognize, it well fine! it got there somehow so we'll give it to you ;-)
                        //also child entities will not be understood as attributes.
                        entity.setValue(key, getValue(key));
                    } else if (!attributeItem.isHidden() || internal) {
                        //We don't return non-indexable attributes for a search index level of detail
                        if (detail != LiquidRequestDetailLevel.FREE_TEXT_SEARCH_INDEX || attributeItem.isFreeTexSearchable()) {
                            entity.setAttribute(attributeItem, getValue(key));
                        }
                    }
                }
            }
        } else if (detail == LiquidRequestDetailLevel.TITLE_AND_NAME) {
            if (hasAttribute(TITLE)) {
                entity.copyAttribute(this, LSDAttribute.TITLE);
            }
            if (hasAttribute(NAME)) {
                entity.copyAttribute(this, LSDAttribute.NAME);
            }
            entity.copyAttribute(this, LSDAttribute.ID);
        } else if (detail == LiquidRequestDetailLevel.PERSON_MINIMAL) {
            copyValuesToEntity(entity, LSDAttribute.NAME, LSDAttribute.FULL_NAME, LSDAttribute.IMAGE_URL, LSDAttribute.ID, LSDAttribute.URI);
        } else if (detail == LiquidRequestDetailLevel.BOARD_LIST) {
            copyValuesToEntity(entity, LSDAttribute.NAME, LSDAttribute.TITLE, LSDAttribute.DESCRIPTION, LSDAttribute.COMMENT_COUNT, LSDAttribute.FOLLOWERS_COUNT, LSDAttribute.IMAGE_URL, LSDAttribute.ICON_URL, LSDAttribute.ID, LSDAttribute.URI);
        } else if (detail == LiquidRequestDetailLevel.PERMISSION_DELTA) {
            copyValuesToEntity(entity, LSDAttribute.URI, LSDAttribute.ID, LSDAttribute.MODIFIABLE, LSDAttribute.EDITABLE, LSDAttribute.VIEWABLE);
        } else if (detail == LiquidRequestDetailLevel.MINIMAL) {
            copyValuesToEntity(entity, LSDAttribute.URI, LSDAttribute.ID);
        } else {
            throw new UnsupportedOperationException("The level of detail " + detail + " was not recognized.");
        }
        return entity;

    }

    public boolean isOwner(final Node ownerNode) throws InterruptedException {
        final Iterable<Relationship> relationships = getRelationships(FountainRelationships.OWNER, Direction.OUTGOING);
        for (final Relationship relationship : relationships) {
            if (relationship.getOtherNode(this).equals(ownerNode)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAuthor(final Node ownerNode) throws InterruptedException {
        final Iterable<Relationship> relationships = getRelationships(FountainRelationships.AUTHOR, Direction.OUTGOING);
        for (final Relationship relationship : relationships) {
            if (relationship.getOtherNode(this).equals(ownerNode)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOwner(@Nonnull final LiquidSessionIdentifier identity) throws InterruptedException {
        //Here I am traversing from the node supplied to it's owner alias, to the identity node that the alias relates to.
        //It's cleaner using the traverser as there would be loads of conditional logic here as not all aliases have
        //an associated identity etc.
        final Traverser traverser = traverse(Traverser.Order.DEPTH_FIRST, new StopEvaluator() {
                    @Override
                    public boolean isStopNode(@Nonnull final TraversalPosition currentPos) {
                        final Node currNode = new Node(currentPos.currentNode());
                        return currNode.hasAttribute(URI) && currNode.getAttribute(URI).equals(identity.getUserURL().asString());
                    }
                }, new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                        final Node currNode = new Node(currentPos.currentNode());
                        return currNode.hasAttribute(URI) && currNode.getAttribute(URI).equals(identity.getUserURL().asString());
                    }
                }, FountainRelationships.OWNER, Direction.OUTGOING, FountainRelationships.ALIAS, Direction.OUTGOING
        );
        return traverser.iterator().hasNext();
    }

    public boolean isLatestVersion() {
        return getSingleRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING) == null;
    }

    public void assertLatestVersion() {
        if (!isLatestVersion()) {
            throw new StaleUpdateException("Attempted to reference a stale node %s which has already been updated (i.e. stale version).", getAttribute(URI));
        }
    }

    public void forEachChild(@Nonnull final NodeCallback callback) throws Exception {
        final Iterable<Relationship> relationships = getRelationships(FountainRelationships.CHILD, Direction.OUTGOING);
        for (final Relationship relationship : relationships) {
            final Node poolObjectNode = relationship.getOtherNode(this);
            if (!poolObjectNode.isDeleted()) {
                callback.call(poolObjectNode.getLatestVersionFromFork());
            }
        }

        final Iterable<Relationship> linkedRelationships = getRelationships(FountainRelationships.LINKED_CHILD, Direction.OUTGOING);
        for (final Relationship relationship : linkedRelationships) {
            final Node poolObjectNode = relationship.getOtherNode(this);
            if (!poolObjectNode.isDeleted()) {
                callback.call(poolObjectNode);
            }
        }
    }

    public double calculateRadius() {
        if (hasAttribute(LSDAttribute.VIEW_X) && hasAttribute(LSDAttribute.VIEW_Y)) {
            final double x = Double.valueOf(getAttribute(LSDAttribute.VIEW_X));
            final double y = Double.valueOf(getAttribute(LSDAttribute.VIEW_Y));
            return Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    public boolean isListed() {
        Node boardNode = null;
        final Iterator<org.neo4j.graphdb.Node> parentIterator = traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            @Override
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return new Node(currentPos.currentNode()).canBe(LSDDictionaryTypes.BOARD);
            }
        }, FountainRelationships.CHILD, Direction.INCOMING, FountainRelationships.COMMENT, Direction.INCOMING).iterator();
        if (parentIterator.hasNext()) {
            boardNode = new Node(parentIterator.next());
        }
        return boardNode != null && boardNode.getBooleanAttribute(LSDAttribute.LISTED);
    }

    public void inheritPermissions(@Nonnull final LSDEntity parent) {
        final LiquidPermissionSet liquidPermissionSet = LiquidPermissionSet.createPermissionSet(parent.getAttribute(PERMISSIONS));
        log.debug("Inheriting permission " + liquidPermissionSet);
        setProperty(PERMISSIONS, liquidPermissionSet.restoreDeletePermission().toString());
        log.debug("Child now has " + getAttribute(PERMISSIONS));
    }

    public boolean isAuthorizedInternal(@Nullable final LiquidSessionIdentifier identity, @Nonnull final LiquidPermission... permissions) throws InterruptedException {
        assertLatestVersion();
        if (identity == null) {
            return false;
        }
//            if (identity.getName().equals("neo")) {
//                log.debug("He is the one, so he can do as he wishes.");
//                return true;
//            }
        if ("admin".equals(identity.getName())) {
            log.debug("Admin user privilege used.");
            return true;
        }
        if (identity.getName().equals(FountainNeo.SYSTEM)) {
            log.debug("System user privilege used.");
            return true;
        }
        final String permissionsStr = getAttribute(PERMISSIONS);
        if (log.isDebugEnabled()) {
            log.debug("Authorizing {0} for permission {1} on object {2}; permission set is {3}.", identity.getName(), Arrays.toString(permissions), getAttribute(ID), permissionsStr);
        }
        final LiquidPermissionSet permissionSet = LiquidPermissionSet.createPermissionSet(permissionsStr);
        try {
            final Relationship ownerRelationship = getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
            if (ownerRelationship == null) {
                log.debug("No owner found on {0}/{1} .", getAttribute(ID), getAttribute(URI));
            } else {
                log.debug("Owner node is {0}", ownerRelationship.getEndNode().getAttribute(URI));
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        //is owner
        if (isOwner(identity)) {
            log.debug("Authorizing {0} as owner on {1}.", identity.getName(), getAttribute(ID));
            for (final LiquidPermission permission : permissions) {
                if (!permissionSet.hasPermission(LiquidPermissionScope.OWNER, permission)) {
                    return false;
                }
            }
            return true;
        } else if ("anon".equals(identity.getName())) {
            log.debug("Authorizing {0} as anonymous on {1}.", identity.getName(), getAttribute(URI));
            for (final LiquidPermission permission : permissions) {
                if (!permissionSet.hasPermission(LiquidPermissionScope.UNKNOWN, permission)) {
                    return false;
                }
            }
            return true;
        } else {
            log.debug("Authorizing {0} as world on {1}.", identity.getName(), getAttribute(URI));
            for (final LiquidPermission permission : permissions) {
                if (!permissionSet.hasPermission(LiquidPermissionScope.WORLD, permission)) {
                    return false;
                }
            }
            return true;
        }

    }

    public boolean isAuthorized(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidPermission... permissions) throws InterruptedException {
        if (this == null) {
            return false;
        }
        final StringBuilder cacheKeyBuilder = new StringBuilder().append(getNeoId()).append(':').append(identity.getAliasURL()).append(':');
        for (final LiquidPermission permission : permissions) {
            cacheKeyBuilder.append(permission.ordinal()).append(',');
        }
        final String cacheKey = cacheKeyBuilder.toString();
        if (nodeAuthCache.get(cacheKey) != null) {
            return (Boolean) nodeAuthCache.get(cacheKey).getValue();
        } else {
            final boolean result = isAuthorizedInternal(identity, permissions);

            //remember nodes are regarded by Fountain as immutable
            // any attempt to change their permissions should result in a new node being created.
            //therefore the cache lifetime is eternal
            nodeAuthCache.put(new Element(cacheKey, result, true, null, null));
            return result;
        }

    }

    public void setPermissionFlagsOnEntity(@Nonnull final LiquidSessionIdentifier session, @Nullable final Node parent, @Nonnull final LSDEntity entity) throws InterruptedException {
        if (parent == null) {
            entity.setAttribute(LSDAttribute.MODIFIABLE, isAuthorized(session, LiquidPermission.MODIFY));
            entity.setAttribute(LSDAttribute.EDITABLE, isAuthorized(session, LiquidPermission.EDIT));
            entity.setAttribute(LSDAttribute.DELETABLE, isAuthorized(session, LiquidPermission.DELETE));
        } else {
            entity.setAttribute(LSDAttribute.MODIFIABLE, parent.isAuthorized(session, LiquidPermission.EDIT) || parent.isAuthorized(session, LiquidPermission.MODIFY) && isAuthorized(session, LiquidPermission.MODIFY));
            entity.setAttribute(LSDAttribute.EDITABLE, parent.isAuthorized(session, LiquidPermission.EDIT) || parent.isAuthorized(session, LiquidPermission.MODIFY) && isAuthorized(session, LiquidPermission.EDIT));
            entity.setAttribute(LSDAttribute.DELETABLE, parent.isAuthorized(session, LiquidPermission.EDIT) || parent.isAuthorized(session, LiquidPermission.MODIFY) && isAuthorized(session, LiquidPermission.DELETE));
        }
        entity.setAttribute(LSDAttribute.ADMINISTERABLE, isAuthorized(session, LiquidPermission.SYSTEM));
    }


    private static class RelationshipIterable implements Iterable<Relationship> {
        private final Iterable<org.neo4j.graphdb.Relationship> relationships;

        private RelationshipIterable(final Iterable<org.neo4j.graphdb.Relationship> relationships) {
            super();
            this.relationships = relationships;
        }

        @Nonnull
        @Override
        public Iterator<Relationship> iterator() {
            final Iterator<org.neo4j.graphdb.Relationship> iterator = relationships.iterator();
            return new RelationshipIterator(iterator);
        }

        private static class RelationshipIterator implements Iterator<Relationship> {
            private final Iterator<org.neo4j.graphdb.Relationship> iterator;

            private RelationshipIterator(final Iterator<org.neo4j.graphdb.Relationship> iterator) {
                super();
                this.iterator = iterator;
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Nonnull
            @Override
            public Relationship next() {
                return new Relationship(iterator.next());
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && neoNode.equals(((Node) o).neoNode);

    }

    @Override
    public int hashCode() {
        return neoNode.hashCode();
    }

    @Nonnull
    @Override
    public String toString() {
        return "Node{" +
                "neoNode=" + neoNode +
                '}';
    }
}
