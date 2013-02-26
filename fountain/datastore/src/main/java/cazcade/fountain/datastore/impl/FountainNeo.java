/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import cazcade.fountain.common.service.ServiceStateMachine;
import cazcade.fountain.datastore.api.EntityNotFoundException;
import cazcade.fountain.datastore.impl.services.persistence.FountainEntity;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.Attribute;
import cazcade.liquid.api.lsd.TransferEntity;
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
    void assertAuthorized(@Nonnull PersistedEntity persistedEntity, @Nonnull SessionIdentifier identity, Permission... permissions) throws InterruptedException;

    void backup() throws Exception;

    Transaction beginTx();

    @Nonnull
    PersistedEntity changeNodePermissionNoTx(@Nonnull PersistedEntity entity, @Nonnull SessionIdentifier editor, PermissionChangeType change) throws Exception;

    @Nullable
    TransferEntity changePermissionNoTx(@Nonnull SessionIdentifier editor, @Nonnull LiquidURI uri, PermissionChangeType change, RequestDetailLevel detail, boolean internal) throws Exception;

    @Nonnull
    PersistedEntity cloneNodeForNewVersion(@Nonnull SessionIdentifier editor, @Nonnull PersistedEntity entity, boolean fork) throws InterruptedException;

    @Nonnull FountainEntity createNode();

    @Nonnull PersistedEntity createSystemPool(String pool) throws InterruptedException;

    void delete(@Nonnull PersistedEntity entity);

    @Nonnull
    TransferEntity deleteEntityTx(@Nonnull LiquidURI uri, boolean children, boolean internal, RequestDetailLevel detail) throws InterruptedException;

    @Nonnull @Deprecated
    TransferEntity deleteEntityTx(@Nonnull LiquidUUID objectId, boolean children, boolean internal, RequestDetailLevel detail) throws InterruptedException;


    @Nullable
    TransferEntity deleteNodeTx(boolean children, boolean internal, @Nonnull PersistedEntity persistedEntity, RequestDetailLevel detail) throws InterruptedException;

    <T> T doInBeginBlock(@Nonnull Callable<T> callable) throws Exception;

    <T> T doInTransaction(@Nonnull Callable<T> callable) throws Exception;

    <T> T doInTransactionAndBeginBlock(@Nonnull Callable<T> callable) throws Exception;

    /**
     * Find the {@link PersistedEntity} entity that matches the supplied URI, if mustMatch is true then thius will throw an
     * EntityNotFoundException if there is no match, otherwise it returns null.
     *
     * @param uri       the uri of the PersistedEntity we are looking for.
     * @param mustMatch if true then exception thrown if not found, otherwise return null.
     * @return the PersistedEntity or null if mustMatch is false.
     * @throws InterruptedException    if the execution thread is interrupted.
     * @throws EntityNotFoundException if mustMatch is true and nothing matches the uri.
     */
    @Nullable
    PersistedEntity findByURI(@Nonnull LiquidURI uri, boolean mustMatch) throws InterruptedException, EntityNotFoundException;

    /**
     * Equivalent of find(uri, false)
     *
     * @see FountainNeo#findByURI(LiquidURI, boolean)
     */
    @Nullable FountainEntity find(@Nonnull LiquidURI uri) throws InterruptedException;

    /**
     * Equivalent of find(uri, false)
     *
     * @see FountainNeo#findByURI(LiquidURI, boolean)
     */
    @Nonnull FountainEntity findOrFail(@Nonnull LiquidURI uri) throws InterruptedException;

    @Nonnull FountainEntity find(@Nonnull LiquidUUID id) throws InterruptedException;

    void freeTextIndexNoTx(@Nonnull PersistedEntity persistedEntity) throws InterruptedException;

    TransferEntity freeTextSearch(String searchText, RequestDetailLevel detail, boolean internal) throws InterruptedException;

    @Nullable
    TransferEntity getEntityByUUID(@Nonnull LiquidUUID id, boolean internal, RequestDetailLevel detail) throws InterruptedException;

    /**
     * This needs to be deprecated as we don't want to expose the Node interface directly.
     *
     * @return the index service.
     */
    Index<Node> getIndexService();

    PersistedEntity getPeoplePool();

    PersistedEntity getRootPool();


    void indexBy(@Nonnull PersistedEntity entity, @Nonnull Attribute key, @Nonnull Attribute luceneIndex, boolean unique) throws InterruptedException;

    void putProfileInformationIntoAlias(@Nonnull PersistedEntity alias);

    void recalculateURI(@Nonnull PersistedEntity childPersistedEntity) throws InterruptedException;

    void reindex(@Nonnull PersistedEntity entity, @Nonnull Attribute key, @Nonnull Attribute luceneIndex);

    void setPeoplePool(PersistedEntity peoplePool);

    void setRootPool(PersistedEntity rootPool);

    void unindex(@Nonnull PersistedEntity entity, @Nonnull Attribute key, String luceneIndex);

    @Nonnull
    TransferEntity updateEntityByURITx(@Nonnull SessionIdentifier editor, @Nonnull LiquidURI uri, @Nonnull TransferEntity entity, boolean internal, RequestDetailLevel detail, @Nullable Runnable onRenameAction) throws Exception;

    @Nonnull @Deprecated
    TransferEntity updateEntityByUUIDTx(@Nonnull SessionIdentifier editor, @Nonnull LiquidUUID id, @Nonnull TransferEntity entity, boolean internal, RequestDetailLevel detail, @Nullable Runnable onRenameAction) throws Exception;

    PersistedEntity updateNodeAndReturnNodeNoTx(@Nonnull SessionIdentifier editor, @Nonnull PersistedEntity origPersistedEntity, @Nonnull TransferEntity entity, Runnable onRenameAction) throws Exception;

    void updateSessionTx(@Nonnull LiquidUUID session) throws InterruptedException;

    @Nullable
    TransferEntity updateUnversionedEntityByUUIDTx(@Nonnull LiquidUUID id, @Nonnull TransferEntity entity, boolean internal, RequestDetailLevel detail, @Nullable Runnable onRenameAction) throws InterruptedException;

    Transaction getCurrentTransaction();

    PersistedEntity findForWrite(@Nonnull LiquidURI uri) throws InterruptedException;
}
