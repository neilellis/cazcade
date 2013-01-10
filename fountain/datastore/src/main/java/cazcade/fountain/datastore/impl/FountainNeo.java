/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import cazcade.fountain.common.service.ServiceStateMachine;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;

/**
 * The FountainNeo interface provides a wrapper around Neo4J for Fountain. Exposing a service oriented interface aimed at
 * low level operations. The other Fountain services that rely on Neo4J  use this interface to perform lower level operations.
 *
 * @author neilellis@cazcade.com
 * @todo: remove any org.neo4j classes from this interface, that includes beginTx :-) (should be done in a callback).
 */
public interface FountainNeo extends ServiceStateMachine {
    void assertAuthorized(@Nonnull LSDPersistedEntity persistedEntity, @Nonnull LiquidSessionIdentifier identity, LiquidPermission... permissions) throws InterruptedException;

    void backup() throws Exception;

    Transaction beginTx();

    @Nonnull
    LSDPersistedEntity changeNodePermissionNoTx(@Nonnull LSDPersistedEntity entity, @Nonnull LiquidSessionIdentifier editor, LiquidPermissionChangeType change) throws Exception;

    @Nullable
    LSDTransferEntity changePermissionNoTx(@Nonnull LiquidSessionIdentifier editor, @Nonnull LiquidURI uri, LiquidPermissionChangeType change, LiquidRequestDetailLevel detail, boolean internal) throws Exception;

    @Nonnull
    LSDPersistedEntity cloneNodeForNewVersion(@Nonnull LiquidSessionIdentifier editor, @Nonnull LSDPersistedEntity entity, boolean fork) throws InterruptedException;

    @Nonnull LSDPersistedEntity createNode();

    @Nonnull LSDPersistedEntity createSystemPool(String pool) throws InterruptedException;

    void delete(@Nonnull LSDPersistedEntity entity);

    @Nonnull
    LSDTransferEntity deleteEntityTx(@Nonnull LiquidURI uri, boolean children, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;

    @Nonnull
    LSDTransferEntity deleteEntityTx(@Nonnull LiquidUUID objectId, boolean children, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;


    @Nullable
    LSDTransferEntity deleteNodeTx(boolean children, boolean internal, @Nonnull LSDPersistedEntity persistedEntity, LiquidRequestDetailLevel detail) throws InterruptedException;

    <T> T doInBeginBlock(@Nonnull Callable<T> callable) throws Exception;

    <T> T doInTransaction(@Nonnull Callable<T> callable) throws Exception;

    <T> T doInTransactionAndBeginBlock(@Nonnull Callable<T> callable) throws Exception;

    /**
     * Find the {@link LSDPersistedEntity} entity that matches the supplied URI, if mustMatch is true then thius will throw an
     * EntityNotFoundException if there is no match, otherwise it returns null.
     *
     * @param uri       the uri of the LSDPersistedEntity we are looking for.
     * @param mustMatch if true then exception thrown if not found, otherwise return null.
     * @return the LSDPersistedEntity or null if mustMatch is false.
     * @throws InterruptedException    if the execution thread is interrupted.
     * @throws EntityNotFoundException if mustMatch is true and nothing matches the uri.
     */
    @Nullable
    LSDPersistedEntity findByURI(@Nonnull LiquidURI uri, boolean mustMatch) throws InterruptedException, EntityNotFoundException;

    /**
     * Equivalent of findByURI(uri, false)
     *
     * @see FountainNeo#findByURI(LiquidURI, boolean)
     */
    @Nullable LSDPersistedEntity findByURI(@Nonnull LiquidURI uri) throws InterruptedException;

    /**
     * Equivalent of findByURI(uri, false)
     *
     * @see FountainNeo#findByURI(LiquidURI, boolean)
     */
    @Nonnull LSDPersistedEntity findByURIOrFail(@Nonnull LiquidURI uri) throws InterruptedException;

    @Nonnull LSDPersistedEntity findByUUID(@Nonnull LiquidUUID id) throws InterruptedException;

    void freeTextIndexNoTx(@Nonnull LSDPersistedEntity persistedEntity) throws InterruptedException;

    LSDTransferEntity freeTextSearch(String searchText, LiquidRequestDetailLevel detail, boolean internal) throws InterruptedException;

    @Nullable
    LSDTransferEntity getEntityByUUID(@Nonnull LiquidUUID id, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException;

    /**
     * This needs to be deprecated as we don't want to expose the Node interface directly.
     *
     * @return the index service.
     */
    Index<Node> getIndexService();

    LSDPersistedEntity getPeoplePool();

    LSDPersistedEntity getRootPool();


    void indexBy(@Nonnull LSDPersistedEntity entity, @Nonnull LSDAttribute key, @Nonnull LSDAttribute luceneIndex, boolean unique) throws InterruptedException;

    void migrateParentNode(@Nonnull LSDPersistedEntity entity, @Nonnull LSDPersistedEntity clone, boolean fork);

    void putProfileInformationIntoAlias(@Nonnull LSDPersistedEntity alias);

    void recalculateURI(@Nonnull LSDPersistedEntity childPersistedEntity) throws InterruptedException;

    void reindex(@Nonnull LSDPersistedEntity entity, @Nonnull LSDAttribute key, @Nonnull LSDAttribute luceneIndex);

    void setPeoplePool(LSDPersistedEntity peoplePool);

    void setRootPool(LSDPersistedEntity rootPool);

    void unindex(@Nonnull LSDPersistedEntity entity, @Nonnull LSDAttribute key, String luceneIndex);

    @Nonnull
    LSDTransferEntity updateEntityByURITx(@Nonnull LiquidSessionIdentifier editor, @Nonnull LiquidURI uri, @Nonnull LSDTransferEntity entity, boolean internal, LiquidRequestDetailLevel detail, @Nullable Runnable onRenameAction) throws Exception;

    @Nonnull
    LSDTransferEntity updateEntityByUUIDTx(@Nonnull LiquidSessionIdentifier editor, @Nonnull LiquidUUID id, @Nonnull LSDTransferEntity entity, boolean internal, LiquidRequestDetailLevel detail, @Nullable Runnable onRenameAction) throws Exception;

    LSDPersistedEntity updateNodeAndReturnNodeNoTx(@Nonnull LiquidSessionIdentifier editor, @Nonnull LSDPersistedEntity origPersistedEntity, @Nonnull LSDTransferEntity entity, Runnable onRenameAction) throws Exception;

    void updateSessionTx(@Nonnull LiquidUUID session) throws InterruptedException;

    @Nullable
    LSDTransferEntity updateUnversionedEntityByUUIDTx(@Nonnull LiquidUUID id, @Nonnull LSDTransferEntity entity, boolean internal, LiquidRequestDetailLevel detail, @Nullable Runnable onRenameAction) throws InterruptedException;
}
