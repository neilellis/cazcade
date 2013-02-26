/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.*;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.NodeCallback;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.impl.UUIDFactory;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.neo4j.graphdb.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author neilellis@cazcade.com
 */
@SuppressWarnings({"UnnecessaryFullyQualifiedName"})
public final class FountainEntity extends SimpleEntity<PersistedEntity> implements PersistedEntity {

    @Nonnull
    private static final Logger log = Logger.getLogger(FountainEntity.class);

    private static final long serialVersionUID = -1697240636351951371L;


    private static final Cache nodeAuthCache;
    public static final String LOCKED_PROPERTY = "___locked___";

    @SuppressWarnings({"TransientFieldNotInitialized"}) @Nonnull
    private final transient org.neo4j.graphdb.Node neoNode;

    static {
        if (!CacheManager.getInstance().cacheExists("nodeauth")) {
            CacheManager.getInstance().addCache("nodeauth");
        }
        nodeAuthCache = CacheManager.getInstance().getCache("nodeauth");
    }

    @Nullable private Lock lock;

    public FountainEntity(@Nonnull final org.neo4j.graphdb.Node neoNode) {
        lsdProperties = new NeoPropertyStore(neoNode).copy();
        this.neoNode = neoNode;
    }

    @Override
    public double calculateRadius() {
        if (has$(Dictionary.VIEW_X) && has$(Dictionary.VIEW_Y)) {
            final double x = Double.valueOf($(Dictionary.VIEW_X));
            final double y = Double.valueOf($(Dictionary.VIEW_Y));
            return Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }

    @Override @Nonnull
    public TransferEntity toTransfer(@Nonnull final RequestDetailLevel detail, final boolean internal) throws InterruptedException {
        final TransferEntity entity = createEmpty();
        if (has$(Dictionary.TYPE)) {
            entity.$(Dictionary.TYPE, $(Dictionary.TYPE));
        } else {
            throw new DataStoreException("FountainEntity had no type");
        }
        if (detail == RequestDetailLevel.COMPLETE ||
            detail == RequestDetailLevel.NORMAL ||
            detail == RequestDetailLevel.FREE_TEXT_SEARCH_INDEX) {
            final Iterable<String> iterable = keys();
            for (final String key : iterable) {
                if (!key.startsWith("_")) {
                    final Attribute attributeItem = Attribute.valueOf(key);
                    final String value = getValue(key);
                    assert value != null;
                    if (attributeItem == null) {
                        //so we don't recognize, it well fine! it got there somehow so we'll give it to you ;-)
                        //also child entities will not be understood as attributes.
                        entity.setValue(key, value);
                    } else if (!attributeItem.isHidden() || internal) {
                        //We don't return non-indexable attributes for a search index level of detail
                        if (detail != RequestDetailLevel.FREE_TEXT_SEARCH_INDEX || attributeItem.isFreeTexSearchable()) {
                            entity.$(attributeItem, value);
                        }
                    }
                }
            }
        } else if (detail == RequestDetailLevel.TITLE_AND_NAME) {
            if (has$(Dictionary.TITLE)) {
                entity.$(this, Dictionary.TITLE);
            }
            if (has$(Dictionary.NAME)) {
                entity.$(this, Dictionary.NAME);
            }
            entity.$(this, Dictionary.ID);
        } else if (detail == RequestDetailLevel.PERSON_MINIMAL) {
            copyValuesToEntity(entity, Dictionary.NAME, Dictionary.FULL_NAME, Dictionary.IMAGE_URL, Dictionary.ID, Dictionary.URI);
        } else if (detail == RequestDetailLevel.BOARD_LIST) {
            copyValuesToEntity(entity, Dictionary.NAME, Dictionary.TITLE, Dictionary.DESCRIPTION, Dictionary.COMMENT_COUNT, Dictionary.FOLLOWERS_COUNT, Dictionary.IMAGE_URL, Dictionary.ICON_URL, Dictionary.ICON_HEIGHT, Dictionary.ICON_WIDTH, Dictionary.ID, Dictionary.URI, Dictionary.MODIFIED);
        } else if (detail == RequestDetailLevel.PERMISSION_DELTA) {
            copyValuesToEntity(entity, Dictionary.URI, Dictionary.ID, Dictionary.MODIFIABLE, Dictionary.EDITABLE, Dictionary.VIEWABLE);
        } else if (detail == RequestDetailLevel.MINIMAL) {
            copyValuesToEntity(entity, Dictionary.URI, Dictionary.ID);
        } else {
            throw new UnsupportedOperationException("The level of detail " + detail + " was not recognized.");
        }
        return entity;
    }

    //    public void removeProperty(@Nonnull final Attribute key) {
    //        remove(key);
    //    }


    @Override
    public Iterable<String> keys() {
        return neoNode.getPropertyKeys();
    }

    @Override
    public void copyValuesToEntity(@Nonnull final Entity entity, @Nonnull final Attribute... attributes) {
        for (final Attribute attribute : attributes) {
            if (has$(attribute)) {
                entity.$(this, attribute);
            }
        }
    }

    @Override @SuppressWarnings({"TypeMayBeWeakened"}) @Nonnull
    public FountainRelationship relate(@Nonnull final PersistedEntity otherPersistedEntityImpl, final FountainRelationships type) {
        return new FountainRelationshipImpl(neoNode.createRelationshipTo(otherPersistedEntityImpl.getNeoNode(), type));
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && neoNode.equals(((FountainEntity) o).neoNode);
    }

    @Override
    public void forEachChild(@Nonnull final NodeCallback callback) throws Exception {
        final Iterable<FountainRelationship> relationships = relationships(FountainRelationships.CHILD, Direction.OUTGOING);
        for (final FountainRelationship relationship : relationships) {
            final PersistedEntity poolObjectPersistedEntity = relationship.other(this);
            if (!poolObjectPersistedEntity.deleted()) {
                callback.call(poolObjectPersistedEntity.getLatestVersionFromFork());
            }
        }

        final Iterable<FountainRelationship> linkedRelationships = relationships(FountainRelationships.LINKED_CHILD, Direction.OUTGOING);
        for (final FountainRelationship relationship : linkedRelationships) {
            final PersistedEntity poolObjectPersistedEntity = relationship.other(this);
            if (!poolObjectPersistedEntity.deleted()) {
                callback.call(poolObjectPersistedEntity);
            }
        }
    }

    @Override @SuppressWarnings({"TypeMayBeWeakened"}) @Nonnull
    public FountainRelationshipCollection relationships(final FountainRelationships type, final Direction dir) {
        return new FountainRelationshipCollection(new RelationshipIterable(neoNode.getRelationships(type, dir)));
    }

    @Nonnull public Set<FountainRelationship> getRelationshipsAsSet(final FountainRelationships type, final Direction dir) {
        final HashSet<FountainRelationship> result = new HashSet<FountainRelationship>();
        final Iterable<Relationship> relationships = neoNode.getRelationships(type, dir);
        for (final Relationship relationship : relationships) {
            result.add(new FountainRelationshipImpl(relationship));
        }
        return result;
    }

    @SuppressWarnings({"ReturnOfThis"}) @Override @Nonnull
    public PersistedEntity getLatestVersionFromFork() {
        log.debug("Getting latest version for {0}.", $(Dictionary.ID));
        if (has(FountainRelationships.VERSION_PARENT, Direction.INCOMING)) {
            final FountainRelationship rel = relationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING);
            if (rel != null) {
                return rel.other(this).getLatestVersionFromFork();
            } else {
                return this;
            }
        } else {
            return this;
        }
    }

    @Override @SuppressWarnings({"TypeMayBeWeakened"})
    public boolean has(final FountainRelationships type, final Direction dir) {
        return neoNode.hasRelationship(type, dir);
    }

    @Override @SuppressWarnings({"TypeMayBeWeakened"}) @Nullable
    public FountainRelationship relationship(final FountainRelationships type, final Direction dir) {
        final org.neo4j.graphdb.Relationship relationship = neoNode.getSingleRelationship(type, dir);
        if (relationship == null) {
            return null;
        }
        return new FountainRelationshipImpl(relationship);
    }

    @Override
    public long getPersistenceId() {
        return neoNode.getId();
    }

    @Override @Nonnull
    public FountainRelationshipCollection relationships(final FountainRelationships... types) {
        return new FountainRelationshipCollection(new RelationshipIterable(neoNode.getRelationships(types)));
    }

    @Override @Nonnull
    public FountainRelationshipCollection relationships() {
        final Iterable<org.neo4j.graphdb.Relationship> relationships = neoNode.getRelationships();
        return new FountainRelationshipCollection(new RelationshipIterable(relationships));
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
    public int hashCode() {
        return neoNode.hashCode();
    }

    @Override
    public PersistedEntity inheritPermissions(@Nonnull final Entity parent) {
        //        log.debug("Inheriting permission " + liquidPermissionSet);
        $(Dictionary.PERMISSIONS, PermissionSet.createPermissionSet(parent.$(Dictionary.PERMISSIONS))
                                               .restoreDeletePermission()
                                               .toString());
        //        log.debug("Child now has " + $(PERMISSIONS));
        return this;
    }

    @Override
    public boolean isAuthor(@Nonnull final PersistedEntity ownerPersistedEntity) throws InterruptedException {
        final Iterable<FountainRelationship> relationships = relationships(FountainRelationships.AUTHOR, Direction.OUTGOING);
        for (final FountainRelationship relationship : relationships) {
            if (relationship.other(this).equals(ownerPersistedEntity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleted() throws InterruptedException {
        return has$(Dictionary.DELETED);
    }

    @Override
    public boolean isListed() {
        FountainEntity boardPersistedEntity = null;
        final Iterator<org.neo4j.graphdb.Node> parentIterator = traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            @Override
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return new FountainEntity(currentPos.currentNode()).canBe(Types.T_BOARD);
            }
        }, FountainRelationships.CHILD, Direction.INCOMING, FountainRelationships.COMMENT, Direction.INCOMING).iterator();
        if (parentIterator.hasNext()) {
            boardPersistedEntity = new FountainEntity(parentIterator.next());
        }
        return boardPersistedEntity != null && boardPersistedEntity.$bool(Dictionary.LISTED);
    }

    @Override
    public boolean isOwner(@Nonnull final PersistedEntity ownerPersistedEntity) throws InterruptedException {
        final Iterable<FountainRelationship> relationships = relationships(FountainRelationships.OWNER, Direction.OUTGOING);
        for (final FountainRelationship relationship : relationships) {
            if (relationship.other(this).equals(ownerPersistedEntity)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"ReturnOfThis"}) @Override @Nonnull
    public PersistedEntity mergeProperties(@Nonnull final TransferEntity source, final boolean update, final boolean ignoreType, @Nullable final Runnable onRenameAction) throws InterruptedException {
        if (has$(Dictionary.TYPE) && source.has$(Dictionary.TYPE) && !ignoreType) {
            if (!is(source.type())) {
                throw new CannotChangeTypeException("The entity type used to be %s the request was to change it to %s", type(), source
                        .type());
            }
        }
        final Map<String, String> map = source.asMapForPersistence(ignoreType, update);
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals(Dictionary.ID.getKeyName())) {
                if (update) {
                } else {
                    if (has$(Dictionary.ID)) {
                        final String currentId = id().toString();
                        if (!currentId.equals(entry.getValue())) {
                            throw new CannotChangeIdException("You cannot change the id of entity from %s to %s", currentId, entry.getValue());
                        }
                    } else {
                        //force lower case for IDs
                        id(new LiquidUUID(entry.getValue()));
                    }
                }
            } else if (entry.getKey().equals(Dictionary.NAME.getKeyName()) && has$(Dictionary.NAME)) {
                //trigger a URL recalculation
                if (!entry.getValue().equals($(Dictionary.NAME))) {
                    $(Dictionary.NAME, entry.getValue());
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

    @Override @Nonnull
    public PersistedEntity parent() {
        final org.neo4j.graphdb.Relationship parentRelationship = neoNode.getSingleRelationship(FountainRelationships.CHILD, Direction.INCOMING);
        if (parentRelationship == null) {
            throw new OrphanedEntityException("The entity with URI %s has no parent.", $(Dictionary.URI));
        }
        return new FountainEntity(parentRelationship.getOtherNode(neoNode));
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
    public FountainEntity setIDIfNotSetOnNode() {
        if (!has$(Dictionary.ID) || $(Dictionary.ID).isEmpty()) {
            $(Dictionary.ID, UUIDFactory.randomUUID().toString().toLowerCase());
        }
        return this;
    }

    @Override
    public void setPermissionFlagsOnEntity(@Nonnull final SessionIdentifier session, @Nullable final PersistedEntity parent, @Nonnull final Entity entity) throws InterruptedException {
        if (parent == null) {
            entity.$(Dictionary.MODIFIABLE, isAuthorized(session, Permission.MODIFY_PERM));
            entity.$(Dictionary.EDITABLE, isAuthorized(session, Permission.EDIT_PERM));
            entity.$(Dictionary.DELETABLE, isAuthorized(session, Permission.DELETE_PERM));
        } else {
            entity.$(Dictionary.MODIFIABLE, parent.isAuthorized(session, Permission.EDIT_PERM)
                                            || parent.isAuthorized(session, Permission.MODIFY_PERM)
                                               && isAuthorized(session, Permission.MODIFY_PERM));
            entity.$(Dictionary.EDITABLE, parent.isAuthorized(session, Permission.EDIT_PERM)
                                          || parent.isAuthorized(session, Permission.MODIFY_PERM)
                                             && isAuthorized(session, Permission.EDIT_PERM));
            entity.$(Dictionary.DELETABLE, parent.isAuthorized(session, Permission.EDIT_PERM)
                                           || parent.isAuthorized(session, Permission.MODIFY_PERM)
                                              && isAuthorized(session, Permission.DELETE_PERM));
        }
        entity.$(Dictionary.ADMINISTERABLE, isAuthorized(session, Permission.SYSTEM_PERM));
    }

    @Override
    public boolean isAuthorized(@Nonnull final SessionIdentifier identity, @Nonnull final Permission... permissions) throws InterruptedException {
        final StringBuilder cacheKeyBuilder = new StringBuilder().append(getPersistenceId())
                                                                 .append(':')
                                                                 .append(identity.aliasURI())
                                                                 .append(':');
        for (final Permission permission : permissions) {
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

    private boolean isAuthorizedInternal(@Nullable final SessionIdentifier identity, @Nonnull final Permission... permissions) throws InterruptedException {
        assertLatestVersion();
        if (identity == null) {
            return false;
        }
        //            if (identity.name().equals("neo")) {
        //                log.debug("He is the one, so he can do as he wishes.");
        //                return true;
        //            }
        if ("admin".equals(identity.name())) {
            log.debug("Admin user privilege used.");
            return true;
        }
        if (identity.name().equals(FountainNeoImpl.SYSTEM)) {
            log.debug("System user privilege used.");
            return true;
        }
        final String permissionsStr = $(Dictionary.PERMISSIONS);
        if (log.isDebugEnabled()) {
            log.debug("Authorizing {0} for permission {1} on object {2}; permission set is {3}.", identity.name(), Arrays.toString(permissions), $(Dictionary.ID), permissionsStr);
        }
        final PermissionSet permissionSet = PermissionSet.createPermissionSet(permissionsStr);
        try {
            final FountainRelationship ownerRelationship = relationship(FountainRelationships.OWNER, Direction.OUTGOING);
            if (ownerRelationship == null) {
                log.debug("No owner found on {0}/{1} .", $(Dictionary.ID), $(Dictionary.URI));
            } else {
                log.debug("Owner node is {0}", ownerRelationship.end().$(Dictionary.URI));
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        //is owner
        if (isOwner(identity)) {
            log.debug("Authorizing {0} as owner on {1}.", identity.name(), $(Dictionary.ID));
            for (final Permission permission : permissions) {
                if (!permissionSet.hasPermission(PermissionScope.OWNER_SCOPE, permission)) {
                    return false;
                }
            }
            return true;
        } else if ("anon".equals(identity.name())) {
            log.debug("Authorizing {0} as anonymous on {1}.", identity.name(), $(Dictionary.URI));
            for (final Permission permission : permissions) {
                if (!permissionSet.hasPermission(PermissionScope.UNKNOWN_SCOPE, permission)) {
                    return false;
                }
            }
            return true;
        } else {
            log.debug("Authorizing {0} as world on {1}.", identity.name(), $(Dictionary.URI));
            for (final Permission permission : permissions) {
                if (!permissionSet.hasPermission(PermissionScope.WORLD_SCOPE, permission)) {
                    return false;
                }
            }
            return true;
        }
    }

    @Override
    public void assertLatestVersion() {
        if (!isLatestVersion()) {
            throw new StaleUpdateException("Attempted to reference a stale node %s which has already been updated (i.e. stale version).", $(Dictionary.URI));
        }
    }

    @Override
    public boolean isLatestVersion() {
        return relationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING) == null;
    }

    @Override @SuppressWarnings({"TypeMayBeWeakened"})
    public boolean isOwner(@Nonnull final SessionIdentifier identity) throws InterruptedException {
        //Here I am traversing from the node supplied to it's owner alias, to the identity node that the alias relates to.
        //It's cleaner using the traverser as there would be loads of conditional logic here as not all aliases have
        //an associated identity etc.
        final Traverser traverser = traverse(Traverser.Order.DEPTH_FIRST, new StopEvaluator() {
                    @Override
                    public boolean isStopNode(@Nonnull final TraversalPosition currentPos) {
                        final PersistedEntity entity = new FountainEntity(currentPos.currentNode());
                        return entity.has$(Dictionary.URI) && entity.$(Dictionary.URI).equals(identity.userURL().asString());
                    }
                }, new ReturnableEvaluator() {
                    @Override
                    public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                        final PersistedEntity entity = new FountainEntity(currentPos.currentNode());
                        return entity.has$(Dictionary.URI) && entity.$(Dictionary.URI).equals(identity.userURL().asString());
                    }
                }, FountainRelationships.OWNER, Direction.OUTGOING, FountainRelationships.ALIAS, Direction.OUTGOING
                                            );
        return traverser.iterator().hasNext();
    }

    @Nonnull @Override
    public Traverser traverse(final Traverser.Order traversalOrder, final StopEvaluator stopEvaluator, final ReturnableEvaluator returnableEvaluator, final RelationshipType firstRelationshipType, final Direction firstDirection, final RelationshipType secondRelationshipType, final Direction secondDirection) {
        return neoNode.traverse(traversalOrder, stopEvaluator, returnableEvaluator, firstRelationshipType, firstDirection, secondRelationshipType, secondDirection);
    }


    @Override
    public PersistedEntity publishTimestamp() {
        if (!has$(Dictionary.PUBLISHED)) {
            $(Dictionary.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        }
        return this;
    }

    @Nonnull @Override
    public String toString() {
        return "FountainEntity{" +
               "neoNode=" + neoNode +
               '}';
    }

    @Nonnull @Override
    public Traverser traverse(final Traverser.Order traversalOrder, final StopEvaluator stopEvaluator, final ReturnableEvaluator returnableEvaluator, final FountainRelationships relationshipType, final Direction direction) {
        return neoNode.traverse(traversalOrder, stopEvaluator, returnableEvaluator, relationshipType, direction);
    }

    @Nonnull @Override
    public Traverser traverse(final Traverser.Order traversalOrder, final StopEvaluator stopEvaluator, final ReturnableEvaluator returnableEvaluator, final Object... relationshipTypesAndDirections) {
        return neoNode.traverse(traversalOrder, stopEvaluator, returnableEvaluator, relationshipTypesAndDirections);
    }

    @Override public void writeLock() {
        lock = FountainNeoImpl.getTransactionInternal().acquireWriteLock(neoNode);
    }

    @Override public void unLock() {
        if (lock != null) {
            lock.release();
        }
        lock = null;
    }

    @Override
    public void modifiedTimestamp() {
        $(Dictionary.MODIFIED, String.valueOf(System.currentTimeMillis()));
    }

    @Override @Nonnull
    public org.neo4j.graphdb.Node getNeoNode() {
        return neoNode;
    }

    private static final class RelationshipIterable implements Iterable<FountainRelationship> {
        private final Iterable<org.neo4j.graphdb.Relationship> relationships;

        private RelationshipIterable(final Iterable<org.neo4j.graphdb.Relationship> relationships) {
            super();
            this.relationships = relationships;
        }

        @Nonnull @Override
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

            @Nonnull @Override
            public FountainRelationship next() {
                return new FountainRelationshipImpl(iterator.next());
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        }
    }
}
