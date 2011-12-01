package cazcade.fountain.datastore.impl;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.common.service.AbstractServiceStateMachine;
import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.Relationship;
import cazcade.fountain.datastore.api.*;
import cazcade.fountain.datastore.impl.io.FountainNeoExporter;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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

public final class FountainNeo extends AbstractServiceStateMachine {
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
    private static final Logger log = Logger.getLogger(FountainNeo.class);
    @Nonnull
    private static final String ROOT_POOL_PROPERTY = "_root_pool";
    @Nonnull
    private static final String TRUE = "true";
    @Nonnull
    private static final String FALSE = "false";
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

    //    private final static Logger log = Logger.getLogger(FountainNeo.class);
    private EmbeddedGraphDatabase neo;
    private Index<org.neo4j.graphdb.Node> indexService;
    private Index<org.neo4j.graphdb.Node> fulltextIndexService;


    private Node rootPool;
    private Node peoplePool;

    /**
     * How old an inactive session can be before scheduled for deletion.
     */
    public static final int SESSION_EXPIRES_MILLI = 1000 * 3600 * 24 * 7;

    /**
     * How long in milliseconds before we decide a user's session is now inactive.
     * Currently this is based on the heartbeat (i.e. notification timeout).
     */
    public static final int SESSION_INACTIVE_MILLI = 5 * 60 * 1000;

    private final ScheduledExecutorService backupScheduler =
            Executors.newScheduledThreadPool(1);


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

    public FountainNeo() throws Exception {
        super();
    }

    public Node getRootPool() {
        return rootPool;
    }

    public Transaction beginTx() {
        return neo.beginTx();
    }


    public void recalculateURI(@Nonnull final Node childNode) throws InterruptedException {
        final Node parentNode = childNode.parentNode();
        if (childNode.getProperty(LSDAttribute.TYPE).startsWith(LSDDictionaryTypes.POOL.toString())) {
            childNode.setProperty(URI, parentNode.getProperty(URI) + '/' + childNode.getProperty(NAME));
        } else {
            if (childNode.getProperty(URI).startsWith("pool://")) {
                childNode.setProperty(URI, parentNode.getProperty(URI) + '#' + childNode.getProperty(NAME));
            }
        }
        reindex(childNode, URI, URI);
    }

    void indexBy(@Nonnull final Node node, @Nonnull final LSDAttribute key, @Nonnull final LSDAttribute luceneIndex, final boolean unique) throws InterruptedException {
        final String value = node.getAttribute(key).toLowerCase();
        log.debug("Indexing node " + node.getNeoId() + " with key " + key + " with value " + value);
        final IndexHits<org.neo4j.graphdb.Node> hits = indexService.get(luceneIndex.getKeyName(), value);
        if (hits.size() > 0 && unique) {
            for (final org.neo4j.graphdb.Node hit : hits) {
                if (!new Node(hit).isDeleted()) {
                    throw new DuplicateEntityException("Attempted to index an entity with the %s of %s, there is already an entity with that %s value.", key, value, key);
                }
            }
        }
        indexService.add(node.getNeoNode(), luceneIndex.getKeyName(), value);
    }

    void reindex(@Nonnull final Node node, @Nonnull final LSDAttribute key, @Nonnull final LSDAttribute luceneIndex) {
        indexService.remove(node.getNeoNode(), luceneIndex.getKeyName());
        final String value = node.getAttribute(key).toLowerCase();
        log.debug("Indexing node " + node.getNeoId() + " with key " + key + " with value " + value);
        final IndexHits<org.neo4j.graphdb.Node> hits = indexService.get(luceneIndex.getKeyName(), value);
        for (final org.neo4j.graphdb.Node hit : hits) {
            indexService.remove(hit, luceneIndex.getKeyName(), value);
        }
        indexService.add(node.getNeoNode(), luceneIndex.getKeyName(), value);
    }

    private void unindex(@Nonnull final Node node, @Nonnull final LSDAttribute key, final String luceneIndex) {
        final String value = node.getAttribute(key).toLowerCase();
        log.debug("Un-indexing node " + node.getNeoId() + " with key " + key + " with value " + value);
        indexService.remove(node.getNeoNode(), luceneIndex, value);
    }


    @Nullable
    public Node findByURI(@Nonnull final LiquidURI uri) throws InterruptedException {
        return findByURI(uri, false);
    }

    @Nullable
    public Node findByURI(@Nonnull final LiquidURI uri, final boolean mustMatch) throws InterruptedException {
        begin();
        try {
            final IndexHits<org.neo4j.graphdb.Node> nodes = indexService.get(URI.getKeyName(), uri.asString());
            Node matchingNode = null;
            int nodeCount = 0;
            for (final org.neo4j.graphdb.Node node : nodes) {
                if (!new Node(node).isDeleted()) {
                    nodeCount++;
                    matchingNode = new Node(node);
                }
            }
            if (nodeCount > 1) {
                log.error("Duplicate entity found for URI %s.", uri.asString());
            }
            if (matchingNode == null && mustMatch) {
                throw new EntityNotFoundException("Could not locate " + uri);
            }
            return matchingNode;
        } finally {
            end();
        }
    }


    @Nonnull
    public Node createNode() {
        final Node node = new Node(neo.createNode());
        node.setProperty(VERSION, "1");
        return node;
    }


    @Nullable
    public LSDEntity deleteEntityTx(@Nonnull final LiquidURI uri, final boolean children, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {
        final Node node = findByURI(uri);
        if (node == null) {
            throw new EntityNotFoundException("Could not find node identified by %s.", uri);
        }

        return deleteNodeTx(children, internal, node, detail);
    }

    @Nullable
    private LSDEntity deleteNodeTx(final boolean children, final boolean internal, @Nonnull final Node node, final LiquidRequestDetailLevel detail) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                delete(node);
                if (children) {
                    final Traverser traverser = node.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
                        public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                            return new Node(currentPos.currentNode()).hasAttribute(URI);
                        }
                    }, FountainRelationships.CHILD, Direction.OUTGOING, FountainRelationships.VIEW, Direction.OUTGOING);
                    for (final org.neo4j.graphdb.Node childNode : traverser) {
                        delete(new Node(childNode));
                    }
                }
                transaction.success();
                return node.convertNodeToLSD(detail, internal);
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

    @Nullable
    public LSDEntity deleteEntityTx(@Nonnull final LiquidUUID objectId, final boolean children, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {
        final Node node = findByUUID(objectId);
        if (node == null) {
            throw new EntityNotFoundException("Could not find node identified by %s.", objectId);
        }

        return deleteNodeTx(children, internal, node, detail);
    }

    @Nonnull
    public Node findByUUID(@Nonnull final LiquidUUID id) throws InterruptedException {
        begin();
        try {
            final String idString = id.toString().toLowerCase();
            final org.neo4j.graphdb.Node neoNode = indexService.get(ID.getKeyName(), idString).getSingle();
            if (neoNode == null) {
                throw new EntityNotFoundException("Could not find node identified by %s.", idString);
            }
            final Node node = new Node(neoNode);
            if (node.isDeleted()) {
                throw new DeletedEntityException("Attempted to retrieve a deleted node.");
            }
            return node;
        } finally {
            end();
        }
    }

    void delete(@Nonnull final Node node) {
        //the deleted attribute is special, it can't actually be set on the Fountain Node, it must be set directly on the underlying Neo node.
        node.getNeoNode().setProperty(DELETED.getKeyName(), "true");
        indexService.remove(node.getNeoNode(), node.getAttribute(LSDAttribute.URI));
        fulltextIndexService.remove(node.getNeoNode(), FREE_TEXT_SEARCH_INDEX_KEY);
    }

    @Nullable
    public LSDEntity getEntityByUUID(@Nonnull final LiquidUUID id, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                final Node node = findByUUID(id);
                return node.convertNodeToLSD(detail, internal);
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

    public void assertAuthorized(@Nonnull final Node node, @Nonnull final LiquidSessionIdentifier identity, final LiquidPermission... permissions) throws InterruptedException {
        if (!node.isAuthorized(identity, permissions)) {
            throw new AuthorizationException("Session " + identity.toString() + " is not authorized to " + Arrays.toString(permissions) + " the resource " + (node.hasAttribute(URI) ? node.getAttribute(URI) : "<unknown>") + " permissions are " + node.getAttribute(PERMISSIONS));
        }
    }


    @Override
    public void start() throws Exception {
        try {
            super.start();


            final HashMap<String, String> params = new HashMap<String, String>();
//        params.put("allow_store_upgrade", "true");
            params.put("enable_remote_shell", "true");

            neo = new EmbeddedGraphDatabase(Constants.FOUNTAIN_NEO_STORE_DIR, params);
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
            fulltextIndexService = neo.index().forNodes(FountainNeo.FREE_TEXT_SEARCH_INDEX_KEY,
                    MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "type", "fulltext"));

            final Transaction transaction = neo.beginTx();
            try {
                setRootPool(findByURI(new LiquidURI("pool:///")));


                setPeoplePool(findByURI(new LiquidURI("pool:///people")));
                transaction.success();
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

    public void backup() throws Exception {
        final String dir = CommonConstants.NEO_BACKUP_DIR + "/" + System.currentTimeMillis();
        new File(dir).mkdirs();
        new FountainNeoExporter(neo).export(dir);
    }

    @Nonnull
    Node createSystemPool(final String pool) throws InterruptedException {
        final Node rootPool = createNode();
        rootPool.setValue(ROOT_POOL_PROPERTY, TRUE);
        rootPool.setIDIfNotSetOnNode();
        rootPool.setProperty(URI, pool);
        rootPool.setProperty(TYPE, LSDDictionaryTypes.POOL2D.getValue());
        rootPool.setProperty(PERMISSIONS, DEFAULT_POOL_PERMISSIONS);
        rootPool.setProperty(NAME, "");
        indexBy(rootPool, ID, ID, true);
        indexBy(rootPool, URI, URI, true);
        return rootPool;
    }

    public void stop() {
        super.stop();
        neo.shutdown();
//        wrappingNeoServerBootstrapper.stop();
        log.info("Shutdown Neo.");
        log.info("Shutdown Fountain Neo service.");
    }


    private void migrateParentNode(@Nonnull final Node node, @Nonnull final Node clone, final boolean fork) {
        node.assertLatestVersion();
        final Iterable<Relationship> relationships = node.getRelationships(FountainRelationships.CHILD, Direction.OUTGOING);
        for (final Relationship relationship : relationships) {
            final Node childNode = relationship.getOtherNode(node);
            clone.createRelationshipTo(childNode, FountainRelationships.CHILD);
            relationship.delete();
        }
        if (!fork) {
            final Relationship parentRel = node.getSingleRelationship(FountainRelationships.CHILD, Direction.INCOMING);
            if (parentRel != null) {
                final Node parentNode = parentRel.getOtherNode(node);
                parentNode.createRelationshipTo(clone, FountainRelationships.CHILD);
                parentRel.delete();
            }
        }
    }

    @Nonnull
    Node cloneNodeForNewVersion(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final Node node, final boolean fork) throws InterruptedException {
        fulltextIndexService.remove(node.getNeoNode(), FREE_TEXT_SEARCH_INDEX_KEY);
        final Node clone = createNode();
        final Iterable<String> keys = node.getPropertyKeys();
        for (final String key : keys) {
            clone.setValue(key, node.getValue(key));
        }
        clone.setProperty(ID, UUIDFactory.randomUUID().toString());
        indexBy(clone, ID, ID, true);
        final String versionString = node.getAttribute(VERSION);

        migrateParentNode(node, clone, fork);

        if (fork) {
            Long lastFork = null;
            if (node.hasAttribute(LSDAttribute.LAST_FORK_VERSION)) {
                lastFork = node.getLongAttribute(LSDAttribute.LAST_FORK_VERSION);
            }
            if (lastFork == null) {
                node.setAttribute(LSDAttribute.LAST_FORK_VERSION, (long) 1);
                lastFork = 1L;
            } else {
                node.setAttribute(LSDAttribute.LAST_FORK_VERSION, ++lastFork);
            }
            clone.setProperty(VERSION, versionString + "." + lastFork + ".1");

        } else {
            reindex(clone, URI, URI);
            if (node.hasRelationship(FountainRelationships.FORK_PARENT, Direction.OUTGOING)) {
                clone.createRelationshipTo(node.getSingleRelationship(FountainRelationships.FORK_PARENT, Direction.OUTGOING).getOtherNode(node), FountainRelationships.FORK_PARENT);
            } else {
                clone.createRelationshipTo(node, FountainRelationships.FORK_PARENT);
            }
            final int lastDot = versionString.lastIndexOf('.');
            final String newVersion;
            if (lastDot < 0) {
                newVersion = String.valueOf(Long.parseLong(versionString) + 1);
            } else {
                newVersion = versionString.substring(0, lastDot) + "." + (Long.parseLong(versionString.substring(lastDot + 1)) + 1);
            }
            clone.setProperty(VERSION, newVersion);
        }

        final Node editorNode = findByURI(editor.getAliasURL());
        //TODO: remove this restriction in later version of Neo4J (i.e. when it supports self references)
        if (!editorNode.equals(clone)) {
            clone.createRelationshipTo(editorNode, FountainRelationships.EDITOR);
        }

        final Iterable<Relationship> relationships = node.getRelationships(FountainRelationships.FOLLOW_CONTENT, FountainRelationships.FOLLOW_ALIAS, FountainRelationships.AUTHOR, FountainRelationships.CREATOR, FountainRelationships.OWNER, FountainRelationships.VIEW, FountainRelationships.ALIAS);
        for (final Relationship relationship : relationships) {
            if (relationship.getStartNode().equals(node)) {
                clone.createRelationshipTo(relationship.getEndNode(), relationship.getType());
                if (relationship.getType() == FountainRelationships.AUTHOR || relationship.getType() == FountainRelationships.CREATOR || relationship.getType() == FountainRelationships.OWNER || relationship.getType() == FountainRelationships.VIEW) {
                    //preserve these relationships
                } else {
                    relationship.delete();
                }
            }
            if (relationship.getEndNode().equals(node)) {
                relationship.getStartNode().createRelationshipTo(clone, relationship.getType());
                relationship.delete();
            }
        }
        clone.createRelationshipTo(node, FountainRelationships.VERSION_PARENT);
        clone.timestamp();
        return clone;
    }

    @Nullable
    public LSDEntity updateUnversionedEntityByUUIDTx(@Nonnull final LiquidUUID id, @Nonnull final LSDEntity entity, final boolean internal, final LiquidRequestDetailLevel detail, @Nullable final Runnable onRenameAction) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                final Node node = findByUUID(id);
                //Timestampcheck has been removed as we use versioning now.
//        if (node.hasProperty(LSDAttribute.UPDATED.getKeyName())) {
//            String entityValue = lsdEntity.getAttribute(LSDAttribute.UPDATED);
//            String nodeValue = (String) node.getProperty(LSDAttribute.UPDATED.getKeyName());
//            long entityMilli = Long.parseLong(entityValue);
//            long nodeMilli = Long.parseLong(nodeValue);
//            if (entityMilli < nodeMilli) {
//                throw new StaleUpdateException("Stale update attempted date on the stored entity is %s the supplied entity was %s (difference in milliseconds was: %s). ", new Date(nodeMilli), new Date(entityMilli), nodeMilli - entityMilli);
//            }
//        }
                if (!entity.getUUID().equals(id)) {
                    throw new CannotChangeIdException("Tried to change the id of %s to %s", id.toString(), entity.getUUID().toString());
                }
                final LSDEntity newObject = node.mergeProperties(entity, true, false, onRenameAction).convertNodeToLSD(detail, internal);
                freeTextIndexNoTx(node);

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


    @Nullable
    public LSDEntity updateEntityByUUIDTx(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LiquidUUID id, @Nonnull final LSDEntity entity, final boolean internal, final LiquidRequestDetailLevel detail, @Nullable final Runnable onRenameAction) throws Exception {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                if (!entity.getUUID().equals(id)) {
                    throw new CannotChangeIdException("Tried to change the id of %s to %s", id.toString(), entity.getUUID().toString());
                }
                final Node origNode = findByUUID(id);
                if (origNode == null) {
                    throw new EntityNotFoundException("Could not find entity %s", id);
                }

                return updateNodeNoTx(editor, entity, internal, detail, transaction, origNode, onRenameAction);
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
    private LSDEntity updateNodeNoTx(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LSDEntity entity, final boolean internal, final LiquidRequestDetailLevel detail, @Nonnull final Transaction transaction, @Nonnull final Node origNode, final Runnable onRenameAction) throws Exception {
        final Node node = updateNodeAndReturnNodeNoTx(editor, origNode, entity, onRenameAction);
        final LSDEntity newObject = node.convertNodeToLSD(detail, internal);
        transaction.success();
        return newObject;
    }

    public Node updateNodeAndReturnNodeNoTx(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final Node origNode, @Nonnull final LSDEntity entity, final Runnable onRenameAction) throws Exception {
        return doInBeginBlock(new Callable<Node>() {
            @Nonnull
            @Override
            public Node call() throws Exception {
                //Timestampcheck has been removed as we use versioning now.
//        if (node.hasProperty(LSDAttribute.UPDATED.getKeyName())) {
//            String entityValue = lsdEntity.getAttribute(LSDAttribute.UPDATED);
//            String nodeValue = (String) node.getProperty(LSDAttribute.UPDATED.getKeyName());
//            long entityMilli = Long.parseLong(entityValue);
//            long nodeMilli = Long.parseLong(nodeValue);
//            if (entityMilli < nodeMilli) {
//                throw new StaleUpdateException("Stale update attempted date on the stored entity is %s the supplied entity was %s (difference in milliseconds was: %s). ", new Date(nodeMilli), new Date(entityMilli), nodeMilli - entityMilli);
//            }
//        }
                final Node node = cloneNodeForNewVersion(editor, origNode, false);
                node.mergeProperties(entity, true, false, onRenameAction);
                freeTextIndexNoTx(node);
                return node;
            }
        });
    }

    @Nullable
    public LSDEntity updateEntityByURITx(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LiquidURI uri, @Nonnull final LSDEntity entity, final boolean internal, final LiquidRequestDetailLevel detail, @javax.annotation.Nullable final Runnable onRenameAction) throws Exception {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                final Node origNode = findByURI(uri);
                if (origNode == null) {
                    throw new EntityNotFoundException("Could not find entity %s", uri);
                }
                return updateNodeNoTx(editor, entity, internal, detail, transaction, origNode, onRenameAction);
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


    void putProfileInformationIntoAlias(@Nonnull final Node alias) {
        try {
            final LiquidURI poolURI = new LiquidURI("pool:///people/" + alias.getAttribute(NAME) + "/profile");
            final Node pool = findByURI(poolURI);
            if (pool == null) {
                throw new EntityNotFoundException("Could not locate pool %s", poolURI.toString());
            }
            if (pool.hasAttribute(LSDAttribute.IMAGE_URL)) {
                alias.setProperty(LSDAttribute.IMAGE_URL, pool.getAttribute(LSDAttribute.IMAGE_URL));
            }
            if (pool.hasAttribute(LSDAttribute.IMAGE_WIDTH)) {
                alias.setProperty(LSDAttribute.IMAGE_WIDTH, pool.getAttribute(LSDAttribute.IMAGE_WIDTH));
            }
            if (pool.hasAttribute(LSDAttribute.IMAGE_HEIGHT)) {
                alias.setProperty(LSDAttribute.IMAGE_HEIGHT, pool.getAttribute(LSDAttribute.IMAGE_HEIGHT));
            }
        } catch (Exception e) {
            log.error(e);
        }
    }


//    private Node recalculateCentreImage(final Node pool, final Node object /*Do not delete this parameter */) throws Exception {
//        final double[] distance = {Double.MAX_VALUE};
//        forEachChild(pool, new NodeCallback() {
//            public void call(Node child) throws InterruptedException {
//                if (!isDeleted(child) && !child.getProperty(TYPE).toString().startsWith("Collection.Pool") && child.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
//                    Node viewNode = child.getSingleRelationship(VIEW, Direction.OUTGOING).getOtherNode(child);
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


    public void updateSessionTx(@Nonnull final LiquidUUID session) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                final Node node = findByUUID(session);
                node.timestamp();
                node.setAttribute(LSDAttribute.ACTIVE, true);
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

    public void freeTextIndexNoTx(@Nonnull final Node node) throws InterruptedException {
        final LSDEntity entity = node.convertNodeToLSD(LiquidRequestDetailLevel.FREE_TEXT_SEARCH_INDEX, true);
        final String freeText = entity.asFreeText();
        fulltextIndexService.add(node.getNeoNode(), FREE_TEXT_SEARCH_INDEX_KEY, freeText);
    }

    public IndexHits<org.neo4j.graphdb.Node> freeTextSearch(final String searchText) {
        return fulltextIndexService.query(FREE_TEXT_SEARCH_INDEX_KEY, searchText);
    }

    void setRootPool(final Node rootPool) {
        this.rootPool = rootPool;
    }

    Node getPeoplePool() {
        return peoplePool;
    }

    void setPeoplePool(final Node peoplePool) {
        this.peoplePool = peoplePool;
    }

    @Nullable
    public LSDEntity changePermissionNoTx(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LiquidURI uri, final LiquidPermissionChangeType change, final LiquidRequestDetailLevel detail, final boolean internal) throws Exception {
        begin();
        try {
            Node startNode = findByURI(uri);
            if (startNode == null) {
                throw new EntityNotFoundException("Could not find entity %s", uri);
            }
            startNode = changeNodePermissionNoTx(startNode, editor, change);
            log.debug("Changed permission of {0} to {1}", uri, change);
            return startNode.convertNodeToLSD(detail, internal);
        } finally {
            end();
        }


    }

    @Nonnull
    public Node changeNodePermissionNoTx(@Nonnull final Node node, @Nonnull final LiquidSessionIdentifier editor, final LiquidPermissionChangeType change) throws Exception {
        node.assertLatestVersion();
        Node startNode = node;
        final Traverser traverser = startNode.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL, FountainRelationships.CHILD, Direction.OUTGOING);
        final Collection<org.neo4j.graphdb.Node> allNodes = traverser.getAllNodes();
        log.debug("Changing permission on {0} nodes to {1} .", allNodes.size(), change);
        for (final org.neo4j.graphdb.Node origNode : allNodes) {
            final Node cloneNode = cloneNodeForNewVersion(editor, new Node(origNode), false);
            final String permissions = cloneNode.getAttribute(PERMISSIONS);
            final LiquidPermissionSet newPermissions = LiquidPermissionSet.createPermissionSet(permissions).convertChangeRequestIntoPermissionSet(change);
            cloneNode.setProperty(PERMISSIONS, newPermissions.toString());
            //So we have cloned the start node and need to use the cloned version to build our result.
            if (origNode.getId() == startNode.getNeoId()) {
                startNode = cloneNode;
            }
        }
        return startNode;
    }


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

    public <T> T doInBeginBlock(@Nonnull final Callable<T> callable) throws Exception {
        begin();
        try {

            final T result = callable.call();

            return result;

        } finally {
            end();
        }

    }


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


    public Index<org.neo4j.graphdb.Node> getIndexService() {
        return indexService;
    }


}
