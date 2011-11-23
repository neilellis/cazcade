package cazcade.fountain.datastore.impl;

import cazcade.common.CommonConstants;
import cazcade.common.Logger;
import cazcade.fountain.common.service.AbstractServiceStateMachine;
import cazcade.fountain.datastore.api.*;
import cazcade.fountain.datastore.impl.io.FountainNeoExporter;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.impl.UUIDFactory;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static cazcade.fountain.datastore.impl.FountainRelationships.*;


/**
 * @author Neil Ellis
 *         <p/>
 *         The big TODO list starts here:
 *         <p/>
 *         todo : use assertAuthorized() on all public methods, this makes sure we don't allow slip ups and spot them early.
 */

public class FountainNeo extends AbstractServiceStateMachine {
    public static final String SYSTEM = "system";
    public static final LiquidSessionIdentifier SYSTEM_FAKE_SESSION = new LiquidSessionIdentifier(SYSTEM, null);
    public static final String DEFAULT_POOL_PERMISSIONS = String.valueOf(LiquidPermissionSet.getDefaultPermissions().toString());
    public static final String TYPE = LSDAttribute.TYPE.getKeyName();
    public static final String PERMISSIONS = LSDAttribute.PERMISSIONS.getKeyName();
    public static final String TITLE = LSDAttribute.TITLE.getKeyName();
    public static final String DESCRIPTION = LSDAttribute.DESCRIPTION.getKeyName();
    public static final String DELETED_PROPERTY = LSDAttribute.DELETED.getKeyName();
    public static final String ID = LSDAttribute.ID.getKeyName();
    public static final String UPDATED = LSDAttribute.UPDATED.getKeyName();
    public static final String VERSION = LSDAttribute.VERSION.getKeyName();
    public static final String VIEW_RADIUS = LSDAttribute.VIEW_RADIUS.getKeyName();
    public static final String URI = LSDAttribute.URI.getKeyName();
    public static final String NAME = LSDAttribute.NAME.getKeyName();
    public static final String TEXT_EXTENDED = LSDAttribute.TEXT_EXTENDED.getKeyName();
    public static final String IMAGE_URL = LSDAttribute.IMAGE_URL.getKeyName();
    public static final String ICON_URL = LSDAttribute.ICON_URL.getKeyName();
    public static final String BOARDS_URI = "pool:///boards";
    public static final LiquidURI ADMIN_ALIAS_URI = new LiquidURI(LiquidURIScheme.alias, "cazcade:system");


    private final static Logger log = Logger.getLogger(FountainNeo.class);
    private static final String ROOT_POOL_PROPERTY = "_root_pool";
    private static final String TRUE = "true";
    private static final String FALSE = "false";
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
    public static final String NODE_INDEX_NAME = "nodes";

    //    private final static Logger log = Logger.getLogger(FountainNeo.class);
    private EmbeddedGraphDatabase neo;
    private Index<Node> indexService;
    private Index<Node> fulltextIndexService;


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
    public static final String PINNED = LSDAttribute.PINNED.getKeyName();
    private Cache nodeAuthCache;


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
    }

    public Node getRootPool() {
        return rootPool;
    }

    public Transaction beginTx() {
        return neo.beginTx();
    }


    Node mergeProperties(Node node, LSDEntity entity, boolean update, boolean ignoreType, Runnable onRenameAction) throws InterruptedException {
        if (node.hasProperty(TYPE) && !ignoreType) {
            Object originalType = node.getProperty(TYPE);
            LSDTypeDef typedef = entity.getTypeDef();
            if (typedef != null) {
                LSDType newType = typedef.getPrimaryType();
                if (!newType.asString().equals(originalType)) {
                    throw new CannotChangeTypeException("The entity type used to be %s the request was to change it to %s", originalType, newType);
                }
            }
        }
        Map<String, String> map = entity.asMapForPersistence(ignoreType, update);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals(ID)) {
                if (update) {
                } else {
                    if (node.hasProperty(ID)) {
                        String currentId = node.getProperty(ID).toString();
                        if (!currentId.equals(entry.getValue())) {
                            throw new CannotChangeIdException("You cannot change the id of entity from %s to %s", currentId, entry.getValue());
                        }
                    } else {
                        //force lower case for IDs
                        node.setProperty(ID, entry.getValue().toLowerCase());
                    }
                }
            } else if (entry.getKey().equals(NAME) && node.hasProperty(NAME)) {
                //trigger a URL recalculation
                if (!entry.getValue().equals(node.getProperty(NAME))) {
                    node.setProperty(NAME, entry.getValue());
                    if (onRenameAction != null) {
                        onRenameAction.run();
                    }
                }
            } else {
                if (entry.getValue().equals("")) {
                    node.removeProperty(entry.getKey());
                } else {
                    node.setProperty(entry.getKey(), entry.getValue());
                }
            }
        }
        freeTextIndexNoTx(node);
        return node;
    }

    public void recalculateURI(Node childNode) throws InterruptedException {
        final Node parentNode = parentNode(childNode);
        if (childNode.getProperty(TYPE).toString().startsWith(LSDDictionaryTypes.POOL.toString())) {
            childNode.setProperty(URI, parentNode.getProperty(URI) + "/" + childNode.getProperty(NAME));
        } else if (childNode.getProperty(URI).toString().startsWith("pool://")) {
            childNode.setProperty(URI, parentNode.getProperty(URI) + "#" + childNode.getProperty(NAME));
        }
        reindex(childNode, URI, URI);
    }

    Node parentNode(Node childNode) {
        final Relationship parentRelationship = childNode.getSingleRelationship(CHILD, Direction.INCOMING);
        if (parentRelationship == null) {
            throw new OrphanedEntityException("The entity with URI %s has no parent.", childNode.getProperty(URI));
        }
        return parentRelationship.getOtherNode(childNode);
    }

    public void setIDIfNotSetOnNode(Node node) {
        if (!node.hasProperty(ID) || node.getProperty(ID).toString().isEmpty()) {
            node.setProperty(ID, UUIDFactory.randomUUID().toString().toLowerCase());
        }
    }

    void indexBy(Node node, String key, String luceneIndex, boolean unique) throws InterruptedException {
        String value = node.getProperty(key).toString().toLowerCase();
        log.debug("Indexing node " + node.getId() + " with key " + key + " with value " + value);
        IndexHits<Node> hits = indexService.get(luceneIndex, value);
        if (hits.size() > 0 && unique) {
            for (Node hit : hits) {
                if (!isDeleted(hit)) {
                    throw new DuplicateEntityException("Attempted to index an entity with the %s of %s, there is already an entity with that %s value.", key, value, key);
                }
            }
        }
        indexService.add(node, luceneIndex, value);
    }

    void reindex(Node node, String key, String luceneIndex) {
        indexService.remove(node, luceneIndex);
        String value = node.getProperty(key).toString().toLowerCase();
        log.debug("Indexing node " + node.getId() + " with key " + key + " with value " + value);
        IndexHits<Node> hits = indexService.get(luceneIndex, value);
        for (Node hit : hits) {
            indexService.remove(hit, luceneIndex, value);
        }
        indexService.add(node, luceneIndex, value);
    }

    private void unindex(Node node, String key, String luceneIndex) {
        String value = node.getProperty(key).toString().toLowerCase();
        log.debug("Un-indexing node " + node.getId() + " with key " + key + " with value " + value);
        indexService.remove(node, luceneIndex, value);
    }

    void timestamp(Node node) {
        if (!node.hasProperty(LSDAttribute.PUBLISHED.getKeyName())) {
            node.setProperty(LSDAttribute.PUBLISHED.getKeyName(), String.valueOf(System.currentTimeMillis()));
        }
        node.setProperty(LSDAttribute.UPDATED.getKeyName(), String.valueOf(System.currentTimeMillis()));
    }


    public Node findByURI(LiquidURI uri) throws InterruptedException {
        return findByURI(uri, false);
    }

    public Node findByURI(LiquidURI uri, boolean mustMatch) throws InterruptedException {
        begin();
        try {
            IndexHits<Node> nodes = indexService.get(URI, uri.asString());
            Node matchingNode = null;
            int nodeCount = 0;
            for (Node node : nodes) {
                if (!isDeleted(node)) {
                    nodeCount++;
                    matchingNode = node;
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

    public boolean isDeleted(Node node) throws InterruptedException {
        begin();
        try {
            return node.hasProperty(DELETED_PROPERTY);
        } finally {
            end();
        }
    }


    public void inheritPermissions(Node parent, Node child) {
        final LiquidPermissionSet liquidPermissionSet = LiquidPermissionSet.createPermissionSet(parent.getProperty(PERMISSIONS).toString());
        log.debug("Inheriting permission " + liquidPermissionSet);
        child.setProperty(PERMISSIONS, liquidPermissionSet.restoreDeletePermission().toString());
        log.debug("Child now has " + child.getProperty(PERMISSIONS));
    }

    public Node createNode() {
        Node node = neo.createNode();
        node.setProperty(VERSION, "1");
        return node;
    }


    public LSDEntity deleteEntityTx(LiquidURI uri, boolean children, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException {
        Node node = findByURI(uri);
        if (node == null) {
            throw new EntityNotFoundException("Could not find node identified by %s.", uri);
        }

        return deleteNodeTx(children, internal, node, detail);
    }

    private LSDEntity deleteNodeTx(boolean children, boolean internal, Node node, LiquidRequestDetailLevel detail) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                delete(node);
                if (children) {
                    Traverser traverser = node.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                            return currentPos.currentNode().hasProperty(URI);
                        }
                    }, CHILD, Direction.OUTGOING, VIEW, Direction.OUTGOING);
                    for (Node childNode : traverser) {
                        delete(childNode);
                    }
                }
                transaction.success();
                return convertNodeToLSD(node, detail, internal);
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

    public LSDEntity deleteEntityTx(LiquidUUID objectId, boolean children, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException {
        Node node = findByUUID(objectId);
        if (node == null) {
            throw new EntityNotFoundException("Could not find node identified by %s.", objectId);
        }

        return deleteNodeTx(children, internal, node, detail);
    }

    public Node findByUUID(LiquidUUID id) throws InterruptedException {
        begin();
        try {
            String idString = id.toString().toLowerCase();
            Node node = indexService.get(ID, idString).getSingle();
            if (node == null) {
                throw new EntityNotFoundException("Could not find node identified by %s.", idString);
            }
            if (isDeleted(node)) {
                throw new DeletedEntityException("Attempted to retrieve a deleted node.");
            }
            return node;
        } finally {
            end();
        }
    }

    void delete(Node node) {
        node.setProperty(DELETED_PROPERTY, "");
        indexService.remove(node, node.getProperty(LSDAttribute.URI.getKeyName()).toString());
        fulltextIndexService.remove(node, FREE_TEXT_SEARCH_INDEX_KEY);
    }

    public LSDEntity convertNodeToLSD(Node node, LiquidRequestDetailLevel detail, boolean internal) throws InterruptedException {
        begin();
        try {
            LSDSimpleEntity entity = LSDSimpleEntity.createEmpty();
            if (node == null) {
                return null;
            }
            if (detail == LiquidRequestDetailLevel.COMPLETE || detail == LiquidRequestDetailLevel.NORMAL || detail == LiquidRequestDetailLevel.FREE_TEXT_SEARCH_INDEX) {
                final Iterable<String> iterable = node.getPropertyKeys();
                for (String key : iterable) {
                    if (!key.startsWith("_")) {
                        LSDAttribute attributeItem = LSDAttribute.valueOf(key);
                        if (attributeItem == null) {
                            //so we don't recognize, it well fine! it got there somehow so we'll give it to you ;-)
                            //also child entities will not be understood as attributes.
                            entity.setValue(key, node.getProperty(key).toString());
                        } else if (!attributeItem.isHidden() || internal) {
                            //We don't return non-indexable attributes for a search index level of detail
                            if (detail != LiquidRequestDetailLevel.FREE_TEXT_SEARCH_INDEX || attributeItem.isFreeTexSearchable()) {
                                entity.setAttribute(attributeItem, node.getProperty(key).toString());
                            }
                        }
                    }
                }
            } else if (detail == LiquidRequestDetailLevel.TITLE_AND_NAME) {
                if (node.hasProperty(TITLE)) {
                    entity.setAttribute(LSDAttribute.TITLE, (String) node.getProperty(TITLE));
                }
                if (node.hasProperty(NAME)) {
                    entity.setAttribute(LSDAttribute.NAME, (String) node.getProperty(NAME));
                }
                entity.setAttribute(LSDAttribute.ID, (String) node.getProperty(ID));
            } else if (detail == LiquidRequestDetailLevel.PERSON_MINIMAL) {
                addValuesToEntity(node, entity, LSDAttribute.NAME, LSDAttribute.FULL_NAME, LSDAttribute.IMAGE_URL, LSDAttribute.ID, LSDAttribute.URI);
            } else if (detail == LiquidRequestDetailLevel.BOARD_LIST) {
                addValuesToEntity(node, entity, LSDAttribute.NAME, LSDAttribute.TITLE, LSDAttribute.DESCRIPTION, LSDAttribute.COMMENT_COUNT, LSDAttribute.FOLLOWERS_COUNT, LSDAttribute.IMAGE_URL, LSDAttribute.ICON_URL, LSDAttribute.ID, LSDAttribute.URI);
            } else if (detail == LiquidRequestDetailLevel.PERMISSION_DELTA) {
                addValuesToEntity(node, entity, LSDAttribute.URI, LSDAttribute.ID, LSDAttribute.MODIFIABLE, LSDAttribute.EDITABLE, LSDAttribute.VIEWABLE);
            } else if (detail == LiquidRequestDetailLevel.MINIMAL) {
                addValuesToEntity(node, entity, LSDAttribute.URI, LSDAttribute.ID);
            } else {
                throw new UnsupportedOperationException("The level of detail " + detail + " was not recognized.");
            }
            return entity;
        } finally {
            end();
        }
    }

    private void addValuesToEntity(Node node, LSDEntity entity, LSDAttribute... attributes) {
        for (LSDAttribute attribute : attributes) {
            if (node.hasProperty(attribute.getKeyName())) {
                entity.setAttribute(attribute, node.getProperty(attribute.getKeyName()).toString());
            }
        }
    }

    public LSDEntity getEntityByUUID(LiquidUUID id, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                Node node = findByUUID(id);
                return convertNodeToLSD(node, detail, internal);
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

    public void assertAuthorized(Node node, LiquidSessionIdentifier identity, LiquidPermission... permissions) throws InterruptedException {
        if (!isAuthorized(node, identity, permissions)) {
            throw new AuthorizationException("Session " + identity.toString() + " is not authorized to " + Arrays.toString(permissions) + " the resource " + (node.hasProperty(URI) ? node.getProperty(URI) : "<unknown>") + " permissions are " + node.getProperty(PERMISSIONS));
        }
    }

    public boolean isAuthorized(Node node, LiquidSessionIdentifier identity, LiquidPermission... permissions) throws InterruptedException {
        begin();
        try {
            if (node == null) {
                return false;
            }
            StringBuilder cacheKeyBuilder = new StringBuilder().append(node.getId()).append(':').append(identity.getAliasURL()).append(':');
            for (LiquidPermission permission : permissions) {
                cacheKeyBuilder.append(permission.ordinal()).append(',');
            }
            String cacheKey = cacheKeyBuilder.toString();
            if (nodeAuthCache.get(cacheKey) != null) {
                return (Boolean) nodeAuthCache.get(cacheKey).getValue();
            } else {
                boolean result = isAuthorizedInternal(node, identity, permissions);

                //remember nodes are regarded by Fountain as immutable
                // any attempt to change their permissions should result in a new node being created.
                //therefore the cache lifetime is eternal
                nodeAuthCache.put(new Element(cacheKey, result, true, null, null));
                return result;
            }
        } finally {
            end();
        }

    }

    private boolean isAuthorizedInternal(Node node, LiquidSessionIdentifier identity, LiquidPermission... permissions) throws InterruptedException {
        assertLatestVersion(node);
        if (identity == null) {
            return false;
        }
//            if (identity.getName().equals("neo")) {
//                log.debug("He is the one, so he can do as he wishes.");
//                return true;
//            }
        if (identity.getName().equals("admin")) {
            log.debug("Admin user privilege used.");
            return true;
        }
        if (identity.getName().equals(SYSTEM)) {
            log.debug("System user privilege used.");
            return true;
        }
        String permissionsStr = (String) node.getProperty(PERMISSIONS);
        if (log.isDebugEnabled()) {
            log.debug("Authorizing {0} for permission {1} on object {2}; permission set is {3}.", identity.getName(), Arrays.toString(permissions), node.getProperty(ID), permissionsStr);
        }
        LiquidPermissionSet permissionSet = LiquidPermissionSet.createPermissionSet(permissionsStr);
        try {
            final Relationship ownerRelationship = node.getSingleRelationship(OWNER, Direction.OUTGOING);
            if (ownerRelationship == null) {
                log.debug("No owner found on {0}/{1} .", node.getProperty(ID), node.getProperty(URI));
            } else {
                log.debug("Owner node is {0}", ownerRelationship.getEndNode().getProperty(URI));
            }
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        //is owner
        if (isOwnerNoTX(identity, node)) {
            log.debug("Authorizing {0} as owner on {1}.", identity.getName(), node.getProperty(ID));
            for (LiquidPermission permission : permissions) {
                if (!permissionSet.hasPermission(LiquidPermissionScope.OWNER, permission)) {
                    return false;
                }
            }
            return true;
        } else if (identity.getName().equals("anon")) {
            log.debug("Authorizing {0} as anonymous on {1}.", identity.getName(), node.getProperty(URI));
            for (LiquidPermission permission : permissions) {
                if (!permissionSet.hasPermission(LiquidPermissionScope.UNKNOWN, permission)) {
                    return false;
                }
            }
            return true;
        } else {
            log.debug("Authorizing {0} as world on {1}.", identity.getName(), node.getProperty(URI));
            for (LiquidPermission permission : permissions) {
                if (!permissionSet.hasPermission(LiquidPermissionScope.WORLD, permission)) {
                    return false;
                }
            }
            return true;
        }

    }

    public boolean isOwnerNoTX(final Node ownerNode, Node node) throws InterruptedException {
        begin();
        try {
            final Iterable<Relationship> relationships = node.getRelationships(OWNER, Direction.OUTGOING);
            for (Relationship relationship : relationships) {
                if (relationship.getOtherNode(node).equals(ownerNode)) {
                    return true;
                }
            }
            return false;
        } finally {
            end();
        }
    }

    public boolean isOwnerNoTX(final LiquidSessionIdentifier identity, Node node) throws InterruptedException {
        begin();
        try {
            //Here I am traversing from the node supplied to it's owner alias, to the identity node that the alias relates to.
            //It's cleaner using the traverser as there would be loads of conditional logic here as not all aliases have
            //an associated identity etc.
            Traverser traverser = node.traverse(Traverser.Order.DEPTH_FIRST, new StopEvaluator() {
                        public boolean isStopNode(TraversalPosition currentPos) {
                            return currentPos.currentNode().hasProperty(URI) && currentPos.currentNode().getProperty(URI).equals(identity.getUserURL().asString());
                        }
                    }, new ReturnableEvaluator() {
                        public boolean isReturnableNode(TraversalPosition currentPos) {
                            return currentPos.currentNode().hasProperty(URI) && currentPos.currentNode().getProperty(URI).equals(identity.getUserURL().asString());
                        }
                    }, OWNER, Direction.OUTGOING, FountainRelationships.ALIAS, Direction.OUTGOING
            );
            return traverser.iterator().hasNext();
        } finally {
            end();
        }
    }


    public void start() throws Exception {
        try {
            super.start();

            if (!CacheManager.getInstance().cacheExists("nodeauth")) {
                CacheManager.getInstance().addCache("nodeauth");
            }
            nodeAuthCache = CacheManager.getInstance().getCache("nodeauth");

            HashMap<String, String> params = new HashMap<String, String>();
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
        String dir = CommonConstants.NEO_BACKUP_DIR + "/" + System.currentTimeMillis();
        new File(dir).mkdirs();
        new FountainNeoExporter(neo).export(dir);
    }

    Node createSystemPool(String pool) throws InterruptedException {
        Node rootPool = createNode();
        rootPool.setProperty(ROOT_POOL_PROPERTY, TRUE);
        setIDIfNotSetOnNode(rootPool);
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


    public void assertLatestVersion(Node node) {
        //no begin block for an assertion, no point in this holding up a shutdown.
        Relationship versionChildRelationship = node.getSingleRelationship(VERSION_PARENT, Direction.INCOMING);
        if (!isLatestVersion(node)) {
            throw new StaleUpdateException("Attempted to reference a stale node %s which has already been updated (i.e. stale version).", node.getProperty(URI));
        }
    }

    public boolean isLatestVersion(Node node) {
        //no begin block for an assertion, no point in this holding up a shutdown.
        Relationship versionChildRelationship = node.getSingleRelationship(VERSION_PARENT, Direction.INCOMING);
        return versionChildRelationship == null;
    }

    private void migrateParentNode(Node node, Node clone, boolean fork) {
        assertLatestVersion(node);
        Iterable<Relationship> relationships = node.getRelationships(CHILD, Direction.OUTGOING);
        for (Relationship relationship : relationships) {
            Node childNode = relationship.getOtherNode(node);
            clone.createRelationshipTo(childNode, CHILD);
            relationship.delete();
        }
        if (!fork) {
            Relationship parentRel = node.getSingleRelationship(CHILD, Direction.INCOMING);
            if (parentRel != null) {
                Node parentNode = parentRel.getOtherNode(node);
                parentNode.createRelationshipTo(clone, CHILD);
                parentRel.delete();
            }
        }
    }

    Node cloneNodeForNewVersion(LiquidSessionIdentifier editor, Node node, boolean fork) throws InterruptedException {
        fulltextIndexService.remove(node, FREE_TEXT_SEARCH_INDEX_KEY);
        Node clone = createNode();
        Iterable<String> keys = node.getPropertyKeys();
        for (String key : keys) {
            clone.setProperty(key, node.getProperty(key));
        }
        clone.setProperty(ID, UUIDFactory.randomUUID().toString());
        indexBy(clone, ID, ID, true);
        final String versionString = (String) node.getProperty(VERSION);

        migrateParentNode(node, clone, fork);

        if (fork) {
            Long lastFork = null;
            if (node.hasProperty(LSDAttribute.LAST_FORK_VERSION.getKeyName())) {
                lastFork = (Long) node.getProperty(LSDAttribute.LAST_FORK_VERSION.getKeyName());
            }
            if (lastFork == null) {
                node.setProperty(LSDAttribute.LAST_FORK_VERSION.getKeyName(), (long) 1);
                lastFork = 1L;
            } else {
                node.setProperty(LSDAttribute.LAST_FORK_VERSION.getKeyName(), ++lastFork);
            }
            clone.setProperty(VERSION, versionString + "." + lastFork + ".1");

        } else {
            reindex(clone, URI, URI);
            if (node.hasRelationship(FountainRelationships.FORK_PARENT, Direction.OUTGOING)) {
                clone.createRelationshipTo(node.getSingleRelationship(FountainRelationships.FORK_PARENT, Direction.OUTGOING).getOtherNode(node), FountainRelationships.FORK_PARENT);
            } else {
                clone.createRelationshipTo(node, FountainRelationships.FORK_PARENT);
            }
            int lastDot = versionString.lastIndexOf('.');
            String newVersion;
            if (lastDot < 0) {
                newVersion = String.valueOf(Long.parseLong(versionString) + 1);
            } else {
                newVersion = versionString.substring(0, lastDot) + "." + (Long.parseLong(versionString.substring(lastDot + 1)) + 1);
            }
            clone.setProperty(VERSION, newVersion);
        }

        Node editorNode = findByURI(editor.getAliasURL());
        //TODO: remove this restriction in later version of Neo4J (i.e. when it supports self references)
        if (!editorNode.equals(clone)) {
            clone.createRelationshipTo(editorNode, EDITOR);
        }

        Iterable<Relationship> relationships = node.getRelationships(FOLLOW_CONTENT, FOLLOW_ALIAS, AUTHOR, CREATOR, OWNER, VIEW, ALIAS);
        for (Relationship relationship : relationships) {
            if (relationship.getStartNode().equals(node)) {
                clone.createRelationshipTo(relationship.getEndNode(), relationship.getType());
                if (relationship.getType() == AUTHOR || relationship.getType() == CREATOR || relationship.getType() == OWNER || relationship.getType() == VIEW) {
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
        clone.createRelationshipTo(node, VERSION_PARENT);
        timestamp(clone);
        return clone;
    }

    public LSDEntity updateUnversionedEntityByUUIDTx(LiquidUUID id, LSDEntity entity, boolean internal, LiquidRequestDetailLevel detail, Runnable onRenameAction) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                Node node = findByUUID(id);
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
                if (!entity.getID().equals(id)) {
                    throw new CannotChangeIdException("Tried to change the id of %s to %s", id.toString(), entity.getID().toString());
                }
                LSDEntity newObject = convertNodeToLSD(mergeProperties(node, entity, true, false, onRenameAction), detail, internal);
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


    public LSDEntity updateEntityByUUIDTx(LiquidSessionIdentifier editor, LiquidUUID id, LSDEntity entity, boolean internal, LiquidRequestDetailLevel detail, Runnable onRenameAction) throws Exception {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                if (!entity.getID().equals(id)) {
                    throw new CannotChangeIdException("Tried to change the id of %s to %s", id.toString(), entity.getID().toString());
                }
                Node origNode = findByUUID(id);
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

    private LSDEntity updateNodeNoTx(LiquidSessionIdentifier editor, LSDEntity entity, boolean internal, LiquidRequestDetailLevel detail, Transaction transaction, Node origNode, Runnable onRenameAction) throws Exception {
        Node node = updateNodeAndReturnNodeNoTx(editor, origNode, entity, onRenameAction);
        LSDEntity newObject = convertNodeToLSD(node, detail, internal);
        transaction.success();
        return newObject;
    }

    public Node updateNodeAndReturnNodeNoTx(final LiquidSessionIdentifier editor, final Node origNode, final LSDEntity entity, final Runnable onRenameAction) throws Exception {
        return doInBeginBlock(new Callable<Node>() {
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
                Node node = cloneNodeForNewVersion(editor, origNode, false);
                mergeProperties(node, entity, true, false, onRenameAction);
                return node;
            }
        });
    }

    public LSDEntity updateEntityByURITx(LiquidSessionIdentifier editor, LiquidURI uri, LSDEntity entity, boolean internal, LiquidRequestDetailLevel detail, Runnable onRenameAction) throws Exception {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                Node origNode = findByURI(uri);
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


    void forEachChild(Node node, NodeCallback callback) throws Exception {
        Iterable<Relationship> relationships = node.getRelationships(CHILD, Direction.OUTGOING);
        for (Relationship relationship : relationships) {
            Node poolObjectNode = relationship.getOtherNode(node);
            if (!isDeleted(poolObjectNode)) {
                callback.call(getLatestVersionFromFork(poolObjectNode));
            }
        }

        Iterable<Relationship> linkedRelationships = node.getRelationships(FountainRelationships.LINKED_CHILD, Direction.OUTGOING);
        for (Relationship relationship : linkedRelationships) {
            Node poolObjectNode = relationship.getOtherNode(node);
            if (!isDeleted(poolObjectNode)) {
                callback.call(poolObjectNode);
            }
        }
    }

    Node getLatestVersionFromFork(Node node) {
        log.debug("Getting latest version for {0}.", node.getProperty(ID));
        if (node.hasRelationship(VERSION_PARENT, Direction.INCOMING)) {
            return getLatestVersionFromFork(node.getSingleRelationship(VERSION_PARENT, Direction.INCOMING).getOtherNode(node));
        } else {
            return node;
        }

    }


    int popularity(Node child) {
        int score = 0;
        for (Relationship relationship : child.getRelationships()) {
            score++;
        }
        return score;
    }

    void setPermissionFlagsOnEntity(LiquidSessionIdentifier session, Node child, Node parent, LSDEntity entity) throws InterruptedException {
        if (parent == null) {
            entity.setAttribute(LSDAttribute.MODIFIABLE, isAuthorized(child, session, LiquidPermission.MODIFY) ? "true" : "false");
            entity.setAttribute(LSDAttribute.EDITABLE, isAuthorized(child, session, LiquidPermission.EDIT) ? "true" : "false");
            entity.setAttribute(LSDAttribute.DELETABLE, isAuthorized(child, session, LiquidPermission.DELETE) ? "true" : "false");
        } else {
            entity.setAttribute(LSDAttribute.MODIFIABLE, isAuthorized(parent, session, LiquidPermission.EDIT) || (isAuthorized(parent, session, LiquidPermission.MODIFY) && isAuthorized(child, session, LiquidPermission.MODIFY)) ? "true" : "false");
            entity.setAttribute(LSDAttribute.EDITABLE, isAuthorized(parent, session, LiquidPermission.EDIT) || (isAuthorized(parent, session, LiquidPermission.MODIFY) && isAuthorized(child, session, LiquidPermission.EDIT)) ? "true" : "false");
            entity.setAttribute(LSDAttribute.DELETABLE, isAuthorized(parent, session, LiquidPermission.EDIT) || (isAuthorized(parent, session, LiquidPermission.MODIFY) && isAuthorized(child, session, LiquidPermission.DELETE)) ? "true" : "false");
        }
        entity.setAttribute(LSDAttribute.ADMINISTERABLE, isAuthorized(child, session, LiquidPermission.SYSTEM) ? "true" : "false");
    }


    void putProfileInformationIntoAlias(final Node alias) {
        try {
            final LiquidURI poolURI = new LiquidURI("pool:///people/" + alias.getProperty(NAME).toString() + "/profile");
            Node pool = findByURI(poolURI);
            if (pool == null) {
                throw new EntityNotFoundException("Could not locate pool %s", poolURI.toString());
            }
            if (pool.hasProperty(LSDAttribute.IMAGE_URL.getKeyName())) {
                alias.setProperty(LSDAttribute.IMAGE_URL.getKeyName(), pool.getProperty(LSDAttribute.IMAGE_URL.getKeyName()));
            }
            if (pool.hasProperty(LSDAttribute.IMAGE_WIDTH.getKeyName())) {
                alias.setProperty(LSDAttribute.IMAGE_WIDTH.getKeyName(), pool.getProperty(LSDAttribute.IMAGE_WIDTH.getKeyName()));
            }
            if (pool.hasProperty(LSDAttribute.IMAGE_HEIGHT.getKeyName())) {
                alias.setProperty(LSDAttribute.IMAGE_HEIGHT.getKeyName(), pool.getProperty(LSDAttribute.IMAGE_HEIGHT.getKeyName()));
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    double calculateRadius(Node viewNode) {
        if (viewNode.hasProperty(LSDAttribute.VIEW_X.getKeyName()) && viewNode.hasProperty(LSDAttribute.VIEW_Y.getKeyName())) {
            double x = Double.valueOf(viewNode.getProperty(LSDAttribute.VIEW_X.getKeyName()).toString());
            double y = Double.valueOf(viewNode.getProperty(LSDAttribute.VIEW_Y.getKeyName()).toString());
            double thisdistance = Math.sqrt((x * x + y * y));
            return thisdistance;
        } else {
            return 0;
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


    public void updateSessionTx(LiquidUUID session) throws InterruptedException {
        begin();
        try {
            final Transaction transaction = neo.beginTx();
            try {
                Node node = findByUUID(session);
                timestamp(node);
                node.setProperty(LSDAttribute.ACTIVE.getKeyName(), "true");
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

    public void freeTextIndexNoTx(Node node) throws InterruptedException {
        final LSDEntity entity = convertNodeToLSD(node, LiquidRequestDetailLevel.FREE_TEXT_SEARCH_INDEX, true);
        String freeText = entity.asFreeText();
        fulltextIndexService.add(node, FREE_TEXT_SEARCH_INDEX_KEY, freeText);
    }

    public IndexHits<Node> freeTextSearch(String searchText) {
        return fulltextIndexService.query(FREE_TEXT_SEARCH_INDEX_KEY, searchText);
    }

    void setRootPool(Node rootPool) {
        this.rootPool = rootPool;
    }

    Node getPeoplePool() {
        return peoplePool;
    }

    void setPeoplePool(Node peoplePool) {
        this.peoplePool = peoplePool;
    }

    private LiquidPermissionSet convertChangeRequestIntoPermissionSet(LiquidPermissionChangeType change, LiquidPermissionSet existingPermissions) {
        if (change == LiquidPermissionChangeType.MAKE_PRIVATE) {
            return LiquidPermissionSet.getPrivatePermissionSet();
        } else if (change == LiquidPermissionChangeType.MAKE_PUBLIC) {
            return LiquidPermissionSet.getPublicPermissionSet();
        } else if (change == LiquidPermissionChangeType.MAKE_PUBLIC_READONLY) {
            return LiquidPermissionSet.getDefaultPermissions();
        } else {
            return existingPermissions;
        }
    }

    public LSDEntity changePermissionNoTx(LiquidSessionIdentifier editor, LiquidURI uri, LiquidPermissionChangeType change, LiquidRequestDetailLevel detail, boolean internal) throws Exception {
        begin();
        try {
            Node startNode = findByURI(uri);
            if (startNode == null) {
                throw new EntityNotFoundException("Could not find entity %s", uri);
            }
            startNode = changeNodePermissionNoTx(startNode, editor, change);
            log.debug("Changed permission of {0} to {1}", uri, change);
            return convertNodeToLSD(startNode, detail, internal);
        } finally {
            end();
        }


    }

    public Node changeNodePermissionNoTx(final Node node, final LiquidSessionIdentifier editor, final LiquidPermissionChangeType change) throws Exception {
        return doInBeginBlock(new Callable<Node>() {
            @Override
            public Node call() throws Exception {
                assertLatestVersion(node);
                Node startNode = node;
                Traverser traverser = startNode.traverse(Traverser.Order.BREADTH_FIRST, StopEvaluator.END_OF_GRAPH, ReturnableEvaluator.ALL, FountainRelationships.CHILD, Direction.OUTGOING);
                Collection<Node> allNodes = traverser.getAllNodes();
                log.debug("Changing permission on {0} nodes to {1} .", allNodes.size(), change);
                for (Node origNode : allNodes) {
                    Node cloneNode = cloneNodeForNewVersion(editor, origNode, false);
                    String permissions = (String) cloneNode.getProperty(PERMISSIONS);
                    LiquidPermissionSet newPermissions = convertChangeRequestIntoPermissionSet(change, LiquidPermissionSet.createPermissionSet(permissions));
                    cloneNode.setProperty(PERMISSIONS, newPermissions.toString());
                    //So we have cloned the start node and need to use the cloned version to build our result.
                    if (origNode.getId() == startNode.getId()) {
                        startNode = cloneNode;
                    }
                }
                return startNode;
            }
        });
    }

    public <T> T doInTransaction(Callable<T> callable) throws Exception {
        Transaction transaction = beginTx();
        try {
            T result = callable.call();
            transaction.success();
            return result;
        } catch (Exception e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }

    }

    public <T> T doInBeginBlock(Callable<T> callable) throws Exception {
        begin();
        try {

            T result = callable.call();

            return result;

        } finally {
            end();
        }

    }


    public <T> T doInTransactionAndBeginBlock(Callable<T> callable) throws Exception {
        begin();
        try {
            Transaction transaction = beginTx();
            try {
                T result = callable.call();
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


    public GraphDatabaseService getGraphDatabase() {
        return neo;
    }

    public EmbeddedGraphDatabase getNeo() {
        return neo;
    }

    public Index<Node> getIndexService() {
        return indexService;
    }

//    public FountainPoolDAO getFountainPoolDAO() {
//        return fountainPoolDAO;
//    }
//
//    public FountainUserDAO getFountainUserDAO() {
//        return fountainUserDAO;
//    }
}
