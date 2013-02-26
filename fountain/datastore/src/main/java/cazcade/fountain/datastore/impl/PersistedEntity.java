/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import cazcade.fountain.datastore.impl.services.persistence.FountainRelationshipCollection;
import cazcade.liquid.api.Permission;
import cazcade.liquid.api.RequestDetailLevel;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.Attribute;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.TransferEntity;
import org.neo4j.graphdb.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * This is the core persistent object used by Fountain. Like all Entity objects it's type is derived from
 * a type attribute and all apart from the compulsory (uuid, uri, updated) properties are optional. Their
 * presence can usually be inferred by type. However we're deliberately working with sketchy data, because data
 * always is, so check for an attributes presence and have a back up plan if it's not there.
 *
 * @author neilellis@cazcade.com
 */
public interface PersistedEntity extends TransferEntity<PersistedEntity> {

    void assertLatestVersion();

    /**
     * Okay so this should really go somewhere else., in fact I'm not sure if it's even relevant anymore.
     *
     * @return the radius.
     */
    double calculateRadius();

    @Nonnull TransferEntity toTransfer(@Nonnull RequestDetailLevel detail, boolean internal) throws InterruptedException;

    void copyValuesToEntity(@Nonnull Entity entity, @Nonnull Attribute... attributes);

    @SuppressWarnings({"TypeMayBeWeakened"}) @Nonnull
    FountainRelationship relate(@Nonnull PersistedEntity otherEntity, FountainRelationships type);

    void forEachChild(@Nonnull NodeCallback callback) throws Exception;

    @Nonnull PersistedEntity getLatestVersionFromFork();

    @Nonnull @Deprecated org.neo4j.graphdb.Node getNeoNode();


    /**
     * This is a low impact identifier for the node, it should be treated with caution. If the entity
     * is in Neo4J then consider the warnings here {@link org.neo4j.graphdb.Node#getId()}. Basically
     * it's good for cache keys and that's about it.
     *
     * @return a numerical persistent identifier.
     */
    long getPersistenceId();

    Iterable<String> keys();

    @SuppressWarnings({"TypeMayBeWeakened"}) @Nonnull
    FountainRelationshipCollection relationships(FountainRelationships type, Direction dir);

    @Nonnull Set<FountainRelationship> getRelationshipsAsSet(FountainRelationships type, Direction dir);

    @Nonnull FountainRelationshipCollection relationships(FountainRelationships... types);

    @Nonnull FountainRelationshipCollection relationships();

    @SuppressWarnings({"TypeMayBeWeakened"}) @Nullable FountainRelationship relationship(FountainRelationships type, Direction dir);

    /**
     * Use with great caution, deletes the underlying persistent store value, should only
     * be used on extremely transient data like sessions.
     */
    void hardDelete();

    @SuppressWarnings({"TypeMayBeWeakened"}) boolean has(FountainRelationships type, Direction dir);

    /**
     * Inherit permissions from a parent entity.
     *
     * @param parent a parent entity.
     */
    PersistedEntity inheritPermissions(@Nonnull Entity parent);

    boolean isAuthor(@Nonnull PersistedEntity ownerPersistedEntity) throws InterruptedException;

    boolean isAuthorized(@Nonnull SessionIdentifier identity, @Nonnull Permission... permissions) throws InterruptedException;

    boolean deleted() throws InterruptedException;

    boolean isLatestVersion();

    /**
     * Does this entity belong to a containing entity that is listed somewhere.
     *
     * @return true if it is.
     */
    boolean isListed();

    boolean isOwner(@Nonnull PersistedEntity ownerPersistedEntity) throws InterruptedException;

    boolean isOwner(@Nonnull SessionIdentifier identity) throws InterruptedException;

    @Nonnull
    PersistedEntity mergeProperties(@Nonnull TransferEntity source, boolean update, boolean ignoreType, @Nullable Runnable onRenameAction) throws InterruptedException;

    void modifiedTimestamp();

    @Nonnull PersistedEntity parent();

    int popularity();

    PersistedEntity publishTimestamp();

    PersistedEntity setIDIfNotSetOnNode();

    void setPermissionFlagsOnEntity(@Nonnull SessionIdentifier session, @Nullable PersistedEntity parent, @Nonnull Entity entity) throws InterruptedException;


    @Nonnull
    Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator, RelationshipType firstRelationshipType, Direction firstDirection, RelationshipType secondRelationshipType, Direction secondDirection);

    @Nonnull
    Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator, FountainRelationships relationshipType, Direction direction);

    @Nonnull
    Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator, Object... relationshipTypesAndDirections);

    void writeLock();

    void unLock();
}
