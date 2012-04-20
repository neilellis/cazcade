package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.common.service.AbstractServiceStateMachine;
import cazcade.fountain.datastore.api.*;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
import cazcade.fountain.datastore.impl.io.FountainNeoExporter;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.impl.UUIDFactory;
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

import static cazcade.liquid.api.lsd.LSDAttribute.*;


/**
 * @author Neil Ellis
 *         <p/>
 *         The big TODO list starts here:
 *         <p/>
 *         todo : use assertAuthorized() on all public methods, this makes sure we don't allow slip ups and spot them early.
 */

public final class FountainNeoImpl extends AbstractServiceStateMachine implements FountainNeo {
    @Nonnull
    public static final String SYSTEM = "system";
    @Nonnull
    public static final LiquidSessionIdentifier SYSTEM_FAKE_SESSION = new LiquidSessionIdentifier(SYSTEM, null);
    public static final String DEFAULT_POOL_PERMISSIONS = String.valueOf(LiquidPermissionSet.getDefaultPermissions().toString());
    @Nonnull
    public static final String BOARDS_URI = "pool:///boards";
    @Nonnull
    public static final LiquidURI ADMIN_ALIAS_URI = new LiquidURI(LiquidURIScheme.alias, "cazcade:system");
    @Nonnull
    public static final String FREE_TEXT_SEARCH_INDEX_KEY = "ftsindex";

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
    public static final int MAX_COMMENTS_DEFAULT = 25;
    public static final int AUTHORIZATION_FOR_WRITE_TIME_TO_LIVE_SECONDS = 60;
    public static final int AUTHORIZATION_FOR_READ_TIME_TO_LIVE_SECONDS = 2;
    @Nonnull
    public static final String NODE_INDEX_NAME = "nodes";

    /**
     * How old an inactive session can be before scheduled for deletion.
     */
    public static final int SESSION_EXPIRES_MILLI = 1000 * 3600 * 24 * 7;

    /**
     * How long in milliseconds before we decide a user's session is now inactive.
     * Currently this is based on the heartbeat (i.e. notification timeout).
     */
    public static final int SESSION_INACTIVE_MILLI = 5 * 60 * 1000;


    @Nonnull
    private static final Logger log = Logger.getLogger(FountainNeoImpl.class);
    @Nonnull
    private static final String ROOT_POOL_PROPERTY = "root_pool________";
    @Nonnull
    private static final String FALSE = "false";

    //    private final static Logger log = Logger.getLogger(FountainNeo.class);
    private EmbeddedGraphDatabase neo;
    private Index<Node> indexService;
    private Index<Node> fulltextIndexService;


    private LSDPersistedEntity rootPool;
    private LSDPersistedEntity peoplePool;

    private final ScheduledExecutorService backupScheduler =
            Executors.newScheduledThreadPool(1);
    private boolean started;


    static {
        try {
            publicPermissionValue = LiquidPermissionSet.getPublicPermissionSet().toString();
            sharedPermissionValue = LiquidPermissionSet.getSharedPermissionSet().toString();
            dropPermissionValue = LiquidPermissionSet.getWriteOnlyPermissionSet().toString();
            privatePermissionValue = LiquidPermissionSet.getPrivatePermissionSet().toString();
            privateSharedPermissionValue = LiquidPermissionSet.getPrivateSharedPermissionSet().toString();
            minimalPermissionValue = LiquidPermissionSet.getMinimalPermissionSet().toString();
            publicPermissionNoDeleteValue = LiquidPermissionSet.getPublicNoDeletePermissionSet().toString();
            sharedPermissionNoDeleteValue = LiquidPermissionSet.getSharedNoDeletePermissionSet().toString();
            dropPermissionNoDeleteValue = LiquidPermissionSet.getWriteOnlyNoDeletePermissionSet().toString();
            privatePermissionNoDeleteValue = LiquidPermissionSet.getPrivateNoDeletePermissionSet().toString();
            privateSharedPermissionNoDeleteValue = LiquidPermissionSet.getPrivateSharedNoDeletePermissionSet().toString();
            minimalPermissionNoDeleteValue = LiquidPermissionSet.getMinimalNoDeletePermissionSet().toString();
            defaultPermissionNoDeleteValue = LiquidPermissionSet.getDefaultPermissionsNoDelete().toString();
        } catch (Exception e) {
            log.error(e);
            throw new Error(e);
        }
    }

    public FountainNeoImpl() throws Exception {
        super();
    }

    @Override
    public void assertAuthorized(@Nonnull final LSDPersistedEntity persistedEntity, @Nonnull final LiquidSessionIdentifier identity,
                                 final LiquidPermission... permissions) throws InterruptedException {
        if (!persistedEntity.isAuthorized(identity, permissions)) {
            throw new AuthorizationException("Session " +
                                             identity.toString() +
                                             " is not authorized to " +
                                             Arrays.toString(permissions
                                                            ) +
                                             " the resource " +
                                             (persistedEntity.hasAttribute(URI) ? persistedEntity.getAttribute(URI) : "<unknown>") +
                                             " permissions are " +
                                             persistedEntity.getAttribute(PERMISSIONS)
            );
        }
    }

    @Override
    @Nullable
    public LSDTransferEntity changePermissionNoTx(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LiquidURI uri,
                                                  final LiquidPermissionChangeType change, final LiquidRequestDetailLevel detail,
                                                  final boolean internal) throws Exception {
        begin();
        try {
            LSDPersistedEntity startPersistedEntityImpl = findByURI(uri);
            if (startPersistedEntityImpl == null) {
                throw new EntityNotFoundException("Could not find entity %s", uri);
            }
            startPersistedEntityImpl = changeNodePermissionNoTx(startPersistedEntityImpl, editor, change);
            log.debug("Changed permission of {0} to {1}", uri, change);
            return startPersistedEntityImpl.convertNodeToLSD(detail, internal);
        } finally {
            end();
        }
    }

    @Override
    @Nonnull
    public LSDPersistedEntity changeNodePermissionNoTx(@Nonnull final LSDPersistedEntity entity,
                                                       @Nonnull final LiquidSessionIdentifier editor,
                                                       final LiquidPermissionChangeType change) throws Exception {
        entity.assertLatestVersion();
        LSDPersistedEntity startPersistedEntityImpl = entity;
        final Traverser traverser = startPersistedEntityImpl.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH,
                                                                      ReturnableEvaluator.ALL, FountainRelationships.CHILD,
                                                                      Direction.OUTGOING
                                                                     );
        final Collection<Node> allNodes = traverser.getAllNodes();
        log.debug("Changing permission on {0} nodes to {1} .", allNodes.size(), change);
        for (final Node origNode : allNodes) {
            final LSDPersistedEntity clonePersistedEntityImpl = cloneNodeForNewVersion(editor, new FountainEntityImpl(origNode),
                                                                                       false
                                                                                      );
            final String permissions = clonePersistedEntityImpl.getAttribute(PERMISSIONS);
            final LiquidPermissionSet newPermissions = LiquidPermissionSet.createPermissionSet(permissions)
                                                                          .convertChangeRequestIntoPermissionSet(change);
            clonePersistedEntityImpl.setAttribute(PERMISSIONS, newPermissions.toString());
            //So we have cloned the start persistedEntityImpl and need to use the cloned version to build our result.
            if (origNode.getId() == startPersistedEntityImpl.getPersistenceId()) {
                startPersistedEntityImpl = clonePersistedEntityImpl;
            }
        }
        return startPersistedEntityImpl;
    }

    @Override
    @Nonnull
    public LSDPersistedEntity createSystemPool(final String pool) throws InterruptedException {
        final LSDPersistedEntity systemPool = createNode();
        systemPool.setValue(ROOT_POOL_PROPERTY, "true");
        systemPool.setIDIfNotSetOnNode();
        systemPool.setAttribute(URI, pool);
        systemPool.setAttribute(TYPE, LSDDictionaryTypes.POOL2D.getValue());
        systemPool.setAttribute(PERMISSIONS, DEFAULT_POOL_PERMISSIONS);
        systemPool.setAttribute(NAME, "");
        indexBy(systemPool, ID, ID, true);
        indexBy(systemPool, URI, URI, true);
        return systemPool;
    }

    @Override
    @Nonnull
    public FountainEntityImpl createNode() {
        final FountainEntityImpl fountainEntityImpl = new FountainEntityImpl(neo.createNode());
        fountainEntityImpl.setAttribute(VERSION, "1");
        fountainEntityImpl.publishTimestamp();
        fountainEntityImpl.timestamp();
        return fountainEntityImpl;
    }

    @Override
    public void indexBy(@Nonnull final LSDPersistedEntity persistedEntity, @Nonnull final LSDAttribute key,
                        @Nonnull final LSDAttribute luceneIndex, final boolean unique) throws InterruptedException {
        final String value = persistedEntity.getAttribute(key).toLowerCase();
        log.debug(
                "Indexing persistedEntityImpl " + persistedEntity.getPersistenceId() + " with key " + key + " with value " + value
                 );
        final IndexHits<Node> hits = indexService.get(luceneIndex.getKeyName(), value);
        if (hits.size() > 0 && unique) {
            for (final Node hit : hits) {
                if (!new FountainEntityImpl(hit).isDeleted()) {
                    throw new DuplicateEntityException(
                            "Attempted to index an entity with the %s of %s, there is already an entity with that %s value.", key,
                            value, key
                    );
                }
            }
        }
        indexService.add(persistedEntity.getNeoNode(), luceneIndex.getKeyName(), value);
    }

    @Override
    @Nullable
    public LSDTransferEntity deleteEntityTx(@Nonnull final LiquidURI uri, final boolean children, final boolean internal,
                                            final LiquidRequestDetailLevel detail) throws InterruptedException {
        final LSDPersistedEntity persistedEntity = findByURI(uri);
        if (persistedEntity == null) {
            throw new EntityNotFoundException("Could not find persistedEntityImpl identified by %s.", uri);
        }

        return deleteNodeTx(children, internal, persistedEntity, detail);
    }

    @Override
    @Nullable
    public FountainEntityImpl findByURI(@Nonnull final LiquidURI uri) throws InterruptedException {
        return findByURI(uri, false);
    }

    @Override
    @Nullable
    public LSDTransferEntity deleteNodeTx(final boolean children, final boolean internal,
                                          @Nonnull final LSDPersistedEntity persistedEntity, final LiquidRequestDetailLevel detail)
            throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                delete(persistedEntity);
                if (children) {
                    final Traverser traverser = persistedEntity.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH,
                                                                         new ReturnableEvaluator() {
                                                                             public boolean isReturnableNode(
                                                                                     @Nonnull final TraversalPosition currentPos) {
                                                                                 return new FountainEntityImpl(
                                                                                         currentPos.currentNode()
                                                                                 ).hasAttribute(URI);
                                                                             }
                                                                         }, FountainRelationships.CHILD, Direction.OUTGOING,
                                                                         FountainRelationships.VIEW, Direction.OUTGOING
                                                                        );
                    for (final Node childNode : traverser) {
                        delete(new FountainEntityImpl(childNode));
                    }
                }
                transaction.success();
                return persistedEntity.convertNodeToLSD(detail, internal);
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
    public void delete(@Nonnull final LSDPersistedEntity persistedEntity) {
        //the deleted attribute is special, it can't actually be set on the Fountain FountainEntityImpl, it must be set directly on the underlying Neo persistedEntityImpl.
        persistedEntity.getNeoNode().setProperty(DELETED.getKeyName(), "true");
        indexService.remove(persistedEntity.getNeoNode(), persistedEntity.getAttribute(LSDAttribute.URI));
        fulltextIndexService.remove(persistedEntity.getNeoNode(), FREE_TEXT_SEARCH_INDEX_KEY);
    }

    @Override
    @Nullable
    public LSDTransferEntity deleteEntityTx(@Nonnull final LiquidUUID objectId, final boolean children, final boolean internal,
                                            final LiquidRequestDetailLevel detail) throws InterruptedException {
        final LSDPersistedEntity persistedEntity = findByUUID(objectId);
        if (persistedEntity == null) {
            throw new EntityNotFoundException("Could not find persistedEntityImpl identified by %s.", objectId);
        }

        return deleteNodeTx(children, internal, persistedEntity, detail);
    }

    @Override
    @Nonnull
    public FountainEntityImpl findByUUID(@Nonnull final LiquidUUID id) throws InterruptedException {
        begin();
        try {
            final String idString = id.toString().toLowerCase();
            final Node neoNode = indexService.get(ID.getKeyName(), idString).getSingle();
            if (neoNode == null) {
                throw new EntityNotFoundException("Could not find persistedEntityImpl identified by %s.", idString);
            }
            final FountainEntityImpl fountainEntityImpl = new FountainEntityImpl(neoNode);
            if (fountainEntityImpl.isDeleted()) {
                throw new DeletedEntityException("Attempted to retrieve a deleted persistedEntityImpl.");
            }
            return fountainEntityImpl;
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
    public Transaction beginTx() {
        return neo.beginTx();
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

    @Override
    @Nullable
    public FountainEntityImpl findByURI(@Nonnull final LiquidURI uri, final boolean mustMatch) throws InterruptedException {
        begin();
        try {
            final IndexHits<Node> nodes = indexService.get(URI.getKeyName(), uri.asString());
            FountainEntityImpl matchingFountainEntityImpl = null;
            int nodeCount = 0;
            for (final Node node : nodes) {
                if (!new FountainEntityImpl(node).isDeleted()) {
                    nodeCount++;
                    matchingFountainEntityImpl = new FountainEntityImpl(node);
                }
            }
            if (nodeCount > 1) {
                log.error("Duplicate entity found for URI %s.", uri.asString());
            }
            if (matchingFountainEntityImpl == null && mustMatch) {
                throw new EntityNotFoundException("Could not locate " + uri);
            }
            return matchingFountainEntityImpl;
        } finally {
            end();
        }
    }

    @Override
    public LSDTransferEntity freeTextSearch(final String searchText, final LiquidRequestDetailLevel detail, final boolean internal)
            throws InterruptedException {

        final IndexHits<Node> results = fulltextIndexService.query(FREE_TEXT_SEARCH_INDEX_KEY, searchText);
        final LSDTransferEntity searchResultEntity = LSDSimpleEntity.createNewTransferEntity(LSDDictionaryTypes.SEARCH_RESULTS,
                                                                                             UUIDFactory.randomUUID()
                                                                                            );
        final List<LSDBaseEntity> resultEntities = new ArrayList<LSDBaseEntity>();
        final List<String> dedupUrls = new ArrayList<String>();
        for (final Node r : results) {
            final LSDPersistedEntity result = new FountainEntityImpl(r);
            if (!dedupUrls.contains(result.getAttribute(LSDAttribute.URI))) {
                resultEntities.add(result.convertNodeToLSD(detail, internal));
            }
            dedupUrls.add(result.getAttribute(LSDAttribute.URI));
        }
        searchResultEntity.addSubEntities(LSDAttribute.CHILD, resultEntities);
        return searchResultEntity;
    }

    @Override
    @Nullable
    public LSDTransferEntity getEntityByUUID(@Nonnull final LiquidUUID id, final boolean internal,
                                             final LiquidRequestDetailLevel detail) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                final LSDPersistedEntity persistedEntity = findByUUID(id);
                return persistedEntity.convertNodeToLSD(detail, internal);
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
    public void putProfileInformationIntoAlias(@Nonnull final LSDPersistedEntity alias) {
        try {
            final LiquidURI poolURI = new LiquidURI("pool:///people/" + alias.getAttribute(NAME) + "/profile");
            final LSDPersistedEntity pool = findByURI(poolURI);
            if (pool == null) {
                throw new EntityNotFoundException("Could not locate pool %s", poolURI.toString());
            }
            if (pool.hasAttribute(LSDAttribute.IMAGE_URL)) {
                alias.setAttribute(LSDAttribute.IMAGE_URL, pool.getAttribute(LSDAttribute.IMAGE_URL));
            }
            if (pool.hasAttribute(LSDAttribute.IMAGE_WIDTH)) {
                alias.setAttribute(LSDAttribute.IMAGE_WIDTH, pool.getAttribute(LSDAttribute.IMAGE_WIDTH));
            }
            if (pool.hasAttribute(LSDAttribute.IMAGE_HEIGHT)) {
                alias.setAttribute(LSDAttribute.IMAGE_HEIGHT, pool.getAttribute(LSDAttribute.IMAGE_HEIGHT));
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public void recalculateURI(@Nonnull final LSDPersistedEntity childPersistedEntity) throws InterruptedException {
        final LSDPersistedEntity parentPersistedEntity = childPersistedEntity.parentNode();
        if (childPersistedEntity.getAttribute(LSDAttribute.TYPE).startsWith(LSDDictionaryTypes.POOL.toString())) {
            childPersistedEntity.setAttribute(URI, parentPersistedEntity.getAttribute(URI) +
                                                   '/' +
                                                   childPersistedEntity.getAttribute(NAME)
                                             );
        }
        else {
            if (childPersistedEntity.getAttribute(URI).startsWith("pool://")) {
                childPersistedEntity.setAttribute(URI, parentPersistedEntity.getAttribute(URI) +
                                                       '#' +
                                                       childPersistedEntity.getAttribute(NAME)
                                                 );
            }
        }
        reindex(childPersistedEntity, URI, URI);
    }

    @Override
    public void reindex(@Nonnull final LSDPersistedEntity persistedEntity, @Nonnull final LSDAttribute key,
                        @Nonnull final LSDAttribute luceneIndex) {
        indexService.remove(persistedEntity.getNeoNode(), luceneIndex.getKeyName());
        final String value = persistedEntity.getAttribute(key).toLowerCase();
        log.debug(
                "Indexing persistedEntityImpl " + persistedEntity.getPersistenceId() + " with key " + key + " with value " + value
                 );
        final IndexHits<Node> hits = indexService.get(luceneIndex.getKeyName(), value);
        for (final Node hit : hits) {
            indexService.remove(hit, luceneIndex.getKeyName(), value);
        }
        indexService.add(persistedEntity.getNeoNode(), luceneIndex.getKeyName(), value);
    }

    @Override
    public void start() throws Exception {
        try {
            super.start();


            final HashMap<String, String> params = new HashMap<String, String>();
//        params.put("allow_store_upgrade", "true");
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
            log.info("Lucene for Neo started.");
            indexService = neo.index().forNodes(NODE_INDEX_NAME, MapUtil.stringMap("type", "exact"));
            fulltextIndexService = neo.index().forNodes(FountainNeoImpl.FREE_TEXT_SEARCH_INDEX_KEY,
                                                        MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "type", "fulltext")
                                                       );

            final Transaction transaction = neo.beginTx();
            try {
                setRootPool(findByURI(new LiquidURI("pool:///")));


                setPeoplePool(findByURI(new LiquidURI("pool:///people")));
                transaction.success();
                this.started= true;
            } catch (RuntimeException e) {
                e.printStackTrace();
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } catch (Exception e) {
            log.error(e);
            throw new Error("Catastrophic failure, could not start FountainNeo", e);
        }
    }

    @Override
    public void backup() throws Exception {
        final String dir = CommonConstants.NEO_BACKUP_DIR + '/' + System.currentTimeMillis();
        new File(dir).mkdirs();
        new FountainNeoExporter(neo).export(dir);
    }

    @Override
    public void stop() {
        super.stop();
        neo.shutdown();
//        wrappingNeoServerBootstrapper.stop();
        log.info("Shutdown Neo.");
        log.info("Shutdown Fountain Neo service.");
        this.started= false;
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public void unindex(@Nonnull final LSDPersistedEntity persistedEntity, @Nonnull final LSDAttribute key,
                        final String luceneIndex) {
        final String value = persistedEntity.getAttribute(key).toLowerCase();
        log.debug("Un-indexing persistedEntityImpl " +
                  persistedEntity.getPersistenceId() +
                  " with key " +
                  key +
                  " with value " +
                  value
                 );
        indexService.remove(persistedEntity.getNeoNode(), luceneIndex, value);
    }

    @Override
    @Nullable
    public LSDTransferEntity updateEntityByURITx(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LiquidURI uri,
                                                 @Nonnull final LSDTransferEntity entity, final boolean internal,
                                                 final LiquidRequestDetailLevel detail, @Nullable final Runnable onRenameAction)
            throws Exception {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                final FountainEntityImpl origFountainEntityImpl = findByURI(uri);
                if (origFountainEntityImpl == null) {
                    throw new EntityNotFoundException("Could not find entity %s", uri);
                }
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
    @Nullable
    public LSDTransferEntity updateEntityByUUIDTx(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LiquidUUID id,
                                                  @Nonnull final LSDTransferEntity entity, final boolean internal,
                                                  final LiquidRequestDetailLevel detail, @Nullable final Runnable onRenameAction)
            throws Exception {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                if (!entity.getUUID().equals(id)) {
                    throw new CannotChangeIdException("Tried to change the id of %s to %s", id.toString(),
                                                      entity.getUUID().toString()
                    );
                }
                final FountainEntityImpl origFountainEntityImpl = findByUUID(id);
                if (origFountainEntityImpl == null) {
                    throw new EntityNotFoundException("Could not find entity %s", id);
                }

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

    @Nullable
    public LSDTransferEntity updateNodeNoTx(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LSDTransferEntity entity,
                                            final boolean internal, final LiquidRequestDetailLevel detail,
                                            @Nonnull final Transaction transaction,
                                            @Nonnull final FountainEntityImpl origFountainEntityImpl, final Runnable onRenameAction)
            throws Exception {
        final LSDPersistedEntity persistedEntity = updateNodeAndReturnNodeNoTx(editor, origFountainEntityImpl, entity,
                                                                               onRenameAction
                                                                              );
        final LSDTransferEntity newObject = persistedEntity.convertNodeToLSD(detail, internal);
        transaction.success();
        return newObject;
    }

    @Override
    public LSDPersistedEntity updateNodeAndReturnNodeNoTx(@Nonnull final LiquidSessionIdentifier editor,
                                                          @Nonnull final LSDPersistedEntity origPersistedEntityImpl,
                                                          @Nonnull final LSDTransferEntity entity, final Runnable onRenameAction)
            throws Exception {
        return doInBeginBlock(new Callable<LSDPersistedEntity>() {
            @Nonnull
            @Override
            public LSDPersistedEntity call() throws Exception {
                //Timestampcheck has been removed as we use versioning now.
//        if (persistedEntityImpl.hasProperty(LSDAttribute.UPDATED.getKeyName())) {
//            String entityValue = lsdEntity.getAttribute(LSDAttribute.UPDATED);
//            String nodeValue = (String) persistedEntityImpl.getProperty(LSDAttribute.UPDATED.getKeyName());
//            long entityMilli = Long.parseLong(entityValue);
//            long nodeMilli = Long.parseLong(nodeValue);
//            if (entityMilli < nodeMilli) {
//                throw new StaleUpdateException("Stale update attempted date on the stored entity is %s the supplied entity was %s (difference in milliseconds was: %s). ", new Date(nodeMilli), new Date(entityMilli), nodeMilli - entityMilli);
//            }
//        }
                final LSDPersistedEntity persistedEntity = cloneNodeForNewVersion(editor, origPersistedEntityImpl, false);
                persistedEntity.mergeProperties(entity, true, false, onRenameAction);
                freeTextIndexNoTx(persistedEntity);
                return persistedEntity;
            }
        }
                             );
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
    @Nonnull
    public LSDPersistedEntity cloneNodeForNewVersion(@Nonnull final LiquidSessionIdentifier editor,
                                                     @Nonnull final LSDPersistedEntity persistedEntityImpl, final boolean fork)
            throws InterruptedException {
        fulltextIndexService.remove(persistedEntityImpl.getNeoNode(), FREE_TEXT_SEARCH_INDEX_KEY);
        final LSDPersistedEntity clone = createNode();
        final Iterable<String> keys = persistedEntityImpl.getPropertyKeys();
        for (final String key : keys) {
            clone.setValue(key, persistedEntityImpl.getValue(key));
        }
        clone.setAttribute(ID, UUIDFactory.randomUUID().toString());
        indexBy(clone, ID, ID, true);
        final String versionString = persistedEntityImpl.getAttribute(VERSION);

        migrateParentNode(persistedEntityImpl, clone, fork);

        if (fork) {
            Long lastFork = null;
            if (persistedEntityImpl.hasAttribute(LSDAttribute.LAST_FORK_VERSION)) {
                lastFork = persistedEntityImpl.getLongAttribute(LSDAttribute.LAST_FORK_VERSION);
            }
            if (lastFork == null) {
                persistedEntityImpl.setAttribute(LSDAttribute.LAST_FORK_VERSION, (long) 1);
                lastFork = 1L;
            }
            else {
                persistedEntityImpl.setAttribute(LSDAttribute.LAST_FORK_VERSION, ++lastFork);
            }
            clone.setAttribute(VERSION, versionString + "." + lastFork + ".1");
        }
        else {
            reindex(clone, URI, URI);
            if (persistedEntityImpl.hasRelationship(FountainRelationships.FORK_PARENT, Direction.OUTGOING)) {
                clone.createRelationshipTo(persistedEntityImpl.getSingleRelationship(FountainRelationships.FORK_PARENT,
                                                                                     Direction.OUTGOING
                                                                                    ).getOtherNode(persistedEntityImpl),
                                           FountainRelationships.FORK_PARENT
                                          );
            }
            else {
                clone.createRelationshipTo(persistedEntityImpl, FountainRelationships.FORK_PARENT);
            }
            final int lastDot = versionString.lastIndexOf('.');
            final String newVersion;
            if (lastDot < 0) {
                newVersion = String.valueOf(Long.parseLong(versionString) + 1);
            }
            else {
                newVersion = versionString.substring(0, lastDot) + "." + (Long.parseLong(versionString.substring(lastDot + 1)) + 1);
            }
            clone.setAttribute(VERSION, newVersion);
        }

        final FountainEntityImpl editorFountainEntityImpl = findByURI(editor.getAliasURL());
        //TODO: remove this restriction in later version of Neo4J (i.e. when it supports self references)
        if (!editorFountainEntityImpl.equals(clone)) {
            clone.createRelationshipTo(editorFountainEntityImpl, FountainRelationships.EDITOR);
        }

        final Iterable<FountainRelationship> relationships = persistedEntityImpl.getRelationships(
                FountainRelationships.FOLLOW_CONTENT, FountainRelationships.FOLLOW_ALIAS, FountainRelationships.AUTHOR,
                FountainRelationships.CREATOR, FountainRelationships.OWNER, FountainRelationships.VIEW, FountainRelationships.ALIAS
                                                                                                 );
        for (final FountainRelationship relationship : relationships) {
            if (relationship.getStartNode().equals(persistedEntityImpl)) {
                clone.createRelationshipTo(relationship.getEndNode(), relationship.getType());
                if (relationship.getType() == FountainRelationships.AUTHOR ||
                    relationship.getType() == FountainRelationships.CREATOR ||
                    relationship.getType() == FountainRelationships.OWNER ||
                    relationship.getType() == FountainRelationships.VIEW) {
                    //preserve these relationships
                }
                else {
                    relationship.delete();
                }
            }
            if (relationship.getEndNode().equals(persistedEntityImpl)) {
                relationship.getStartNode().createRelationshipTo(clone, relationship.getType());
                relationship.delete();
            }
        }
        clone.createRelationshipTo(persistedEntityImpl, FountainRelationships.VERSION_PARENT);
        clone.timestamp();
        return clone;
    }

    @Override
    public void migrateParentNode(@Nonnull final LSDPersistedEntity persistedEntity, @Nonnull final LSDPersistedEntity clone,
                                  final boolean fork) {
        persistedEntity.assertLatestVersion();
        final Iterable<FountainRelationship> relationships = persistedEntity.getRelationships(FountainRelationships.CHILD,
                                                                                              Direction.OUTGOING
                                                                                             );
        for (final FountainRelationship relationship : relationships) {
            final LSDPersistedEntity childPersistedEntityImpl = relationship.getOtherNode(persistedEntity);
            clone.createRelationshipTo(childPersistedEntityImpl, FountainRelationships.CHILD);
            relationship.delete();
        }
        if (!fork) {
            final FountainRelationship parentRel = persistedEntity.getSingleRelationship(FountainRelationships.CHILD,
                                                                                         Direction.INCOMING
                                                                                        );
            if (parentRel != null) {
                final LSDPersistedEntity parentPersistedEntity = parentRel.getOtherNode(persistedEntity);
                parentPersistedEntity.timestamp();
                parentPersistedEntity.createRelationshipTo(clone, FountainRelationships.CHILD);
                parentRel.delete();
            }
        }
    }

//    private FountainEntityImpl recalculateCentreImage(final FountainEntityImpl pool, final FountainEntityImpl object /*Do not delete this parameter */) throws Exception {
//        final double[] distance = {Double.MAX_VALUE};
//        forEachChild(pool, new NodeCallback() {
//            public void call(FountainEntityImpl child) throws InterruptedException {
//                if (!isDeleted(child) && !child.getProperty(TYPE).toString().startsWith("Collection.Pool") && child.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
//                    FountainEntityImpl viewNode = child.getSingleRelationship(VIEW, Direction.OUTGOING).getOtherNode(child);
//                    double thisdistance = Double.valueOf(viewNode.getProperty(VIEW_RADIUS).toString());
//                    if (thisdistance < distance[0]) {
//                        distance[0] = thisdistance;
//                        if (child.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
//                            pool.setProperty(LSDAttribute.IMAGE_URL.getKeyName(), child.getProperty(LSDAttribute.IMAGE_URL.getKeyName()));
//                        }
//                        if (child.hasProperty(LSDAttribute.IMAGE_WIDTH.getKeyName())) {
//                            pool.setProperty(LSDAttribute.IMAGE_WIDTH.getKeyName(), child.getProperty(LSDAttribute.IMAGE_WIDTH.getKeyName()));
//                        }
//                        if (child.hasProperty(LSDAttribute.IMAGE_HEIGHT.getKeyName())) {
//                            pool.setProperty(LSDAttribute.IMAGE_HEIGHT.getKeyName(), child.getProperty(LSDAttribute.IMAGE_HEIGHT.getKeyName()));
//                        }
//                    }
//                }
//            }
//        }, max);
//        pool.setProperty(LSDAttribute.INTERNAL_MIN_IMAGE_RADIUS.getKeyName(), String.valueOf(distance[0]));
//        return pool;
//    }


    //TODO get comments for historical versions!


    @Override
    public void updateSessionTx(@Nonnull final LiquidUUID session) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                final LSDPersistedEntity persistedEntity = findByUUID(session);
                persistedEntity.timestamp();
                persistedEntity.setAttribute(LSDAttribute.ACTIVE, true);
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

    @Override
    @Nullable
    public LSDTransferEntity updateUnversionedEntityByUUIDTx(@Nonnull final LiquidUUID id, @Nonnull final LSDTransferEntity entity,
                                                             final boolean internal, final LiquidRequestDetailLevel detail,
                                                             @Nullable final Runnable onRenameAction) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                final LSDPersistedEntity persistedEntity = findByUUID(id);
                //Timestampcheck has been removed as we use versioning now.
//        if (persistedEntityImpl.hasProperty(LSDAttribute.UPDATED.getKeyName())) {
//            String entityValue = lsdEntity.getAttribute(LSDAttribute.UPDATED);
//            String nodeValue = (String) persistedEntityImpl.getProperty(LSDAttribute.UPDATED.getKeyName());
//            long entityMilli = Long.parseLong(entityValue);
//            long nodeMilli = Long.parseLong(nodeValue);
//            if (entityMilli < nodeMilli) {
//                throw new StaleUpdateException("Stale update attempted date on the stored entity is %s the supplied entity was %s (difference in milliseconds was: %s). ", new Date(nodeMilli), new Date(entityMilli), nodeMilli - entityMilli);
//            }
//        }
                if (!entity.getUUID().equals(id)) {
                    throw new CannotChangeIdException("Tried to change the id of %s to %s", id.toString(),
                                                      entity.getUUID().toString()
                    );
                }
                final LSDTransferEntity newObject = persistedEntity.mergeProperties(entity, true, false, onRenameAction)
                                                                   .convertNodeToLSD(detail, internal);
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

    @Override
    public void freeTextIndexNoTx(@Nonnull final LSDPersistedEntity persistedEntity) throws InterruptedException {
        final LSDBaseEntity entity = persistedEntity.convertNodeToLSD(LiquidRequestDetailLevel.FREE_TEXT_SEARCH_INDEX, true);
        final String freeText = entity.asFreeText();
        fulltextIndexService.add(persistedEntity.getNeoNode(), FREE_TEXT_SEARCH_INDEX_KEY, freeText);
    }

    @Override
    public Index<Node> getIndexService() {
        return indexService;
    }

    @Override
    public LSDPersistedEntity getPeoplePool() {
        return peoplePool;
    }

    @Override
    public void setPeoplePool(final LSDPersistedEntity peoplePool) {
        this.peoplePool = peoplePool;
    }

    @Override
    public LSDPersistedEntity getRootPool() {
        return rootPool;
    }

    @Override
    public void setRootPool(final LSDPersistedEntity rootPool) {
        this.rootPool = rootPool;
    }
}
