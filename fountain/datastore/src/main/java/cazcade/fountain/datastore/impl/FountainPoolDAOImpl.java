package cazcade.fountain.datastore.impl;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.*;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.impl.SortUtil;
import org.neo4j.graphdb.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import static cazcade.fountain.datastore.impl.FountainRelationships.*;

public class FountainPoolDAOImpl implements FountainPoolDAO {

    private final static Logger log = Logger.getLogger(FountainPoolDAOImpl.class);

    @Autowired
    private FountainNeo fountainNeo;


    @Autowired
    private FountainIndexServiceImpl indexDAO;


    @Autowired
    private FountainUserDAO userDAO;


    public FountainPoolDAOImpl() {


    }

    public void recalculatePoolURIs(Node node) throws InterruptedException {
        fountainNeo.recalculateURI(node);
        Traverser traverser = node.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            public boolean isReturnableNode(TraversalPosition currentPos) {
                return currentPos.currentNode().hasProperty(FountainNeo.URI);
            }
        }, FountainRelationships.CHILD, Direction.OUTGOING);
        for (Node childNode : traverser) {
            fountainNeo.recalculateURI(childNode);
        }
    }

    @Override
    public LSDEntity updatePool(LiquidSessionIdentifier sessionIdentifier, Node node, LiquidRequestDetailLevel detail, boolean internal, boolean historical, Integer end, int start, ChildSortOrder order, boolean contents, LSDEntity requestEntity, Runnable onRenameAction) throws Exception {
        Node resultNode = fountainNeo.updateNodeAndReturnNodeNoTx(sessionIdentifier, node, requestEntity, onRenameAction);
        indexDAO.syncBoard(node);
        return getPoolAndContentsNoTx(resultNode, detail, contents, order, internal, sessionIdentifier, start, end, historical);
    }


    @Override
    public Node createPoolNoTx(LiquidSessionIdentifier identity, LiquidURI owner, Node parent, String poolName, double x, double y, String title, boolean listed) throws InterruptedException {
        return createPoolNoTx(identity, owner, parent, LSDDictionaryTypes.POOL2D, poolName, x, y, title, listed);

    }

    @Override
    public Node createPoolNoTx(LiquidSessionIdentifier identity, LiquidURI owner, Node parent, LSDType type, String poolName, double x, double y, String title, boolean listed) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (parent == null) {
                throw new DataStoreException("Tried to create a pool with a null parent node.");
            }
            fountainNeo.assertAuthorized(parent, identity, LiquidPermission.MODIFY, LiquidPermission.VIEW);
            final Node node = fountainNeo.createNode();
            fountainNeo.setIDIfNotSetOnNode(node);
            node.setProperty(LSDAttribute.LISTED.getKeyName(), listed ? "true" : "false");

            String parentURI = (String) parent.getProperty(FountainNeo.URI);
            if (!parentURI.endsWith("/")) {
                parentURI += "/";
            }
            final String newURI = parentURI + poolName.toLowerCase();
            node.setProperty(FountainNeo.URI, newURI);
            node.setProperty(FountainNeo.NAME, poolName);
            if (title != null) {
                node.setProperty(FountainNeo.TITLE, title);
            }
            node.setProperty(FountainNeo.TYPE, type.asString());
            if (!parent.hasProperty(FountainNeo.PERMISSIONS)) {
                throw new DataStoreException("The parent pool %s had no permissions, all pools must have permissions.", parentURI);
            }
            fountainNeo.inheritPermissions(parent, node);
            parent.createRelationshipTo(node, FountainRelationships.CHILD);
            Node ownerNode = fountainNeo.findByURI(owner);
            node.createRelationshipTo(ownerNode, FountainRelationships.OWNER);
            node.createRelationshipTo(ownerNode, FountainRelationships.CREATOR);
            node.createRelationshipTo(ownerNode, FountainRelationships.EDITOR);
            LSDSimpleEntity view = LSDSimpleEntity.createEmpty();
            view.setAttribute(LSDAttribute.VIEW_X, String.valueOf(x));
            view.setAttribute(LSDAttribute.VIEW_Y, String.valueOf(y));
            view.setAttribute(LSDAttribute.VIEW_WIDTH, "200");
            view.setAttribute(LSDAttribute.VIEW_HEIGHT, "200");
            createView(node, view);
            userDAO.addAuthorToNodeNoTX(owner, false, node);
            fountainNeo.indexBy(node, FountainNeo.ID, FountainNeo.ID, true);
            fountainNeo.indexBy(node, FountainNeo.URI, FountainNeo.URI, true);
            fountainNeo.timestamp(node);
            assertHasOwner(node);
            indexDAO.syncBoard(node);
            return node;
        } finally {
            fountainNeo.end();
        }
    }

    public Node createPoolObjectNoTx(LiquidSessionIdentifier identity, Node pool, LSDEntity entity, LiquidURI owner, LiquidURI author, boolean createAuthor) throws InterruptedException {
        fountainNeo.begin();
        try {
            fountainNeo.assertAuthorized(pool, identity, LiquidPermission.MODIFY, LiquidPermission.VIEW);
            LSDEntity entityCopy = entity.copy();
            //We shouldn't be using the ID supplied to us.
            entityCopy.removeCompletely(LSDAttribute.ID);
            String name = entityCopy.getAttribute(LSDAttribute.NAME);
            if (name == null) {
                name = entityCopy.getTypeDef().getPrimaryType().getGenus().toLowerCase() + System.currentTimeMillis();
                entityCopy.setAttribute(LSDAttribute.NAME, name);
            }
            LSDEntity viewEntity = entityCopy.removeSubEntity(LSDAttribute.VIEW);
            viewEntity.setAttribute(LSDAttribute.ID, "");
            if (author == null) {
                throw new NullPointerException("Null author passed to createPoolObjectNoTx().");
            }

            final Node node = fountainNeo.createNode();
            fountainNeo.mergeProperties(node, entityCopy, false, false, null);

            fountainNeo.setIDIfNotSetOnNode(node);
            String uri = pool.getProperty(FountainNeo.URI) + "#" + name.toLowerCase();
            node.setProperty(FountainNeo.URI, uri);
            pool.createRelationshipTo(node, FountainRelationships.CHILD);
            Node ownerNode = fountainNeo.findByURI(owner);
            node.createRelationshipTo(ownerNode, FountainRelationships.OWNER);
            node.createRelationshipTo(ownerNode, FountainRelationships.CREATOR);
            node.createRelationshipTo(ownerNode, FountainRelationships.EDITOR);
            userDAO.addAuthorToNodeNoTX(author, createAuthor, node);
            fountainNeo.inheritPermissions(pool, node);
            fountainNeo.indexBy(node, FountainNeo.ID, FountainNeo.ID, true);
            fountainNeo.indexBy(node, FountainNeo.URI, FountainNeo.URI, true);
            fountainNeo.timestamp(node);
            Node view = createView(node, viewEntity);
            assertHasOwner(node);
            indexDAO.incrementBoardActivity(pool);
            return node;
        } finally {
            fountainNeo.end();
        }
    }

    Node createView(Node object, LSDEntity viewEntity) throws InterruptedException {
        final Node node = fountainNeo.createNode();
        node.setProperty(FountainNeo.TYPE, LSDDictionaryTypes.VIEW.getValue());
        fountainNeo.mergeProperties(node, viewEntity, false, false, null);
        fountainNeo.setIDIfNotSetOnNode(node);
        String uri = object.getProperty(FountainNeo.URI) + ":view";
        node.setProperty(FountainNeo.URI, uri);
        object.createRelationshipTo(node, FountainRelationships.VIEW);
        node.setProperty(FountainNeo.PERMISSIONS, object.getProperty(FountainNeo.PERMISSIONS));
        node.setProperty(FountainNeo.VIEW_RADIUS, String.valueOf(fountainNeo.calculateRadius(node)));
        if (!node.hasProperty(LSDAttribute.VIEW_X.getKeyName())) {
            node.setProperty(LSDAttribute.VIEW_X.getKeyName(), String.valueOf(Math.random() * 100 - 50));
        }

        if (!node.hasProperty(LSDAttribute.VIEW_Y.getKeyName())) {
            node.setProperty(LSDAttribute.VIEW_Y.getKeyName(), String.valueOf((Math.random() * 100) - 50));
        }

        fountainNeo.indexBy(node, FountainNeo.ID, FountainNeo.ID, true);
        fountainNeo.indexBy(node, FountainNeo.URI, FountainNeo.URI, true);
        fountainNeo.timestamp(node);
        return node;
    }

    Node copyPoolObjectForUpdate(LiquidSessionIdentifier editor, Node node, boolean fork) throws InterruptedException {
        fountainNeo.assertLatestVersion(node);
        return fountainNeo.cloneNodeForNewVersion(editor, node, fork);
    }

    @Override
    public LSDEntity updatePoolObjectNoTx(LiquidSessionIdentifier identity, LiquidSessionIdentifier editor, LSDEntity entity, Node pool, Node origNode, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException {

        LSDEntity entityCopy = entity.copy();
        entityCopy.removeSubEntity(LSDAttribute.VIEW);
        entityCopy.removeSubEntity(LSDAttribute.AUTHOR);
        entityCopy.removeSubEntity(LSDAttribute.OWNER);
        entityCopy.removeValue(LSDAttribute.ID);
        LSDEntity newObject;

        final Node node = copyPoolObjectForUpdate(editor, origNode, false);
        if (!entity.getURI().toString().toLowerCase().equals(origNode.getProperty(FountainNeo.URI))) {
            throw new CannotChangeURIException("Tried to change the URI of %s to %s", origNode.getProperty(FountainNeo.URI).toString(), entity.getURI().toString());
        }
        newObject = convertNodeToEntityWithRelatedEntitiesNoTX(identity, fountainNeo.mergeProperties(node, entityCopy, true, false, new Runnable() {
            @Override
            public void run() {
                try {
                    recalculatePoolURIs(node);
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }
        }), pool, detail, internal, false);
        assertHasOwner(node);
        if (pool != null) {
            indexDAO.incrementBoardActivity(pool);
        }
        return newObject;

    }

    @Override
    public LSDEntity getPoolObjectTx(LiquidSessionIdentifier identity, LiquidURI uri, boolean internal, boolean historical, LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.getNeo().beginTx();
            try {
                Node node = fountainNeo.findByURI(uri);
                Node parent = fountainNeo.findByURI(uri.getWithoutFragmentOrComment());

                if (node == null) {
                    return null;
                }
                assertHasOwner(node);
                assertHasOwner(parent);
                return convertNodeToEntityWithRelatedEntitiesNoTX(identity, fountainNeo.getLatestVersionFromFork(node), parent, detail, internal, historical);
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } finally {
            fountainNeo.end();
        }
    }

    @Deprecated //Use URI based methods instead
    public LSDEntity getPoolObjectTx(LiquidSessionIdentifier identity, LiquidUUID uuid, boolean internal, boolean historical, LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.getNeo().beginTx();
            try {
                Node node = fountainNeo.findByUUID(uuid);
                if (node == null) {
                    return null;
                }
                return convertNodeToEntityWithRelatedEntitiesNoTX(identity, fountainNeo.getLatestVersionFromFork(node), null, detail, internal, historical);
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public LSDEntity linkPoolObjectTx(final LiquidSessionIdentifier editor, final LiquidURI newOwner, final LiquidURI target, final LiquidURI to, final LiquidRequestDetailLevel detail, final boolean internal) throws Exception {
        return fountainNeo.doInTransaction(new Callable<LSDEntity>() {
            @Override
            public LSDEntity call() throws Exception {
                return convertNodeToEntityWithRelatedEntitiesNoTX(editor, linkPoolObject(editor, fountainNeo.findByURI(newOwner), fountainNeo.findByURI(target), fountainNeo.findByURI(to)),
                        null, detail, internal, false);
            }
        });
    }

    @Override
    public Node linkPoolObject(LiquidSessionIdentifier editor, Node newOwner, Node target, Node to) throws InterruptedException {
        fountainNeo.begin();
        try {
            Node clone = fountainNeo.cloneNodeForNewVersion(editor, target, true);
            assertHasOwner(clone);
            String candidateName = clone.getProperty(FountainNeo.NAME).toString();
            String candidateURI = to.getProperty(FountainNeo.URI) + "#" + candidateName;
            String name;
            int count = 1;
            while (fountainNeo.findByURI(new LiquidURI(candidateURI)) != null) {
                name = candidateName + count++;
                candidateURI = to.getProperty(FountainNeo.URI) + "#" + name;
            }
            clone.setProperty(FountainNeo.URI, candidateURI);
            fountainNeo.reindex(clone, FountainNeo.URI, FountainNeo.URI);
            to.createRelationshipTo(clone, FountainRelationships.CHILD);
            final Relationship ownerRel = clone.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
            ownerRel.delete();
            clone.createRelationshipTo(newOwner, FountainRelationships.OWNER);
            recalculatePoolURIs(clone);
            indexDAO.incrementBoardActivity(to);

            return clone;
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public Node linkPoolObject(LiquidSessionIdentifier editor, Node newOwner, Node target, Node from, Node to) throws InterruptedException {
        fountainNeo.begin();
        try {
            return linkPoolObject(editor, newOwner, target, to);
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public Node unlinkPoolObject(Node target) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (target.hasRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING)) {
                throw new CannotUnlinkEntityException("Cannot unlink a node that is not the latest version.");
            }
            Iterable<Relationship> relationships = target.getRelationships(FountainRelationships.CHILD, Direction.INCOMING);
            for (Relationship relationship : relationships) {
                relationship.delete();
            }
            Iterable<Relationship> linkedRelationships = target.getRelationships(FountainRelationships.LINKED_CHILD, Direction.INCOMING);
            for (Relationship relationship : linkedRelationships) {
                relationship.delete();
            }
            fountainNeo.getIndexService().remove(target, FountainNeo.URI);
            assertHasOwner(target);
            return target;
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public Node linkPool(Node newOwner, Node target, Node to) throws InterruptedException {
        fountainNeo.begin();
        try {
            String candidateURI = to.getProperty(FountainNeo.URI) + "#" + target.getProperty(FountainNeo.NAME);
            String uri = candidateURI;
            int count = 1;
            while (fountainNeo.findByURI(new LiquidURI(uri)) != null) {
                uri = candidateURI + count++;
            }
            target.setProperty(FountainNeo.URI, uri);
            fountainNeo.reindex(target, FountainNeo.URI, FountainNeo.URI);
            to.createRelationshipTo(target, FountainRelationships.LINKED_CHILD);
//            target.createRelationshipTo(newOwner, FountainRelationships.OWNER);
            assertHasOwner(target);
            indexDAO.incrementBoardActivity(to);
            indexDAO.incrementBoardActivity(target);

            return target;
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public Node linkPool(Node newOwner, Node target, Node from, Node to) throws InterruptedException {
        fountainNeo.begin();
        try {
            Node clone = linkPool(newOwner, target, to);
            unlinkPool(target);
            assertHasOwner(clone);

            return clone;
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public Node unlinkPool(Node target) throws InterruptedException {
        fountainNeo.begin();
        try {
            Iterable<Relationship> relationships = target.getRelationships(FountainRelationships.CHILD, Direction.INCOMING);
            for (Relationship relationship : relationships) {
                relationship.delete();
            }
            Iterable<Relationship> linkedRelationships = target.getRelationships(FountainRelationships.LINKED_CHILD, Direction.INCOMING);
            for (Relationship relationship : linkedRelationships) {
                relationship.delete();
            }
            assertHasOwner(target);

            fountainNeo.getIndexService().remove(target, FountainNeo.URI);
            indexDAO.incrementBoardActivity(target);

            return target;
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public LSDEntity createPoolObjectTx(Node poolNode, LiquidSessionIdentifier identity, LiquidURI owner, LiquidURI author, LSDEntity entity, LiquidRequestDetailLevel detail, boolean internal, boolean createAuthor) throws Exception {
        if (owner == null) {
            throw new NullPointerException("Tried to create a pool without an owner.");
        }
        fountainNeo.begin();
        try {
            Node poolObject = createPoolObjectNoTx(identity, poolNode, entity, owner, author, createAuthor);
            assertHasOwner(poolObject);
            Node parent = fountainNeo.parentNode(poolObject);
//            recalculateCentreImage(parent, poolObject);
            return convertNodeToEntityWithRelatedEntitiesNoTX(identity, poolObject, parent, detail, internal, false);
        } finally {
            fountainNeo.end();
        }
    }

    private void assertHasOwner(Node poolObject) {
        Relationship ownerRel = poolObject.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
        if (ownerRel == null) {
            throw new IllegalStateException("We have a pool object with no owner.");
        }
    }

    @Override
    public LSDEntity getPoolAndContentsNoTx(final Node targetNode, final LiquidRequestDetailLevel detail, boolean contents, ChildSortOrder order, final boolean internal, final LiquidSessionIdentifier identity, Integer start, Integer end, boolean historical) throws Exception {
        fountainNeo.begin();
        try {
            final Node pool = convertToPoolFromPoolOrObject(targetNode);
            final LSDEntity entity = convertNodeToEntityWithRelatedEntitiesNoTX(identity, pool, null, detail, internal, historical);
            if (contents) {
                final List<LSDEntity> entities = new ArrayList<LSDEntity>();
                int count = 0;
                fountainNeo.forEachChild(pool, new NodeCallback() {
                    public void call(Node child) throws Exception {
                        LSDEntity poolObjectEntity = convertNodeToEntityWithRelatedEntitiesNoTX(identity, child, pool, detail, internal, false);
                        if (targetNode.equals(child)) {
                            poolObjectEntity.setAttribute(LSDAttribute.HAS_FOCUS, "true");
                        }
                        if (fountainNeo.isAuthorized(child, identity, LiquidPermission.VIEW)) {
                            entities.add(poolObjectEntity);
                        }
                        poolObjectEntity.setAttribute(LSDAttribute.POPULARITY_METRIC, String.valueOf(fountainNeo.popularity(child)));
                    }
                });
                //Put the result into descending time order.
                SortUtil.sort(entities, order);
                if (end == null) {
                    end = entities.size() - 1;
                }
                if (start == null) {
                    start = 0;
                }
                entity.addSubEntities(LSDAttribute.CHILD, entities.subList(start, end >= entities.size() ? entities.size() : end + 1));
                entity.setAttribute(LSDAttribute.POPULARITY_METRIC, String.valueOf(fountainNeo.popularity(targetNode)));
                indexDAO.addMetrics(pool, entity);
            }
            return entity;
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public LSDEntity getPoolAndContentsNoTx(LiquidURI uri, LiquidRequestDetailLevel detail, ChildSortOrder order, boolean contents, boolean internal, LiquidSessionIdentifier identity, Integer start, Integer end, boolean historical) throws Exception {
        fountainNeo.begin();
        try {
            Node node = fountainNeo.findByURI(uri);
            if (node == null) {
                return null;
            }
            return getPoolAndContentsNoTx(node, detail, contents, order, internal, identity, start, end, historical);
        } finally {
            fountainNeo.end();
        }
    }

    public LSDEntity getPoolAndContentsNoTX(LiquidUUID target, LiquidRequestDetailLevel detail, ChildSortOrder order, boolean contents, boolean internal, LiquidSessionIdentifier identity, Integer start, Integer end, boolean historical) throws Exception {
        fountainNeo.begin();
        try {
            Node node = fountainNeo.findByUUID(target);
            if (node == null) {
                return null;
            }
            return getPoolAndContentsNoTx(node, detail, contents, order, internal, identity, start, end, historical);
        } finally {
            fountainNeo.end();
        }
    }

    public LSDEntity deletePoolObjectTx(LiquidUUID target, boolean internal, LiquidRequestDetailLevel detail) throws Exception {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.getNeo().beginTx();
            try {
                Node node = fountainNeo.findByUUID(target);
                return deletePoolObjectNoTx(internal, detail, transaction, node);
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } finally {
            fountainNeo.end();
        }

    }

    @Override
    public LSDEntity deletePoolObjectTx(LiquidURI uri, boolean internal, LiquidRequestDetailLevel detail) throws Exception {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.getNeo().beginTx();
            try {
                Node node = fountainNeo.findByURI(uri);
                return deletePoolObjectNoTx(internal, detail, transaction, node);
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }

        } finally {
            fountainNeo.end();
        }

    }

    LSDEntity deletePoolObjectNoTx(boolean internal, LiquidRequestDetailLevel detail, Transaction transaction, Node node) throws Exception {
        if (fountainNeo.isDeleted(node)) {
            throw new DeletedEntityException("The entity %s is already deleted so cannot be deleted again.", node.hasProperty(FountainNeo.URI) ? node.getProperty(FountainNeo.URI) : "<unknown-uri>");
        }
        fountainNeo.delete(node);
        final Relationship relationship = node.getSingleRelationship(FountainRelationships.CHILD, Direction.INCOMING);
        if (relationship == null) {
            throw new OrphanedEntityException("The entity %s is orphaned so cannot be deleted.", node.hasProperty(FountainNeo.URI) ? node.getProperty(FountainNeo.URI) : "<unknown-uri>");
        }
//        recalculateCentreImage(relationship.getOtherNode(node), node);
        transaction.success();
        return fountainNeo.convertNodeToLSD(node, detail, internal);
    }

    @Override
    public void visitNodeNoTx(Node node, LiquidSessionIdentifier identity) throws InterruptedException {
        fountainNeo.begin();
        try {
            LiquidUUID session = identity.getSession();
            Node sessionNode = fountainNeo.findByUUID(session);
            for (Relationship relationship : sessionNode.getRelationships(FountainRelationships.VISITING, Direction.OUTGOING)) {
                relationship.delete();
            }
            Node pool = convertToPoolFromPoolOrObject(node);
            final Relationship relationshipTo = sessionNode.createRelationshipTo(pool, FountainRelationships.VISITING);
            relationshipTo.setProperty(LSDAttribute.UPDATED.getKeyName(), String.valueOf(System.currentTimeMillis()));
            sessionNode.setProperty(LSDAttribute.ACTIVE.getKeyName(), "true");
            fountainNeo.timestamp(sessionNode);
            indexDAO.incrementBoardActivity(node);
            indexDAO.visitBoard(node, identity.getAliasURL());
        } finally {
            fountainNeo.end();
        }
    }

    Node convertToPoolFromPoolOrObject(Node node) {
        Node pool;
        if (node.getProperty(FountainNeo.TYPE).toString().startsWith(LSDDictionaryTypes.POOL.getValue())) {
            pool = node;
        } else {
            pool = fountainNeo.parentNode(node);
        }
        return pool;
    }

    @Override
    public LSDEntity selectPoolObjectTx(LiquidSessionIdentifier identity, boolean selected, LiquidURI target, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            LSDEntity newObject;
            final Transaction transaction = fountainNeo.getNeo().beginTx();
            try {
                Node node = fountainNeo.findByURI(target, true);
                node.setProperty(LSDAttribute.SELECTED.getKeyName(), selected ? "true" : "false");
                newObject = convertNodeToEntityWithRelatedEntitiesNoTX(identity, node, null, detail, internal, false);
                indexDAO.incrementBoardActivity(node);
                transaction.success();
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
            return newObject;
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public Node movePoolObjectNoTx(LiquidURI object, Double x, Double y, Double z) throws Exception {
        fountainNeo.begin();
        try {
            Node node = fountainNeo.findByURI(object);
            Relationship relationship = node.getSingleRelationship(FountainRelationships.VIEW, Direction.OUTGOING);
            if (relationship == null) {
                throw new RelationshipNotFoundException("No view relationship for %s(%s)", node.getProperty(FountainNeo.URI), object);
            }
            Node viewNode = relationship.getOtherNode(node);
            if (viewNode == null) {
                throw new EntityNotFoundException("No view relationship for %s(%s)", node.getProperty(FountainNeo.URI), object);
            }
            if (x != null) {
                viewNode.setProperty(LSDAttribute.VIEW_X.getKeyName(), x.toString());
            }
            if (y != null) {
                viewNode.setProperty(LSDAttribute.VIEW_Y.getKeyName(), y.toString());
            }
            if (z != null) {
                viewNode.setProperty(LSDAttribute.VIEW_Z.getKeyName(), z.toString());
            }

            viewNode.setProperty(FountainNeo.VIEW_RADIUS, String.valueOf(fountainNeo.calculateRadius(node)));
            final Relationship parentRel = node.getSingleRelationship(FountainRelationships.CHILD, Direction.INCOMING);
            if (parentRel == null) {
                throw new OrphanedEntityException("The entity %s (%s) is orphaned so cannot be moved.", object.toString(), node.hasProperty(FountainNeo.URI) ? node.getProperty(FountainNeo.URI) : "<unknown-uri>");
            }
//            recalculateCentreImage(parentRel.getOtherNode(node), node);
            indexDAO.incrementBoardActivity(parentRel.getOtherNode(node));

            return viewNode;
        } finally {
            fountainNeo.end();
        }
    }


    public LSDEntity convertNodeToEntityWithRelatedEntitiesNoTX(LiquidSessionIdentifier identity, Node node, Node parent, LiquidRequestDetailLevel detail, boolean internal, boolean historical) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (node == null) {
                return null;
            }
            LSDEntity entity = fountainNeo.convertNodeToLSD(node, detail, internal);
            boolean done = false;
            final LiquidRequestDetailLevel aliasDetailLevel = detail == LiquidRequestDetailLevel.COMPLETE ? LiquidRequestDetailLevel.COMPLETE : LiquidRequestDetailLevel.PERSON_MINIMAL;

            Iterable<Relationship> iterable = node.getRelationships(VIEW, Direction.OUTGOING);
            for (Relationship relationship : iterable) {
                if (done) {
                    throw new DuplicateEntityException("Found a second view for a single object.");
                }
                entity.addSubEntity(LSDAttribute.VIEW, fountainNeo.convertNodeToLSD(relationship.getOtherNode(node), detail, internal), true);
                done = true;
            }

            if (detail == LiquidRequestDetailLevel.COMPLETE || detail == LiquidRequestDetailLevel.NORMAL || detail == LiquidRequestDetailLevel.BOARD_LIST) {
                Relationship ownerRel = node.getSingleRelationship(OWNER, Direction.OUTGOING);
                if (ownerRel != null) {
                    entity.addSubEntity(LSDAttribute.OWNER, userDAO.getAliasFromNode(ownerRel.getOtherNode(node), internal, aliasDetailLevel), true);
                }

                if (detail != LiquidRequestDetailLevel.BOARD_LIST) {
                    Relationship relationship = node.getSingleRelationship(FountainRelationships.AUTHOR, Direction.OUTGOING);
                    if (relationship != null) {
                        entity.addSubEntity(LSDAttribute.AUTHOR, userDAO.getAliasFromNode(relationship.getOtherNode(node), internal, aliasDetailLevel), true);
                    }

                    Relationship editorRel = node.getSingleRelationship(EDITOR, Direction.OUTGOING);
                    if (editorRel != null) {
                        entity.addSubEntity(LSDAttribute.EDITOR, userDAO.getAliasFromNode(editorRel.getOtherNode(node), internal, aliasDetailLevel), true);
                    }
                }

            }

            if (historical) {
                List<LSDEntity> history = new ArrayList<LSDEntity>();
                Node version = node;
                while (version.hasRelationship(VERSION_PARENT, Direction.OUTGOING)) {
                    version = version.getSingleRelationship(VERSION_PARENT, Direction.OUTGOING).getOtherNode(version);
                    history.add(fountainNeo.convertNodeToLSD(version, detail, false));
                }
                entity.addSubEntities(LSDAttribute.HISTORY, history);
            }

            fountainNeo.setPermissionFlagsOnEntity(identity, node, parent, entity);
            return entity;
        } finally {
            fountainNeo.end();
        }
    }

    public Node addCommentNoTX(Node targetNode, LSDEntity entity, LiquidURI author) throws InterruptedException {
        fountainNeo.begin();
        try {
            LSDEntity entityCopy = entity.copy();
            if (author == null) {
                throw new NullPointerException("Null author passed to addComment().");
            }
            if (targetNode == null) {
                throw new NullPointerException("Null node passed to addComment().");
            }
            entityCopy.removeSubEntity(LSDAttribute.AUTHOR);
            final Node commentNode = fountainNeo.createNode();
            fountainNeo.mergeProperties(commentNode, entityCopy, false, false, null);
            commentNode.setProperty(FountainNeo.TYPE, LSDDictionaryTypes.COMMENT.getValue());
            fountainNeo.setIDIfNotSetOnNode(commentNode);
            String name = entityCopy.getAttribute(LSDAttribute.NAME);
            if (name == null) {
                name = entityCopy.getTypeDef().getPrimaryType().getGenus().toLowerCase() + System.currentTimeMillis();
                entityCopy.setAttribute(LSDAttribute.NAME, name);
            }
            String uri = targetNode.getProperty(FountainNeo.URI) + "~" + "comment-" + author.getSubURI().getSubURI() + System.currentTimeMillis();
            commentNode.setProperty(FountainNeo.URI, uri);

            if (targetNode.hasRelationship(COMMENT, Direction.OUTGOING)) {
                final Iterable<Relationship> comments = targetNode.getRelationships(COMMENT, Direction.OUTGOING);
                for (Relationship relationship : comments) {
                    final Node previous = relationship.getEndNode();
                    relationship.delete();
                    final Relationship previousRel = commentNode.createRelationshipTo(previous, PREVIOUS);
                }
            }
            targetNode.createRelationshipTo(commentNode, COMMENT);
            Node ownerNode = fountainNeo.findByURI(author);
            commentNode.createRelationshipTo(ownerNode, OWNER);
            commentNode.createRelationshipTo(ownerNode, CREATOR);
            commentNode.createRelationshipTo(ownerNode, EDITOR);
            userDAO.addAuthorToNodeNoTX(author, false, commentNode);
            fountainNeo.inheritPermissions(targetNode, commentNode);
            fountainNeo.indexBy(commentNode, FountainNeo.ID, FountainNeo.ID, true);
            fountainNeo.indexBy(commentNode, FountainNeo.URI, FountainNeo.URI, true);
            fountainNeo.timestamp(commentNode);


            int commentCount;
            if (targetNode.hasProperty(LSDAttribute.COMMENT_COUNT.getKeyName())) {
                commentCount = Integer.parseInt(targetNode.getProperty(LSDAttribute.COMMENT_COUNT.getKeyName()).toString()) + 1;
            } else {
                commentCount = getCommentTraverser(targetNode, FountainNeo.MAX_COMMENTS_DEFAULT).getAllNodes().size();
            }
            log.debug("Comment count is now {0}", commentCount);
            targetNode.setProperty(LSDAttribute.COMMENT_COUNT.getKeyName(), String.valueOf(commentCount));
            indexDAO.syncCommentCount(targetNode);
            indexDAO.incrementBoardActivity(targetNode);
            return commentNode;
        } finally {
            fountainNeo.end();
        }
    }


    @Override
    public void createPoolsForUserNoTx(String username) throws InterruptedException {
        fountainNeo.begin();
        try {
            Node userParentPool;

            /**
             * The user pool, is being reserved for now, we may remove it later.
             */
            userParentPool = fountainNeo.findByURI(new LiquidURI("pool:///users"));

            LiquidURI cazcadeAliasURI = new LiquidURI("alias:cazcade:" + username);
            if (userParentPool == null) {
                userParentPool = createPoolNoTx(new LiquidSessionIdentifier(FountainNeo.SYSTEM, null), cazcadeAliasURI, fountainNeo.getRootPool(), "users", 0, 0, null, false);
            }
            Node userPool = createPoolNoTx(new LiquidSessionIdentifier(FountainNeo.SYSTEM, null), cazcadeAliasURI, userParentPool, username, 0, 0, null, false);
        } finally {
            fountainNeo.end();
        }

    }


    @Override
    public void createPoolsForAliasNoTx(LiquidURI aliasURI, String name, String fullName, boolean systemUser) throws InterruptedException {
        fountainNeo.begin();
        try {

            if (aliasURI.asString().startsWith("alias:cazcade:") && !systemUser) {
                LiquidSessionIdentifier sessionIdentifier = new LiquidSessionIdentifier(name, null);

                Node userPool = createPoolNoTx(FountainNeo.SYSTEM_FAKE_SESSION, aliasURI, fountainNeo.getPeoplePool(), name, 0, 0, null, false);
                userPool.setProperty(FountainNeo.PERMISSIONS, FountainNeo.minimalPermissionNoDeleteValue);

                Node dockPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".dock", 0, 0, null, false);
                dockPool.setProperty(FountainNeo.PERMISSIONS, FountainNeo.minimalPermissionNoDeleteValue);

                Node clipBoardPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".clipboard", 0, 0, null, false);
                clipBoardPool.setProperty(FountainNeo.PERMISSIONS, FountainNeo.minimalPermissionNoDeleteValue);

                Node streamPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "stream", 210, -210, null, false);
                streamPool.setProperty(FountainNeo.DESCRIPTION, "The feeds that make up your stream go here.");
                streamPool.setProperty(FountainNeo.PERMISSIONS, FountainNeo.privatePermissionNoDeleteValue);
                streamPool.setProperty(FountainNeo.PINNED, "true");

                LSDSimpleEntity streamFeedExplanation = LSDSimpleEntity.createEmpty();
                streamFeedExplanation.setType(LSDDictionaryTypes.HTML_FRAGMENT);
                streamFeedExplanation.setAttribute(LSDAttribute.NAME, "stream_feed_explanation");
                streamFeedExplanation.setAttribute(LSDAttribute.TEXT_EXTENDED, "This is where your web feeds are kept. These feeds create your stream. You can manage them in the same way as anywhere else.");
                streamFeedExplanation.setAttribute(LSDAttribute.DESCRIPTION, "Cazcade's company blog.");
                createPoolObjectNoTx(sessionIdentifier, streamPool, streamFeedExplanation, aliasURI, aliasURI, false);

                LSDSimpleEntity defaultFeedEntity = LSDSimpleEntity.createEmpty();
                defaultFeedEntity.setType(LSDDictionaryTypes.RSS_FEED);
                defaultFeedEntity.setAttribute(LSDAttribute.NAME, "default_cazcade_feed");
                defaultFeedEntity.setAttribute(LSDAttribute.SOURCE, "http://blog.cazcade.com/feed/");
                defaultFeedEntity.setAttribute(LSDAttribute.TITLE, "Cazcade Blog");
                defaultFeedEntity.setAttribute(LSDAttribute.DESCRIPTION, "Cazcade's company blog.");
                createPoolObjectNoTx(sessionIdentifier, streamPool, defaultFeedEntity, aliasURI, aliasURI, false);

                Node trashPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".trash", 0, 0, null, false);
                trashPool.setProperty(FountainNeo.PERMISSIONS, FountainNeo.minimalPermissionNoDeleteValue);

                Node inbox = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".inbox", 0, 0, null, false);
                inbox.setProperty(FountainNeo.TITLE, "Inbox");
                inbox.setProperty(FountainNeo.DESCRIPTION, "Your inbox.");
                inbox.setProperty(FountainNeo.PERMISSIONS, FountainNeo.privatePermissionNoDeleteValue);

                Node publicPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "public", -210, -210, null, false);
                publicPool.setProperty(FountainNeo.PERMISSIONS, FountainNeo.publicPermissionNoDeleteValue);
                publicPool.setProperty(FountainNeo.TITLE, fullName + "'s Public Board");
                publicPool.setProperty(FountainNeo.DESCRIPTION, "Anyone can modify this.");
                publicPool.setProperty(FountainNeo.PINNED, "true");

                Node sharedPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "friends", -210, 210, null, false);
                sharedPool.setProperty(FountainNeo.PERMISSIONS, FountainNeo.sharedPermissionNoDeleteValue);
                sharedPool.setProperty(FountainNeo.TITLE, fullName + "'s Friends Board");
                sharedPool.setProperty(FountainNeo.DESCRIPTION, "Friends can modify this.");
                sharedPool.setProperty(FountainNeo.PINNED, "true");

                Node privatePool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "private", 210, 210, null, false);
                privatePool.setProperty(FountainNeo.TITLE, fullName + "'s Private Board");
                privatePool.setProperty(FountainNeo.DESCRIPTION, "Only you can view this.");
                privatePool.setProperty(FountainNeo.PERMISSIONS, FountainNeo.privateSharedPermissionNoDeleteValue);
                privatePool.setProperty(FountainNeo.PINNED, "true");


                Node profilePool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "profile", 0, 0, null, true);
                profilePool.setProperty(FountainNeo.TITLE, fullName + "'s Profile Board");
                profilePool.setProperty(FountainNeo.DESCRIPTION, "This is all about you.");
                profilePool.setProperty(FountainNeo.PINNED, "true");
                profilePool.setProperty(FountainNeo.PERMISSIONS, FountainNeo.defaultPermissionNoDeleteValue);
            }
        } finally {
            fountainNeo.end();
        }

    }

    @Override
    public void createPoolsForCazcadeAliasNoTx(String name, String fullName, boolean systemUser) throws InterruptedException {
        createPoolsForAliasNoTx(new LiquidURI("alias:cazcade:" + name), name, fullName, systemUser);
    }


    public Collection<LSDEntity> getCommentsTx(LiquidSessionIdentifier identity, LiquidURI uri, int max, boolean internal, LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            List<LSDEntity> comments = new ArrayList<LSDEntity>();
            final Transaction transaction = fountainNeo.beginTx();
            try {
                Node node = fountainNeo.findByURI(uri);
                if (node == null) {
                    return null;
                }
                final Traverser traverser = getCommentTraverser(node, max);

                for (Node comment : traverser) {
                    comments.add(convertNodeToEntityWithRelatedEntitiesNoTX(identity, comment, null, detail, internal, false));
                }
                SortUtil.dateSortEntities(comments);
                return comments;
            } catch (RuntimeException e) {
                transaction.failure();
                throw e;
            } finally {
                transaction.finish();
            }
        } finally {
            fountainNeo.end();
        }
    }

    Traverser getCommentTraverser(Node node, final int max) {
        final int[] count = new int[1];
        return node.traverse(Traverser.Order.DEPTH_FIRST, new StopEvaluator() {
                    @Override
                    public boolean isStopNode(TraversalPosition currentPos) {
                        return count[0]++ >= max;
                    }
                }, new ReturnableEvaluator() {
                    public boolean isReturnableNode(TraversalPosition currentPos) {
                        return currentPos.currentNode().getProperty(FountainNeo.TYPE).equals(LSDDictionaryTypes.COMMENT.getValue());
                    }
                }, VERSION_PARENT, Direction.OUTGOING, COMMENT, Direction.OUTGOING, PREVIOUS, Direction.OUTGOING
        );
    }

}