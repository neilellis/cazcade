package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import org.neo4j.graphdb.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the core persistent object used by Fountain. Like all LSDBaseEntity objects it's type is derived from
 * a type attribute and all apart from the compulsory (uuid, uri, updated) properties are optional. Their
 * presence can usually be inferred by type. However we're deliberately working with sketchy data, because data
 * always is, so check for an attributes presence and have a back up plan if it's not there.
 *
 * @author neilellis@cazcade.com
 */
public interface LSDPersistedEntity extends LSDBaseEntity {
    void assertLatestVersion();

    /**
     * Okay so this should really go somewhere else., in fact I'm not sure if it's even relevant anymore.
     *
     * @return the radius.
     */
    double calculateRadius();

    @Nullable
    LSDTransferEntity convertNodeToLSD(@Nonnull LiquidRequestDetailLevel detail, boolean internal) throws InterruptedException;

    void copyValuesToEntity(@Nonnull LSDBaseEntity entity, @Nonnull LSDAttribute... attributes);

    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nonnull
    FountainRelationship createRelationshipTo(@Nonnull LSDPersistedEntity otherEntity, FountainRelationships type);

    void forEachChild(@Nonnull NodeCallback callback) throws Exception;

    @Nonnull
    LSDPersistedEntity getLatestVersionFromFork();

    @Nonnull
    @Deprecated
    org.neo4j.graphdb.Node getNeoNode();


    /**
     * This is a low impact identifier for the node, it should be treated with caution. If the entity
     * is in Neo4J then consider the warnings here {@link org.neo4j.graphdb.Node#getId()}. Basically
     * it's good for cache keys and that's about it.
     *
     * @return a numerical persistent identifier.
     */
    long getPersistenceId();

    Iterable<String> getPropertyKeys();

    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nonnull
    Iterable<FountainRelationship> getRelationships(FountainRelationships type, Direction dir);

    @Nonnull
    Iterable<FountainRelationship> getRelationships(FountainRelationships... types);

    @Nonnull
    Iterable<FountainRelationship> getRelationships();

    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nullable
    FountainRelationship getSingleRelationship(FountainRelationships type, Direction dir);

    /**
     * Use with great caution, deletes the underlying persistent store value, should only
     * be used on extremely transient data like sessions.
     */
    void hardDelete();

    @SuppressWarnings({"TypeMayBeWeakened"})
    boolean hasRelationship(FountainRelationships type, Direction dir);

    /**
     * Inherit permissions from a parent entity.
     *
     * @param parent a parent entity.
     */
    void inheritPermissions(@Nonnull LSDBaseEntity parent);

    boolean isAuthor(@Nonnull LSDPersistedEntity ownerPersistedEntity) throws InterruptedException;

    boolean isAuthorized(@Nonnull LiquidSessionIdentifier identity, @Nonnull LiquidPermission... permissions)
            throws InterruptedException;

    boolean isDeleted() throws InterruptedException;

    boolean isLatestVersion();

    /**
     * Does this entity belong to a containing entity that is listed somewhere.
     *
     * @return true if it is.
     */
    boolean isListed();

    boolean isOwner(@Nonnull LSDPersistedEntity ownerPersistedEntity) throws InterruptedException;

    boolean isOwner(@Nonnull LiquidSessionIdentifier identity) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity mergeProperties(@Nonnull LSDTransferEntity source, boolean update, boolean ignoreType,
                                       @Nullable Runnable onRenameAction)
            throws InterruptedException;

    @Nonnull
    LSDPersistedEntity parentNode();

    int popularity();

    void setIDIfNotSetOnNode();

    void setPermissionFlagsOnEntity(@Nonnull LiquidSessionIdentifier session, @Nullable LSDPersistedEntity parent,
                                    @Nonnull LSDBaseEntity entity)
            throws InterruptedException;

    @Override
    void timestamp();

    void publishTimestamp();

    @Nonnull
    Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator,
                       RelationshipType firstRelationshipType, Direction firstDirection, RelationshipType secondRelationshipType,
                       Direction secondDirection);

    @Nonnull
    Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator,
                       FountainRelationships relationshipType, Direction direction);

    @Nonnull
    Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator,
                       Object... relationshipTypesAndDirections);

    void modifiedTimestamp();
}
