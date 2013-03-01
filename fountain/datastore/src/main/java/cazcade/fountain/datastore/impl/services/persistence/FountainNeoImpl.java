/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.common.service.AbstractServiceStateMachine;
import cazcade.fountain.datastore.api.*;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.fountain.datastore.impl.io.FountainNeoExporter;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.impl.UUIDFactory;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cazcade.fountain.datastore.impl.FountainRelationships.*;
import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;


/**
 * @author Neil Ellis
 *         <p/>
 *         The big TODO list starts here:
 *         <p/>
 *         todo : use assertAuthorized() on all public methods, this makes sure we don't allow slip ups and spot them early.
 */

public final class FountainNeoImpl extends AbstractServiceStateMachine implements FountainNeo {

    @Nonnull
    public static final String            SYSTEM                     = "system";
    @Nonnull
    public static final SessionIdentifier SYSTEM_FAKE_SESSION        = new SessionIdentifier(SYSTEM, null);
    public static final String            DEFAULT_POOL_PERMISSIONS   = String.valueOf(PermissionSet.getDefaultPermissions()
                                                                                                   .toString());
    @Nonnull
    public static final String            BOARDS_URI                 = "pool:///boards";
    @Nonnull
    public static final LURI              SYSTEM_ALIAS_URI           = new LURI(LiquidURIScheme.alias, "cazcade:system");
    @Nonnull
    public static final String            FREE_TEXT_SEARCH_INDEX_KEY = "ftsindex";
    public static final String publicPermissionValue;
    public static final String sharedPermissionValue;
    public static final String dropPermissionValue;
    public static final String privatePermissionValue;
    public static final String minimalPermissionValue;
    public static final String publicPermissionNoDeleteValue;
    public static final String sharedPermissionNoDeleteValue;
    public static final String dropPermissionNoDeleteValue;
    public static final String privatePermissionNoDeleteValue;
    public static final String minimalPermissionNoDeleteValue;
    public static final String defaultPermissionNoDeleteValue;
    public static final String privateSharedPermissionValue;
    public static final String privateSharedPermissionNoDeleteValue;
    public static final  int                      MAX_COMMENTS_DEFAULT                         = 25;
    public static final  int                      AUTHORIZATION_FOR_WRITE_TIME_TO_LIVE_SECONDS = 60;
    public static final  int                      AUTHORIZATION_FOR_READ_TIME_TO_LIVE_SECONDS  = 2;
    @Nonnull
    public static final  String                   NODE_INDEX_NAME                              = "nodes";
    /**
     * How old an inactive session can be before scheduled for deletion.
     */
    public static final  int                      SESSION_EXPIRES_MILLI                        = 1000 * 3600 * 24 * 7;
    /**
     * How long in milliseconds before we decide a user's session is now inactive.
     * Currently this is based on the heartbeat (i.e. notification timeout).
     */
    public static final  int                      SESSION_INACTIVE_MILLI                       = 5 * 60 * 1000;
    @Nonnull
    private static final Logger                   log                                          = Logger.getLogger(FountainNeoImpl.class);
    @Nonnull
    private static final String                   ROOT_POOL_PROPERTY                           = "root_pool________";
    @Nonnull
    private static final String                   FALSE                                        = "false";
    private static       ThreadLocal<Transaction> currentTransaction                           = new ThreadLocal<Transaction>();
    private final        ScheduledExecutorService backupScheduler                              = Executors.newScheduledThreadPool(1);
    //    private final static Logger log = Logger.getLogger(FountainNeo.class);
    private EmbeddedGraphDatabase neo;
    private Index<Node>           indexService;
    private Index<Node>           fulltextIndexService;
    private PersistedEntity       rootPool;
    private PersistedEntity       peoplePool;
    private boolean               started;

    public FountainNeoImpl() throws Exception {
        super();
    }

    static Transaction getTransactionInternal() {return currentTransaction.get();}

    static {
        try {
            publicPermissionValue = PermissionSet.getPublicPermissionSet().toString();
            sharedPermissionValue = PermissionSet.getSharedPermissionSet().toString();
            dropPermissionValue = PermissionSet.getWriteOnlyPermissionSet().toString();
            privatePermissionValue = PermissionSet.getPrivatePermissionSet().toString();
            privateSharedPermissionValue = PermissionSet.getPrivateSharedPermissionSet().toString();
            minimalPermissionValue = PermissionSet.getMinimalPermissionSet().toString();
            publicPermissionNoDeleteValue = PermissionSet.getPublicNoDeletePermissionSet().toString();
            sharedPermissionNoDeleteValue = PermissionSet.getSharedNoDeletePermissionSet().toString();
            dropPermissionNoDeleteValue = PermissionSet.getWriteOnlyNoDeletePermissionSet().toString();
            privatePermissionNoDeleteValue = PermissionSet.getPrivateNoDeletePermissionSet().toString();
            privateSharedPermissionNoDeleteValue = PermissionSet.getPrivateSharedNoDeletePermissionSet().toString();
            minimalPermissionNoDeleteValue = PermissionSet.getMinimalNoDeletePermissionSet().toString();
            defaultPermissionNoDeleteValue = PermissionSet.getDefaultPermissionsNoDelete().toString();
        } catch (Exception e) {
            log.error(e);
            throw new Error(e);
        }
    }

    @Override
    public void assertAuthorized(@Nonnull final PersistedEntity persistedEntity, @Nonnull final SessionIdentifier identity, final Permission... permissions) throws InterruptedException {
        if (!persistedEntity.allowed(identity, permissions)) {
            throw new AuthorizationException("Session " +
                                             identity.toString() +
                                             " is not authorized to " +
                                             Arrays.toString(permissions) +
                                             " the resource " +
                                             (persistedEntity.has(Dictionary.URI)
                                              ? persistedEntity.$(Dictionary.URI)
                                              : "<unknown>") +
                                             " permissions are " +
                                             persistedEntity.$(Dictionary.PERMISSIONS));
        }
    }

    @Override
    public void backup() throws Exception {
        final String dir = CommonConstants.NEO_BACKUP_DIR + '/' + System.currentTimeMillis();
        new File(dir).mkdirs();
        new FountainNeoExporter(neo).export(dir);
    }

    @Override
    public Transaction beginTx() {
        Transaction transaction = neo.beginTx();
        currentTransaction.set(transaction);
        return transaction;
    }

    @Override @Nonnull
    public PersistedEntity changeNodePermissionNoTx(@Nonnull final PersistedEntity entity, @Nonnull final SessionIdentifier editor, final PermissionChangeType change) throws Exception {
        entity.assertLatestVersion();
        PersistedEntity startPersistedEntityImpl = entity;
        final Traverser traverser = startPersistedEntityImpl.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL, CHILD, OUTGOING);
        final Collection<Node> allNodes = traverser.getAllNodes();
        log.debug("Changing permission on {0} nodes to {1} .", allNodes.size(), change);
        for (final Node origNode : allNodes) {
            final PersistedEntity cloned = cloneNodeForNewVersion(editor, new FountainEntity(origNode), false);
            final String permissions = cloned.$(Dictionary.PERMISSIONS);
            final PermissionSet newPermissions = PermissionSet.createPermissionSet(permissions)
                                                              .convertChangeRequestIntoPermissionSet(change);
            cloned.$(Dictionary.PERMISSIONS, newPermissions.toString());
            //So we have cloned the start persistedEntityImpl and need to use the cloned version to build our result.
            if (origNode.getId() == startPersistedEntityImpl.getPersistenceId()) {
                startPersistedEntityImpl = cloned;
            }
        }
        return startPersistedEntityImpl;
    }

    @Override @Nullable
    public TransferEntity changePermissionNoTx(@Nonnull final SessionIdentifier editor, @Nonnull final LURI uri, final PermissionChangeType change, final RequestDetailLevel detail, final boolean internal) throws Exception {
        begin();
        try {
            PersistedEntity startPersistedEntityImpl = find(uri);
            if (startPersistedEntityImpl == null) {
                throw new EntityNotFoundException("Could not find entity %s", uri);
            }
            startPersistedEntityImpl = changeNodePermissionNoTx(startPersistedEntityImpl, editor, change);
            log.debug("Changed permission of {0} to {1}", uri, change);
            return startPersistedEntityImpl.toTransfer(detail, internal);
        } finally {
            end();
        }
    }

    @Override @Nonnull
    public PersistedEntity cloneNodeForNewVersion(@Nonnull final SessionIdentifier editor, @Nonnull final PersistedEntity entity, final boolean fork) throws InterruptedException {
        entity.writeLock();
        fulltextIndexService.remove(entity.getNeoNode(), FREE_TEXT_SEARCH_INDEX_KEY);
        final PersistedEntity clone = createNode();
        final Iterable<String> keys = entity.keys();
        for (final String key : keys) {
            final String value = entity.getValue(key);
            assert value != null;
            clone.setValue(key, value);
        }
        clone.$(Dictionary.ID, UUIDFactory.randomUUID().toString());
        indexBy(clone, Dictionary.ID, Dictionary.ID, true);
        final String versionString = entity.$(Dictionary.VERSION);

        migrateParentNode(entity, clone, fork);

        if (fork) {
            Long lastFork = null;
            if (entity.has(Dictionary.LAST_FORK_VERSION)) {
                lastFork = entity.$l(Dictionary.LAST_FORK_VERSION);
            }
            if (lastFork == null) {
                entity.$(Dictionary.LAST_FORK_VERSION, (long) 1);
                lastFork = 1L;
            } else {
                entity.$(Dictionary.LAST_FORK_VERSION, ++lastFork);
            }
            clone.$(Dictionary.VERSION, versionString + "." + lastFork + ".1");
        } else {
            reindex(clone, Dictionary.URI, Dictionary.URI);
            if (entity.has(FORK_PARENT, OUTGOING)) {
                final FountainRelationship relationship = entity.relationship(FORK_PARENT, OUTGOING);
                assert relationship != null;
                clone.relate(relationship.other(entity), FORK_PARENT);
            } else {
                clone.relate(entity, FORK_PARENT);
            }
            final int lastDot = versionString.lastIndexOf('.');
            final String newVersion;
            if (lastDot < 0) {
                newVersion = String.valueOf(Long.parseLong(versionString) + 1);
            } else {
                newVersion = versionString.substring(0, lastDot) + "." + (Long.parseLong(versionString.substring(lastDot + 1)) + 1);
            }
            clone.$(Dictionary.VERSION, newVersion);
        }

        final FountainEntity editorFountainEntityImpl = find(editor.aliasURI());
        //TODO: remove this restriction in later version of Neo4J (i.e. when it supports self references)
        assert editorFountainEntityImpl != null;
        if (!editorFountainEntityImpl.equals(clone)) {
            clone.relate(editorFountainEntityImpl, EDITOR);
        }

        final Iterable<FountainRelationship> relationships = entity.relationships(FOLLOW_CONTENT, FOLLOW_ALIAS, AUTHOR, CREATOR, OWNER, VIEW, ALIAS);
        for (final FountainRelationship relationship : relationships) {
            if (relationship.start().equals(entity)) {
                clone.relate(relationship.end(), relationship.type());
                if (relationship.type() == AUTHOR ||
                    relationship.type() == CREATOR ||
                    relationship.type() == OWNER ||
                    relationship.type() == VIEW) {
                    //preserve these relationships
                } else {
                    relationship.delete();
                }
            } else if (relationship.end().equals(entity)) {
                relationship.start().relate(clone, relationship.type());
                relationship.delete();

            }
        }
        clone.relate(entity, VERSION_PARENT);
        clone.timestamp();
        return clone;
    }

    @Override @Nonnull
    public FountainEntity createNode() {
        final FountainEntity entity = new FountainEntity(neo.createNode());
        entity.$(Dictionary.VERSION, "1").publishTimestamp().timestamp();
        return entity;
    }

    @Override @Nonnull
    public PersistedEntity createSystemPool(final String pool) throws InterruptedException {
        final PersistedEntity systemPool = createNode().setValue(ROOT_POOL_PROPERTY, "true")
                .setIDIfNotSetOnNode()
                .$(Dictionary.URI, pool)
                .$(Dictionary.TYPE, Types.T_POOL2D.getValue())
                .$(Dictionary.PERMISSIONS, DEFAULT_POOL_PERMISSIONS)
                .$(Dictionary.NAME, "");
        indexBy(systemPool, Dictionary.ID, Dictionary.ID, true);
        indexBy(systemPool, Dictionary.URI, Dictionary.URI, true);
        return systemPool;
    }

    @Override
    public void delete(@Nonnull final PersistedEntity persistedEntity) {
        //the deleted attribute is special, it can't actually be set on the Fountain FountainEntity, it must be set directly on the underlying Neo persistedEntityImpl.
        persistedEntity.getNeoNode().setProperty(Dictionary.DELETED.getKeyName(), "true");
        indexService.remove(persistedEntity.getNeoNode(), persistedEntity.$(Dictionary.URI));
        fulltextIndexService.remove(persistedEntity.getNeoNode(), FREE_TEXT_SEARCH_INDEX_KEY);
    }

    @Override @Nonnull
    public TransferEntity deleteEntityTx(@Nonnull final LURI uri, final boolean children, final boolean internal, final RequestDetailLevel detail) throws InterruptedException {
        final PersistedEntity persistedEntity = find(uri);
        if (persistedEntity == null) {
            throw new EntityNotFoundException("Could not find persistedEntityImpl identified by %s.", uri);
        }

        return deleteNodeTx(children, internal, persistedEntity, detail);
    }

    @Override @Nonnull
    public TransferEntity deleteEntityTx(@Nonnull final LiquidUUID objectId, final boolean children, final boolean internal, final RequestDetailLevel detail) throws InterruptedException {
        final PersistedEntity persistedEntity = find(objectId);

        return deleteNodeTx(children, internal, persistedEntity, detail);
    }

    @Override @Nonnull
    public TransferEntity deleteNodeTx(final boolean children, final boolean internal, @Nonnull final PersistedEntity persistedEntity, final RequestDetailLevel detail) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                delete(persistedEntity);
                if (children) {
                    final Traverser traverser = persistedEntity.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
                        public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                            return new FountainEntity(currentPos.currentNode()).has(Dictionary.URI);
                        }
                    }, CHILD, OUTGOING, VIEW, OUTGOING);
                    for (final Node childNode : traverser) {
                        delete(new FountainEntity(childNode));
                    }
                }
                transaction.success();
                return persistedEntity.toTransfer(detail, internal);
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } finally {
            end();
        }
    }

    @Override
    public <T> T doInBeginBlock(@Nonnull final Callable<T> callable) throws Exception {
        begin();
        try {
            final T result = callable.call();

            return result;
        } finally {
            end();
        }
    }

    @Override
    public <T> T doInTransaction(@Nonnull final Callable<T> callable) throws Exception {
        final Transaction transaction = beginTx();
        try {
            final T result = callable.call();
            transaction.success();
            return result;
        } catch (Exception e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }

    @Override
    public <T> T doInTransactionAndBeginBlock(@Nonnull final Callable<T> callable) throws Exception {
        begin();
        try {
            final Transaction transaction = beginTx();
            try {
                final T result = callable.call();
                transaction.success();
                return result;
            } catch (Exception e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } finally {
            end();
        }
    }

    @Override @Nullable
    public FountainEntity findByURI(@Nonnull final LURI uri, final boolean mustMatch) throws InterruptedException {
        begin();
        try {
            final IndexHits<Node> nodes = indexService.get(Dictionary.URI.getKeyName(), uri.asString());
            FountainEntity matchingFountainEntityImpl = null;
            int nodeCount = 0;
            for (final Node node : nodes) {
                if (!new FountainEntity(node).deleted()) {
                    nodeCount++;
                    matchingFountainEntityImpl = new FountainEntity(node);
                }
            }
            if (nodeCount > 1) {
                log.error("Duplicate entity found for URI %s.", uri.asString());
            }
            if (matchingFountainEntityImpl == null && mustMatch) {
                throw new EntityNotFoundException("Could not locate " + uri);
            } else {
                //                if (matchingFountainEntityImpl != null) {
                //                    matchingFountainEntityImpl.assertLatestVersion();
                //                }
                return matchingFountainEntityImpl;
            }
        } finally {
            end();
        }
    }

    @Override @Nullable
    public FountainEntity find(@Nonnull final LURI uri) throws InterruptedException {
        return findByURI(uri, false);
    }

    @Override @Nonnull
    public FountainEntity findOrFail(@Nonnull final LURI uri) throws InterruptedException {
        //noinspection ConstantConditions
        return findByURI(uri, true);
    }

    @Override @Nonnull
    public FountainEntity find(@Nonnull final LiquidUUID id) throws InterruptedException {
        begin();
        try {
            final String idString = id.toString().toLowerCase();
            final Node neoNode = indexService.get(Dictionary.ID.getKeyName(), idString).getSingle();
            if (neoNode == null) {
                throw new EntityNotFoundException("Could not find persistedEntityImpl identified by %s.", idString);
            }
            final FountainEntity entity = new FountainEntity(neoNode);
            if (entity.deleted()) {
                throw new DeletedEntityException("Attempted to retrieve a deleted persistedEntityImpl.");
            }
            return entity;
        } finally {
            end();
        }
    }

    @Override
    public void freeTextIndexNoTx(@Nonnull final PersistedEntity persistedEntity) throws InterruptedException {
        final Entity entity = persistedEntity.toTransfer(RequestDetailLevel.FREE_TEXT_SEARCH_INDEX, true);
        final String freeText = entity.asFreeText();
        fulltextIndexService.add(persistedEntity.getNeoNode(), FREE_TEXT_SEARCH_INDEX_KEY, freeText);
    }

    @Override
    public TransferEntity freeTextSearch(final String searchText, final RequestDetailLevel detail, final boolean internal) throws InterruptedException {

        final IndexHits<Node> results = fulltextIndexService.query(FREE_TEXT_SEARCH_INDEX_KEY, searchText);
        final TransferEntity searchResultEntity = SimpleEntity.createNewTransferEntity(Types.T_SEARCH_RESULTS, UUIDFactory.randomUUID());
        final List<Entity> resultEntities = new ArrayList<Entity>();
        final List<String> dedupUrls = new ArrayList<String>();
        for (final Node r : results) {
            final PersistedEntity result = new FountainEntity(r);
            if (!dedupUrls.contains(result.$(Dictionary.URI))) {
                resultEntities.add(result.toTransfer(detail, internal));
            }
            dedupUrls.add(result.$(Dictionary.URI));
        }
        searchResultEntity.children(Dictionary.CHILD_A, resultEntities);
        return searchResultEntity;
    }

    @Override @Nullable
    public TransferEntity getEntityByUUID(@Nonnull final LiquidUUID id, final boolean internal, final RequestDetailLevel detail) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                final PersistedEntity persistedEntity = find(id);
                return persistedEntity.toTransfer(detail, internal);
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } finally {
            end();
        }
    }

    @Override
    public Index<Node> getIndexService() {
        return indexService;
    }

    @Override
    public PersistedEntity getPeoplePool() {
        return peoplePool;
    }

    @Override
    public PersistedEntity getRootPool() {
        return rootPool;
    }

    @Override
    public void setRootPool(final PersistedEntity rootPool) {
        this.rootPool = rootPool;
    }

    @Override
    public void unindex(@Nonnull final PersistedEntity persistedEntity, @Nonnull final Attribute key, final String luceneIndex) {
        final String value = persistedEntity.$(key).toLowerCase();
        log.debug("Un-indexing persistedEntityImpl " +
                  persistedEntity.getPersistenceId() +
                  " with key " +
                  key +
                  " with value " +
                  value);
        indexService.remove(persistedEntity.getNeoNode(), luceneIndex, value);
    }

    @Override @Nonnull
    public TransferEntity updateEntityByURITx(@Nonnull final SessionIdentifier editor, @Nonnull final LURI uri, @Nonnull final TransferEntity entity, final boolean internal, final RequestDetailLevel detail, @Nullable final Runnable onRenameAction) throws Exception {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                return updateNodeNoTx(editor, entity, internal, detail, transaction, findForWrite(uri), onRenameAction);
            } catch (Exception e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } finally {
            end();
        }
    }

    @Override @Nonnull @Deprecated
    public TransferEntity updateEntityByUUIDTx(@Nonnull final SessionIdentifier editor, @Nonnull final LiquidUUID id, @Nonnull final TransferEntity entity, final boolean internal, final RequestDetailLevel detail, @Nullable final Runnable onRenameAction) throws Exception {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                if (!entity.id().equals(id)) {
                    throw new CannotChangeIdException("Tried to change the id of %s to %s", id.toString(), entity.id().toString());
                }
                final FountainEntity origFountainEntityImpl = find(id);

                return updateNodeNoTx(editor, entity, internal, detail, transaction, origFountainEntityImpl, onRenameAction);
            } catch (Exception e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } finally {
            end();
        }
    }

    @Override
    public PersistedEntity updateNodeAndReturnNodeNoTx(@Nonnull final SessionIdentifier editor, @Nonnull final PersistedEntity origPersistedEntityImpl, @Nonnull final TransferEntity entity, @Nullable final Runnable onRenameAction) throws Exception {
        return doInBeginBlock(new Callable<PersistedEntity>() {
            @Nonnull @Override
            public PersistedEntity call() throws Exception {
                //Timestampcheck has been removed as we use versioning now.
                //        if (persistedEntityImpl.has(Attribute.UPDATED.getKeyName())) {
                //            String entityValue = lsdEntity.$(Attribute.UPDATED);
                //            String nodeValue = (String) persistedEntityImpl.$(Attribute.UPDATED.getKeyName());
                //            long entityMilli = Long.parseLong(entityValue);
                //            long nodeMilli = Long.parseLong(nodeValue);
                //            if (entityMilli < nodeMilli) {
                //                throw new StaleUpdateException("Stale update attempted date on the stored entity is %s the supplied entity was %s (difference in milliseconds was: %s). ", new Date(nodeMilli), new Date(entityMilli), nodeMilli - entityMilli);
                //            }
                //        }
                final PersistedEntity persistedEntity = cloneNodeForNewVersion(editor, origPersistedEntityImpl, false);
                persistedEntity.mergeProperties(entity, true, false, onRenameAction);
                freeTextIndexNoTx(persistedEntity);
                return persistedEntity;
            }
        });
    }

    @Override
    public void updateSessionTx(@Nonnull final LiquidUUID session) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                find(session).timestamp().$(Dictionary.ACTIVE, true);
                transaction.success();
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } finally {
            end();
        }
    }

    @Override @Nullable
    public TransferEntity updateUnversionedEntityByUUIDTx(@Nonnull final LiquidUUID id, @Nonnull final TransferEntity entity, final boolean internal, final RequestDetailLevel detail, @Nullable final Runnable onRenameAction) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                final PersistedEntity persistedEntity = find(id);
                //Timestampcheck has been removed as we use versioning now.
                //        if (persistedEntityImpl.has(Attribute.UPDATED.getKeyName())) {
                //            String entityValue = lsdEntity.$(Attribute.UPDATED);
                //            String nodeValue = (String) persistedEntityImpl.$(Attribute.UPDATED.getKeyName());
                //            long entityMilli = Long.parseLong(entityValue);
                //            long nodeMilli = Long.parseLong(nodeValue);
                //            if (entityMilli < nodeMilli) {
                //                throw new StaleUpdateException("Stale update attempted date on the stored entity is %s the supplied entity was %s (difference in milliseconds was: %s). ", new Date(nodeMilli), new Date(entityMilli), nodeMilli - entityMilli);
                //            }
                //        }
                if (!entity.id().equals(id)) {
                    throw new CannotChangeIdException("Tried to change the id of %s to %s", id.toString(), entity.id().toString());
                }
                final TransferEntity newObject = persistedEntity.mergeProperties(entity, true, false, onRenameAction)
                                                                .toTransfer(detail, internal);
                freeTextIndexNoTx(persistedEntity);

                transaction.success();
                return newObject;
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } finally {
            end();
        }
    }

    @Override public Transaction getCurrentTransaction() {
        return getTransactionInternal();
    }

    @Override public FountainEntity findForWrite(@Nonnull final LURI uri) throws InterruptedException {
        FountainEntity entity;
        do {
            entity = findOrFail(uri);
            entity.writeLock();
            if (!entity.isLatestVersion()) {
                log.warn("Concurrent update to " + uri);
            }
        } while (!entity.isLatestVersion());
        return entity;
    }

    @Override
    public void indexBy(@Nonnull final PersistedEntity persistedEntity, @Nonnull final Attribute key, @Nonnull final Attribute luceneIndex, final boolean unique) throws InterruptedException {
        final String value = persistedEntity.$(key).toLowerCase();
        log.debug("Indexing persistedEntityImpl "
                  + persistedEntity.getPersistenceId()
                  + " with key "
                  + key
                  + " with value "
                  + value);
        final IndexHits<Node> hits = indexService.get(luceneIndex.getKeyName(), value);
        if (hits.size() > 0 && unique) {
            for (final Node hit : hits) {
                if (!new FountainEntity(hit).deleted()) {
                    throw new DuplicateEntityException("Attempted to index an entity with the %s of %s, there is already an entity with that %s value.", key, value, key);
                }
            }
        }
        indexService.add(persistedEntity.getNeoNode(), luceneIndex.getKeyName(), value);
    }

    @Override
    public void putProfileInformationIntoAlias(@Nonnull final PersistedEntity alias) {
        try {
            final LURI poolURI = new LURI("pool:///people/" + alias.$(Dictionary.NAME) + "/profile");
            final PersistedEntity pool = find(poolURI);
            if (pool == null) {
                throw new EntityNotFoundException("Could not locate pool %s", poolURI.toString());
            }
            if (pool.has(Dictionary.IMAGE_URL)) {
                alias.$(Dictionary.IMAGE_URL, pool.$(Dictionary.IMAGE_URL));
            }
            if (pool.has(Dictionary.IMAGE_WIDTH)) {
                alias.$(Dictionary.IMAGE_WIDTH, pool.$(Dictionary.IMAGE_WIDTH));
            }
            if (pool.has(Dictionary.IMAGE_HEIGHT)) {
                alias.$(Dictionary.IMAGE_HEIGHT, pool.$(Dictionary.IMAGE_HEIGHT));
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    //    private FountainEntity recalculateCentreImage(final FountainEntity pool, final FountainEntity object /*Do not delete this parameter */) throws Exception {
    //        final double[] distance = {Double.MAX_VALUE};
    //        forEachChild(pool, new NodeCallback() {
    //            public void call(FountainEntity child) throws InterruptedException {
    //                if (!deleted(child) && !child.$(TYPE).toString().startsWith("Collection.Pool") && child.has(Attribute.IMAGE_URL.getKeyName())) {
    //                    FountainEntity viewNode = child.relationship(VIEW, Direction.OUTGOING).other(child);
    //                    double thisdistance = Double.valueOf(viewNode.$(VIEW_RADIUS).toString());
    //                    if (thisdistance < distance[0]) {
    //                        distance[0] = thisdistance;
    //                        if (child.has(Attribute.IMAGE_URL.getKeyName())) {
    //                            pool.$(Attribute.IMAGE_URL.getKeyName(), child.$(Attribute.IMAGE_URL.getKeyName()));
    //                        }
    //                        if (child.has(Attribute.IMAGE_WIDTH.getKeyName())) {
    //                            pool.$(Attribute.IMAGE_WIDTH.getKeyName(), child.$(Attribute.IMAGE_WIDTH.getKeyName()));
    //                        }
    //                        if (child.has(Attribute.IMAGE_HEIGHT.getKeyName())) {
    //                            pool.$(Attribute.IMAGE_HEIGHT.getKeyName(), child.$(Attribute.IMAGE_HEIGHT.getKeyName()));
    //                        }
    //                    }
    //                }
    //            }
    //        }, max);
    //        pool.$(Attribute.INTERNAL_MIN_IMAGE_RADIUS.getKeyName(), String.valueOf(distance[0]));
    //        return pool;
    //    }


    //TODO get comments for historical versions!

    @Override
    public void recalculateURI(@Nonnull final PersistedEntity child) throws InterruptedException {
        final PersistedEntity parent = child.parent();
        if (child.$(Dictionary.TYPE).startsWith(Types.T_POOL.toString())) {
            child.$(Dictionary.URI, parent.$(Dictionary.URI) + '/' + child.$(Dictionary.NAME));
        } else {
            if (child.$(Dictionary.URI).startsWith("pool://")) {
                child.$(Dictionary.URI, parent.$(Dictionary.URI) + '#' + child.$(Dictionary.NAME));
            }
        }
        reindex(child, Dictionary.URI, Dictionary.URI);
    }

    @Override
    public void reindex(@Nonnull final PersistedEntity persistedEntity, @Nonnull final Attribute key, @Nonnull final Attribute luceneIndex) {
        indexService.remove(persistedEntity.getNeoNode(), luceneIndex.getKeyName());
        final String value = persistedEntity.$(key).toLowerCase();
        log.debug("Indexing persistedEntityImpl "
                  + persistedEntity.getPersistenceId()
                  + " with key "
                  + key
                  + " with value "
                  + value);
        final IndexHits<Node> hits = indexService.get(luceneIndex.getKeyName(), value);
        for (final Node hit : hits) {
            indexService.remove(hit, luceneIndex.getKeyName(), value);
        }
        indexService.add(persistedEntity.getNeoNode(), luceneIndex.getKeyName(), value);
    }

    @Override
    public void setPeoplePool(final PersistedEntity peoplePool) {
        this.peoplePool = peoplePool;
    }

    public void migrateParentNode(@Nonnull final PersistedEntity persistedEntity, @Nonnull final PersistedEntity clone, final boolean fork) {
        persistedEntity.assertLatestVersion();
        final Iterable<FountainRelationship> relationships = persistedEntity.getRelationshipsAsSet(CHILD, OUTGOING);
        for (final FountainRelationship relationship : relationships) {
            final PersistedEntity childPersistedEntityImpl = relationship.other(persistedEntity);
            clone.relate(childPersistedEntityImpl, CHILD);
            relationship.delete();
        }
        if (!fork) {
            final FountainRelationship parentRel = persistedEntity.relationship(CHILD, INCOMING);
            if (parentRel != null) {
                final PersistedEntity parentPersistedEntity = parentRel.other(persistedEntity);
                parentPersistedEntity.timestamp().relate(clone, CHILD);
                parentRel.delete();
            }
        }
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void start() throws Exception {
        try {
            super.start();

            final HashMap<String, String> params = new HashMap<String, String>();
            //            params.put("allow_store_upgrade", "true");
            params.put("enable_remote_shell", "true");

            neo = new EmbeddedGraphDatabase(cazcade.fountain.datastore.impl.Constants.FOUNTAIN_NEO_STORE_DIR, params);
            //        wrappingNeoServerBootstrapper = new WrappingNeoServerBootstrapper(neo);
            //        wrappingNeoServerBootstrapper.start();
            //
            //        XaDataSourceManager xaDsMgr = neo.getConfig().getTxModule().getXaDataSourceManager();
            //        XaDataSource dataSource = xaDsMgr.getXaDataSource("nioneodb");
            //        dataSource.keepLogicalLogs(true);
            //        dataSource = xaDsMgr.getXaDataSource("lucene");
            //        dataSource.keepLogicalLogs(true);
            //        dataSource.setAutoRotate(true);
            //        dataSource.setLogicalLogTargetSize(10 * 1024 * 1024); // 10 MB
            //
            if (CommonConstants.IS_PRODUCTION) {
                backupScheduler.scheduleWithFixedDelay(new NeoBackupJob(this), 1, 4, TimeUnit.HOURS);
                backup();
            }


            log.info("Neo started.");
            indexService = neo.index().forNodes(NODE_INDEX_NAME, MapUtil.stringMap("type", "exact"));
            fulltextIndexService = neo.index()
                                      .forNodes(FountainNeoImpl.FREE_TEXT_SEARCH_INDEX_KEY, MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "type", "fulltext"));

            final Transaction transaction = neo.beginTx();
            try {
                final FountainEntity rootPool = find(new LURI("pool:///"));
                assert rootPool != null;
                setRootPool(rootPool);
                final FountainEntity peoplePool = find(new LURI("pool:///people"));
                assert peoplePool != null;
                setPeoplePool(peoplePool);
                transaction.success();
                this.started = true;
            } catch (RuntimeException e) {
                e.printStackTrace();
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } catch (Exception e) {
            log.error(e);
            throw new Error("Catastrophic failure, could not start FountainNeo in directory "
                            + cazcade.fountain.datastore.impl.Constants.FOUNTAIN_NEO_STORE_DIR
                            + " make sure no other instance is using this directory and that permissions are correct.", e);
        }
    }

    @Override
    public void stop() {
        super.stop();
        neo.shutdown();
        //        wrappingNeoServerBootstrapper.stop();
        log.info("Shutdown Neo.");
        log.info("Shutdown Fountain Neo service.");
        this.started = false;
    }

    @Nonnull
    public TransferEntity updateNodeNoTx(@Nonnull final SessionIdentifier editor, @Nonnull final TransferEntity entity, final boolean internal, final RequestDetailLevel detail, @Nonnull final Transaction transaction, @Nonnull final FountainEntity origFountainEntityImpl, @Nullable final Runnable onRenameAction) throws Exception {
        final PersistedEntity persistedEntity = updateNodeAndReturnNodeNoTx(editor, origFountainEntityImpl, entity, onRenameAction);
        final TransferEntity newObject = persistedEntity.toTransfer(detail, internal);
        transaction.success();
        return newObject;
    }
}
