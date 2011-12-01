package cazcade.fountain.datastore.impl;

import cazcade.fountain.common.service.ServiceStateMachine;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
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
    LSDPersistedEntity getRootPool();

    Transaction beginTx();

    void recalculateURI(@Nonnull LSDPersistedEntity childPersistedEntity) throws InterruptedException;

    @Nullable
    LSDPersistedEntity findByURI(@Nonnull LiquidURI uri) throws InterruptedException;

    @Nullable
    LSDPersistedEntity findByURI(@Nonnull LiquidURI uri, boolean mustMatch) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity createNode();

    @Nullable
    LSDTransferEntity deleteEntityTx(@Nonnull LiquidURI uri, boolean children, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;

    @Nullable
    LSDTransferEntity deleteEntityTx(@Nonnull LiquidUUID objectId, boolean children, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;

    @Nonnull
    LSDPersistedEntity findByUUID(@Nonnull LiquidUUID id) throws InterruptedException;

    @Nullable
    LSDTransferEntity getEntityByUUID(@Nonnull LiquidUUID id, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;

    void assertAuthorized(@Nonnull LSDPersistedEntity persistedEntity, @Nonnull LiquidSessionIdentifier identity, LiquidPermission... permissions) throws InterruptedException;

    void backup() throws Exception;

    @Nullable
    LSDTransferEntity updateUnversionedEntityByUUIDTx(@Nonnull LiquidUUID id, @Nonnull LSDTransferEntity entity, boolean internal, LiquidRequestDetailLevel detail, @Nullable Runnable onRenameAction) throws InterruptedException;

    @Nullable
    LSDTransferEntity updateEntityByUUIDTx(@Nonnull LiquidSessionIdentifier editor, @Nonnull LiquidUUID id, @Nonnull LSDTransferEntity entity, boolean internal, LiquidRequestDetailLevel detail, @Nullable Runnable onRenameAction) throws Exception;

    LSDPersistedEntity updateNodeAndReturnNodeNoTx(@Nonnull LiquidSessionIdentifier editor, @Nonnull LSDPersistedEntity origPersistedEntity, @Nonnull LSDTransferEntity entity, Runnable onRenameAction) throws Exception;

    @Nullable
    LSDTransferEntity updateEntityByURITx(@Nonnull LiquidSessionIdentifier editor, @Nonnull LiquidURI uri, @Nonnull LSDTransferEntity entity, boolean internal, LiquidRequestDetailLevel detail, @Nullable Runnable onRenameAction) throws Exception;

    void updateSessionTx(@Nonnull LiquidUUID session) throws InterruptedException;

    void freeTextIndexNoTx(@Nonnull LSDPersistedEntity persistedEntity) throws InterruptedException;

    IndexHits<org.neo4j.graphdb.Node> freeTextSearch(String searchText);

    @Nullable
    LSDTransferEntity changePermissionNoTx(@Nonnull LiquidSessionIdentifier editor, @Nonnull LiquidURI uri, LiquidPermissionChangeType change, LiquidRequestDetailLevel detail, boolean internal) throws Exception;

    @Nonnull
    LSDPersistedEntity changeNodePermissionNoTx(@Nonnull LSDPersistedEntity entity, @Nonnull LiquidSessionIdentifier editor, LiquidPermissionChangeType change) throws Exception;

    <T> T doInTransaction(@Nonnull Callable<T> callable) throws Exception;

    <T> T doInBeginBlock(@Nonnull Callable<T> callable) throws Exception;

    <T> T doInTransactionAndBeginBlock(@Nonnull Callable<T> callable) throws Exception;

    Index<org.neo4j.graphdb.Node> getIndexService();


    void indexBy(@Nonnull LSDPersistedEntity entity, @Nonnull LSDAttribute key, @Nonnull LSDAttribute luceneIndex, boolean unique) throws InterruptedException;

    void reindex(@Nonnull LSDPersistedEntity entity, @Nonnull LSDAttribute key, @Nonnull LSDAttribute luceneIndex);

    void unindex(@Nonnull LSDPersistedEntity entity, @Nonnull LSDAttribute key, String luceneIndex);


    @Nullable
    LSDTransferEntity deleteNodeTx(boolean children, boolean internal, @Nonnull LSDPersistedEntity persistedEntity, LiquidRequestDetailLevel detail) throws InterruptedException;

    void delete(@Nonnull LSDPersistedEntity entity);

    @Nonnull
    LSDPersistedEntity createSystemPool(String pool) throws InterruptedException;

    void migrateParentNode(@Nonnull LSDPersistedEntity entity, @Nonnull LSDPersistedEntity clone, boolean fork);

    @Nonnull
    LSDPersistedEntity cloneNodeForNewVersion(@Nonnull LiquidSessionIdentifier editor, @Nonnull LSDPersistedEntity entity, boolean fork) throws InterruptedException;

    void putProfileInformationIntoAlias(@Nonnull LSDPersistedEntity alias);

    void setRootPool(LSDPersistedEntity rootPool);

    LSDPersistedEntity getPeoplePool();

    void setPeoplePool(LSDPersistedEntity peoplePool);

}
