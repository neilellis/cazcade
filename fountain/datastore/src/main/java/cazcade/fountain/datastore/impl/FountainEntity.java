package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import org.neo4j.graphdb.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This is the core persistent object used by Fountain. Like all LSDEntity objects it's type is derived from
 * a type attribute and all apart from the compulsory (uuid, uri, updated) properties are optional. Their
 * presence can usually be inferred by type. However we're deliberately working with sketchy data, because data
 * always is, so check for an attributes presence and have a back up plan if it's not there.
 *
 * @author neilellis@cazcade.com
 */
public interface FountainEntity extends LSDEntity {


    /**
     * This is a low impact identifier for the node, it should be treated with caution. If the entity
     * is in Neo4J then consider the warnings here {@link org.neo4j.graphdb.Node#getId()}. Basically
     * it's good for cache keys and that's about it.
     *
     * @return a numerical persistent identifier.
     */
    long getPersistenceId();

    /**
     * Use with great caution, deletes the underlying persistent store value, should only
     * be used on extremely transient data like sessions.
     */
    void hardDelete();

    @Nonnull
    Iterable<FountainRelationship> getRelationships();

    @Nonnull
    Iterable<FountainRelationship> getRelationships(FountainRelationships... types);

    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nonnull
    Iterable<FountainRelationship> getRelationships(FountainRelationships type, Direction dir);

    @SuppressWarnings({"TypeMayBeWeakened"})
    boolean hasRelationship(FountainRelationships type, Direction dir);

    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nullable
    FountainRelationship getSingleRelationship(FountainRelationships type, Direction dir);

    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nonnull
    FountainRelationship createRelationshipTo(@Nonnull FountainEntity otherEntity, FountainRelationships type);

    Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator, FountainRelationships relationshipType, Direction direction);

    Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator, RelationshipType firstRelationshipType, Direction firstDirection, RelationshipType secondRelationshipType, Direction secondDirection);

    Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator, Object... relationshipTypesAndDirections);

    Iterable<String> getPropertyKeys();

    @Nonnull
    @Deprecated
    org.neo4j.graphdb.Node getNeoNode();

    @Nonnull
    FountainEntity mergeProperties(@Nonnull LSDEntity entity, boolean update, boolean ignoreType, @Nullable Runnable onRenameAction) throws InterruptedException;

    @Nonnull
    FountainEntity getLatestVersionFromFork();

    int popularity();

    @Nonnull
    FountainEntity parentNode();

    void setIDIfNotSetOnNode();

    @Override
    void timestamp();

    boolean isDeleted() throws InterruptedException;

    void copyValuesToEntity(@Nonnull LSDEntity entity, @Nonnull LSDAttribute... attributes);

    @Nullable
    LSDEntity convertNodeToLSD(LiquidRequestDetailLevel detail, boolean internal) throws InterruptedException;

    boolean isOwner(FountainEntity ownerFountainEntity) throws InterruptedException;

    boolean isAuthor(FountainEntity ownerFountainEntity) throws InterruptedException;

    boolean isOwner(@Nonnull LiquidSessionIdentifier identity) throws InterruptedException;

    boolean isLatestVersion();

    void assertLatestVersion();

    void forEachChild(@Nonnull NodeCallback callback) throws Exception;

    /**
     * Okay so this should really go somewhere else., in fact I'm not sure if it's even relevant anymore.
     *
     * @return the radius.
     */
    double calculateRadius();

    /**
     * Does this entity belong to a containing entity that is listed somewhere.
     *
     * @return true if it is.
     */
    boolean isListed();

    /**
     * Inherit permissions from a parent entity.
     *
     * @param parent a parent entity.
     */
    void inheritPermissions(@Nonnull LSDEntity parent);

    boolean isAuthorized(@Nonnull LiquidSessionIdentifier identity, @Nonnull LiquidPermission... permissions) throws InterruptedException;

    void setPermissionFlagsOnEntity(@Nonnull LiquidSessionIdentifier session, @Nullable FountainEntity parent, @Nonnull LSDEntity entity) throws InterruptedException;
}
