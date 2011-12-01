package cazcade.fountain.datastore.impl;

import cazcade.fountain.common.service.ServiceStateMachine;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainNeo extends ServiceStateMachine {
    FountainEntity getRootPool();

    Transaction beginTx();

    void recalculateURI(@Nonnull FountainEntity childFountainEntity) throws InterruptedException;

    @Nullable
    FountainEntity findByURI(@Nonnull LiquidURI uri) throws InterruptedException;

    @Nullable
    FountainEntity findByURI(@Nonnull LiquidURI uri, boolean mustMatch) throws InterruptedException;

    @Nonnull
    FountainEntity createNode();

    @Nullable
    LSDEntity deleteEntityTx(@Nonnull LiquidURI uri, boolean children, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;

    @Nullable
    LSDEntity deleteEntityTx(@Nonnull LiquidUUID objectId, boolean children, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;

    @Nonnull
    FountainEntity findByUUID(@Nonnull LiquidUUID id) throws InterruptedException;

    @Nullable
    LSDEntity getEntityByUUID(@Nonnull LiquidUUID id, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;

    void assertAuthorized(@Nonnull FountainEntity fountainEntity, @Nonnull LiquidSessionIdentifier identity, LiquidPermission... permissions) throws InterruptedException;

    void backup() throws Exception;

    @Nullable
    LSDEntity updateUnversionedEntityByUUIDTx(@Nonnull LiquidUUID id, @Nonnull LSDEntity entity, boolean internal, LiquidRequestDetailLevel detail, @Nullable Runnable onRenameAction) throws InterruptedException;

    @Nullable
    LSDEntity updateEntityByUUIDTx(@Nonnull LiquidSessionIdentifier editor, @Nonnull LiquidUUID id, @Nonnull LSDEntity entity, boolean internal, LiquidRequestDetailLevel detail, @Nullable Runnable onRenameAction) throws Exception;

    FountainEntity updateNodeAndReturnNodeNoTx(@Nonnull LiquidSessionIdentifier editor, @Nonnull FountainEntity origFountainEntity, @Nonnull LSDEntity entity, Runnable onRenameAction) throws Exception;

    @Nullable
    LSDEntity updateEntityByURITx(@Nonnull LiquidSessionIdentifier editor, @Nonnull LiquidURI uri, @Nonnull LSDEntity entity, boolean internal, LiquidRequestDetailLevel detail, @Nullable Runnable onRenameAction) throws Exception;

    void updateSessionTx(@Nonnull LiquidUUID session) throws InterruptedException;

    void freeTextIndexNoTx(@Nonnull FountainEntity fountainEntity) throws InterruptedException;

    IndexHits<org.neo4j.graphdb.Node> freeTextSearch(String searchText);

    @Nullable
    LSDEntity changePermissionNoTx(@Nonnull LiquidSessionIdentifier editor, @Nonnull LiquidURI uri, LiquidPermissionChangeType change, LiquidRequestDetailLevel detail, boolean internal) throws Exception;

    @Nonnull
    FountainEntity changeNodePermissionNoTx(@Nonnull FountainEntity fountainEntityImpl, @Nonnull LiquidSessionIdentifier editor, LiquidPermissionChangeType change) throws Exception;

    <T> T doInTransaction(@Nonnull Callable<T> callable) throws Exception;

    <T> T doInBeginBlock(@Nonnull Callable<T> callable) throws Exception;

    <T> T doInTransactionAndBeginBlock(@Nonnull Callable<T> callable) throws Exception;

    Index<org.neo4j.graphdb.Node> getIndexService();


    void indexBy(@Nonnull FountainEntity fountainEntity, @Nonnull LSDAttribute key, @Nonnull LSDAttribute luceneIndex, boolean unique) throws InterruptedException;

    void reindex(@Nonnull FountainEntity fountainEntity, @Nonnull LSDAttribute key, @Nonnull LSDAttribute luceneIndex);

    void unindex(@Nonnull FountainEntity fountainEntity, @Nonnull LSDAttribute key, String luceneIndex);


    @Nullable
    LSDEntity deleteNodeTx(boolean children, boolean internal, @Nonnull FountainEntity fountainEntity, LiquidRequestDetailLevel detail) throws InterruptedException;

    void delete(@Nonnull FountainEntity fountainEntity);

    @Nonnull
    FountainEntity createSystemPool(String pool) throws InterruptedException;

    void migrateParentNode(@Nonnull FountainEntity fountainEntity, @Nonnull FountainEntity clone, boolean fork);

    @Nonnull
    FountainEntity cloneNodeForNewVersion(@Nonnull LiquidSessionIdentifier editor, @Nonnull FountainEntity fountainEntityImpl, boolean fork) throws InterruptedException;

    void putProfileInformationIntoAlias(@Nonnull FountainEntity alias);

    void setRootPool(FountainEntity rootPool);

    FountainEntity getPeoplePool();

    void setPeoplePool(FountainEntity peoplePool);

}
