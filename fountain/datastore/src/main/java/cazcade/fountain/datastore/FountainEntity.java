package cazcade.fountain.datastore;

import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.NodeCallback;
import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import org.neo4j.graphdb.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainEntity extends LSDEntity {
    long getNeoId();

    void deleteNeo();

    @Nonnull
    Iterable<Relationship> getRelationships();

    @Nonnull
    Iterable<Relationship> getRelationships(FountainRelationships... types);

    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nonnull
    Iterable<Relationship> getRelationships(FountainRelationships type, Direction dir);

    @SuppressWarnings({"TypeMayBeWeakened"})
    boolean hasRelationship(FountainRelationships type, Direction dir);

    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nullable
    Relationship getSingleRelationship(FountainRelationships type, Direction dir);

    @SuppressWarnings({"TypeMayBeWeakened"})
    @Nonnull
    Relationship createRelationshipTo(@Nonnull FountainEntity otherEntity, FountainRelationships type);

    Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator, FountainRelationships relationshipType, Direction direction);

    Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator, RelationshipType firstRelationshipType, Direction firstDirection, RelationshipType secondRelationshipType, Direction secondDirection);

    Traverser traverse(Traverser.Order traversalOrder, StopEvaluator stopEvaluator, ReturnableEvaluator returnableEvaluator, Object... relationshipTypesAndDirections);

    Iterable<String> getPropertyKeys();

    @Nonnull
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

    double calculateRadius();

    boolean isListed();

    void inheritPermissions(@Nonnull LSDEntity parent);

    boolean isAuthorizedInternal(@Nullable LiquidSessionIdentifier identity, @Nonnull LiquidPermission... permissions) throws InterruptedException;

    boolean isAuthorized(@Nonnull LiquidSessionIdentifier identity, @Nonnull LiquidPermission... permissions) throws InterruptedException;

    void setPermissionFlagsOnEntity(@Nonnull LiquidSessionIdentifier session, @Nullable FountainEntity parent, @Nonnull LSDEntity entity) throws InterruptedException;
}
