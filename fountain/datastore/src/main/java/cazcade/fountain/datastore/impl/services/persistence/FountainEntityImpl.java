package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.*;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.NodeCallback;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.*;
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
@SuppressWarnings({"UnnecessaryFullyQualifiedName"})
public final class FountainEntityImpl extends LSDSimpleEntity implements LSDPersistedEntity {

    @Nonnull
    private static final Logger log = Logger.getLogger(FountainEntityImpl.class);

    private static final long serialVersionUID = -1697240636351951371L;


    private static final Cache nodeAuthCache;

    static {
        if (!CacheManager.getInstance().cacheExists("nodeauth")) {
            CacheManager.getInstance().addCache("nodeauth");
        }
        nodeAuthCache = CacheManager.getInstance().getCache("nodeauth");

    }

    @SuppressWarnings({"TransientFieldNotInitialized"})
    @Nonnull
    private final transient org.neo4j.graphdb.Node neoNode;

    public FountainEntityImpl(@Nonnull final org.neo4j.graphdb.Node neoNode) {
        super(new NeoPropertyStore(neoNode));
        this.neoNode = neoNode;
    }


    @Override
    public long getPersistenceId() {
        return neoNode.getId();
    }

    /**
     * This should rarely be called as we mostly do not delete nodes from Neo.
     * There are some exceptions (like session nodes). However even those exceptions
     * will be removed in future.
     */
    @Override
    public void hardDelete() {
        neoNode.delete();
    }


    @Override
    @Nonnull
    public Iterable<FountainRelationship> getRelationships() {
        final Iterable<org.neo4j.graphdb.Relationship> relationships = neoNode.getRelationships();
        return new RelationshipIterable(relationships);
    }


    @Override
    @Nonnull
    public Iterable<FountainRelationship> getRelationships(final FountainRelationships... types) {
        return new RelationshipIterable(neoNode.getRelationships(types));
    }


    @Override
    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nonnull
    public Iterable<FountainRelationship> getRelationships(final FountainRelationships type, final Direction dir) {
        return new RelationshipIterable(neoNode.getRelationships(type, dir));
    }


    @Override
    @SuppressWarnings({"TypeMayBeWeakened"})
    public boolean hasRelationship(final FountainRelationships type, final Direction dir) {
        return neoNode.hasRelationship(type, dir);
    }


    @Override
    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nullable
    public FountainRelationship getSingleRelationship(final FountainRelationships type, final Direction dir) {
        final org.neo4j.graphdb.Relationship relationship = neoNode.getSingleRelationship(type, dir);
        if (relationship == null) {
            return null;
        }
        return new FountainRelationshipImpl(relationship);
    }


    @Override
    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nonnull
    public FountainRelationship createRelationshipTo(@Nonnull final LSDPersistedEntity otherPersistedEntityImpl, final FountainRelationships type) {
        return new FountainRelationshipImpl(neoNode.createRelationshipTo(otherPersistedEntityImpl.getNeoNode(), type));
    }


    @Override
    public Traverser traverse(final Traverser.Order traversalOrder, final StopEvaluator stopEvaluator, final ReturnableEvaluator returnableEvaluator, final FountainRelationships relationshipType, final Direction direction) {
        return neoNode.traverse(traversalOrder, stopEvaluator, returnableEvaluator, relationshipType, direction);
    }


    @Override
    public Traverser traverse(final Traverser.Order traversalOrder, final StopEvaluator stopEvaluator, final ReturnableEvaluator returnableEvaluator, final RelationshipType firstRelationshipType, final Direction firstDirection, final RelationshipType secondRelationshipType, final Direction secondDirection) {
        return neoNode.traverse(traversalOrder, stopEvaluator, returnableEvaluator, firstRelationshipType, firstDirection, secondRelationshipType, secondDirection);
    }


    @Override
    public Traverser traverse(final Traverser.Order traversalOrder, final StopEvaluator stopEvaluator, final ReturnableEvaluator returnableEvaluator, final Object... relationshipTypesAndDirections) {
        return neoNode.traverse(traversalOrder, stopEvaluator, returnableEvaluator, relationshipTypesAndDirections);
    }


//    public void removeProperty(@Nonnull final LSDAttribute key) {
//        remove(key);
//    }


    @Override
    public Iterable<String> getPropertyKeys() {
        return neoNode.getPropertyKeys();
    }


    @Override
    @Nonnull
    public org.neo4j.graphdb.Node getNeoNode() {
        return neoNode;
    }

    @SuppressWarnings({"ReturnOfThis"})
    @Override
    @Nonnull
    public LSDPersistedEntity mergeProperties(@Nonnull final LSDTransferEntity entity, final boolean update, final boolean ignoreType, @Nullable final Runnable onRenameAction) throws InterruptedException {
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
                if (!entry.getValue().equals(getAttribute(LSDAttribute.NAME))) {
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

    @SuppressWarnings({"ReturnOfThis"})
    @Override
    @Nonnull
    public LSDPersistedEntity getLatestVersionFromFork() {
        log.debug("Getting latest version for {0}.", getAttribute(ID));
        if (hasRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING)) {
            final FountainRelationship rel = getSingleRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING);
            if (rel != null) {
                return rel.getOtherNode(this).getLatestVersionFromFork();
            } else {
                return this;
            }
        } else {
            return this;
        }

    }

    @Override
    public int popularity() {
        int score = 0;
        final Iterable<org.neo4j.graphdb.Relationship> relationships = neoNode.getRelationships();
        //noinspection UnusedDeclaration
        for (final org.neo4j.graphdb.Relationship relationship : relationships) {
            score++;
        }
        return score;
    }

    @Override
    @Nonnull
    public LSDPersistedEntity parentNode() {
        final org.neo4j.graphdb.Relationship parentRelationship = neoNode.getSingleRelationship(FountainRelationships.CHILD, Direction.INCOMING);
        if (parentRelationship == null) {
            throw new OrphanedEntityException("The entity with URI %s has no parent.", getAttribute(URI));
        }
        return new FountainEntityImpl(parentRelationship.getOtherNode(neoNode));
    }

    @Override
    public void setIDIfNotSetOnNode() {
        if (!hasAttribute(ID) || getAttribute(ID).isEmpty()) {
            setAttribute(ID, UUIDFactory.randomUUID().toString().toLowerCase());
        }
    }

    @Override
    public void timestamp() {
        super.timestamp();
        if (!hasAttribute(LSDAttribute.PUBLISHED)) {
            setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        }
    }

    @Override
    public boolean isDeleted() throws InterruptedException {
        return hasAttribute(DELETED);
    }

    @Override
    public void copyValuesToEntity(@Nonnull final LSDBaseEntity entity, @Nonnull final LSDAttribute... attributes) {
        for (final LSDAttribute attribute : attributes) {
            if (hasAttribute(attribute)) {
                entity.copyAttribute(this, attribute);
            }
        }
    }

    @Override
    @Nullable
    public LSDTransferEntity convertNodeToLSD(final LiquidRequestDetailLevel detail, final boolean internal) throws InterruptedException {

        final LSDTransferEntity entity = createEmpty();
        if (hasAttribute(TYPE)) {
            entity.setAttribute(LSDAttribute.TYPE, getAttribute(TYPE));
        } else {
            throw new DataStoreException("FountainEntityImpl had no type");
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

    @Override
    public boolean isOwner(final LSDPersistedEntity ownerPersistedEntity) throws InterruptedException {
        final Iterable<FountainRelationship> relationships = getRelationships(FountainRelationships.OWNER, Direction.OUTGOING);
        for (final FountainRelationship relationship : relationships) {
            if (relationship.getOtherNode(this).equals(ownerPersistedEntity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAuthor(final LSDPersistedEntity ownerPersistedEntity) throws InterruptedException {
        final Iterable<FountainRelationship> relationships = getRelationships(FountainRelationships.AUTHOR, Direction.OUTGOING);
        for (final FountainRelationship relationship : relationships) {
            if (relationship.getOtherNode(this).equals(ownerPersistedEntity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings({"TypeMayBeWeakened"})
    public boolean isOwner(@Nonnull final LiquidSessionIdentifier identity) throws InterruptedException {
        //Here I am traversing from the node supplied to it's owner alias, to the identity node that the alias relates to.
        //It's cleaner using the traverser as there would be loads of conditional logic here as not all aliases have
        //an associated identity etc.
        final Traverser traverser = traverse(Traverser.Order.DEPTH_FIRST, new StopEvaluator() {
                    @Override
                    public boolean isStopNode(@Nonnull final TraversalPosition currentPos) {
                        final LSDPersistedEntity entity = new FountainEntityImpl(currentPos.currentNode());
                        return entity.hasAttribute(URI) && entity.getAttribute(URI).equals(identity.getUserURL().asString());
                    }
                }, new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                        final LSDPersistedEntity entity = new FountainEntityImpl(currentPos.currentNode());
                        return entity.hasAttribute(URI) && entity.getAttribute(URI).equals(identity.getUserURL().asString());
                    }
                }, FountainRelationships.OWNER, Direction.OUTGOING, FountainRelationships.ALIAS, Direction.OUTGOING
        );
        return traverser.iterator().hasNext();
    }

    @Override
    public boolean isLatestVersion() {
        return getSingleRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING) == null;
    }

    @Override
    public void assertLatestVersion() {
        if (!isLatestVersion()) {
            throw new StaleUpdateException("Attempted to reference a stale node %s which has already been updated (i.e. stale version).", getAttribute(URI));
        }
    }

    @Override
    public void forEachChild(@Nonnull final NodeCallback callback) throws Exception {
        final Iterable<FountainRelationship> relationships = getRelationships(FountainRelationships.CHILD, Direction.OUTGOING);
        for (final FountainRelationship relationship : relationships) {
            final LSDPersistedEntity poolObjectPersistedEntity = relationship.getOtherNode(this);
            if (!poolObjectPersistedEntity.isDeleted()) {
                callback.call(poolObjectPersistedEntity.getLatestVersionFromFork());
            }
        }

        final Iterable<FountainRelationship> linkedRelationships = getRelationships(FountainRelationships.LINKED_CHILD, Direction.OUTGOING);
        for (final FountainRelationship relationship : linkedRelationships) {
            final LSDPersistedEntity poolObjectPersistedEntity = relationship.getOtherNode(this);
            if (!poolObjectPersistedEntity.isDeleted()) {
                callback.call(poolObjectPersistedEntity);
            }
        }
    }

    @Override
    public double calculateRadius() {
        if (hasAttribute(LSDAttribute.VIEW_X) && hasAttribute(LSDAttribute.VIEW_Y)) {
            final double x = Double.valueOf(getAttribute(LSDAttribute.VIEW_X));
            final double y = Double.valueOf(getAttribute(LSDAttribute.VIEW_Y));
            return Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    @Override
    public boolean isListed() {
        LSDPersistedEntity boardPersistedEntity = null;
        final Iterator<org.neo4j.graphdb.Node> parentIterator = traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            @Override
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return new FountainEntityImpl(currentPos.currentNode()).canBe(LSDDictionaryTypes.BOARD);
            }
        }, FountainRelationships.CHILD, Direction.INCOMING, FountainRelationships.COMMENT, Direction.INCOMING).iterator();
        if (parentIterator.hasNext()) {
            boardPersistedEntity = new FountainEntityImpl(parentIterator.next());
        }
        return boardPersistedEntity != null && boardPersistedEntity.getBooleanAttribute(LSDAttribute.LISTED);
    }

    @Override
    public void inheritPermissions(@Nonnull final LSDBaseEntity parent) {
        //        log.debug("Inheriting permission " + liquidPermissionSet);
        setAttribute(PERMISSIONS, LiquidPermissionSet.createPermissionSet(parent.getAttribute(PERMISSIONS)).restoreDeletePermission().toString());
//        log.debug("Child now has " + getAttribute(PERMISSIONS));
    }

    private boolean isAuthorizedInternal(@Nullable final LiquidSessionIdentifier identity, @Nonnull final LiquidPermission... permissions) throws InterruptedException {
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
        if (identity.getName().equals(FountainNeoImpl.SYSTEM)) {
            log.debug("System user privilege used.");
            return true;
        }
        final String permissionsStr = getAttribute(PERMISSIONS);
        if (log.isDebugEnabled()) {
            log.debug("Authorizing {0} for permission {1} on object {2}; permission set is {3}.", identity.getName(), Arrays.toString(permissions), getAttribute(ID), permissionsStr);
        }
        final LiquidPermissionSet permissionSet = LiquidPermissionSet.createPermissionSet(permissionsStr);
        try {
            final FountainRelationship ownerRelationship = getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
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

    @Override
    public boolean isAuthorized(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidPermission... permissions) throws InterruptedException {
        final StringBuilder cacheKeyBuilder = new StringBuilder().append(getPersistenceId()).append(':').append(identity.getAliasURL()).append(':');
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

    @Override
    public void setPermissionFlagsOnEntity(@Nonnull final LiquidSessionIdentifier session, @Nullable final LSDPersistedEntity parent, @Nonnull final LSDBaseEntity entity) throws InterruptedException {
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


    private static final class RelationshipIterable implements Iterable<FountainRelationship> {
        private final Iterable<org.neo4j.graphdb.Relationship> relationships;

        private RelationshipIterable(final Iterable<org.neo4j.graphdb.Relationship> relationships) {
            super();
            this.relationships = relationships;
        }

        @Nonnull
        @Override
        public Iterator<FountainRelationship> iterator() {
            final Iterator<org.neo4j.graphdb.Relationship> iterator = relationships.iterator();
            return new RelationshipIterator(iterator);
        }

        private static final class RelationshipIterator implements Iterator<FountainRelationship> {
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
            public FountainRelationship next() {
                return new FountainRelationshipImpl(iterator.next());
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        }
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && neoNode.equals(((FountainEntityImpl) o).neoNode);

    }

    @Override
    public int hashCode() {
        return neoNode.hashCode();
    }

    @Nonnull
    @Override
    public String toString() {
        return "FountainEntityImpl{" +
                "neoNode=" + neoNode +
                '}';
    }
}
