package cazcade.fountain.datastore.impl;

import cazcade.common.Logger;
import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.Relationship;
import cazcade.fountain.datastore.api.*;
import cazcade.liquid.api.*;
import cazcade.liquid.api.lsd.*;
import cazcade.liquid.impl.SortUtil;
import org.neo4j.graphdb.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import static cazcade.fountain.datastore.impl.FountainRelationships.*;

public class FountainPoolDAOImpl implements FountainPoolDAO {

    @Nonnull
    private final static Logger log = Logger.getLogger(FountainPoolDAOImpl.class);

    @Autowired
    private FountainNeo fountainNeo;


    @Autowired
    private FountainIndexServiceImpl indexDAO;


    @Autowired
    private FountainUserDAO userDAO;


    public FountainPoolDAOImpl() {


    }

    public void recalculatePoolURIs(@Nonnull final Node node) throws InterruptedException {
        fountainNeo.recalculateURI(node);
        final Traverser traverser = node.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return currentPos.currentNode().hasProperty(LSDAttribute.URI.getKeyName());
            }
        }, FountainRelationships.CHILD, Direction.OUTGOING);
        for (final org.neo4j.graphdb.Node childNode : traverser) {
            fountainNeo.recalculateURI(new Node(childNode));
        }
    }

    @Nullable
    @Override
    public LSDEntity updatePool(@Nonnull final LiquidSessionIdentifier sessionIdentifier, @Nonnull final Node node, final LiquidRequestDetailLevel detail, final boolean internal, final boolean historical, final Integer end, final int start, final ChildSortOrder order, final boolean contents, @Nonnull final LSDEntity requestEntity, final Runnable onRenameAction) throws Exception {
        final Node resultNode = fountainNeo.updateNodeAndReturnNodeNoTx(sessionIdentifier, node, requestEntity, onRenameAction);
        indexDAO.syncBoard(node);
        return getPoolAndContentsNoTx(resultNode, detail, contents, order, internal, sessionIdentifier, start, end, historical);
    }


    @Nonnull
    @Override
    public Node createPoolNoTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidURI owner, final Node parent, @Nonnull final String poolName, final double x, final double y, @Nullable final String title, final boolean listed) throws InterruptedException {
        return createPoolNoTx(identity, owner, parent, LSDDictionaryTypes.POOL2D, poolName, x, y, title, listed);

    }

    @Nonnull
    @Override
    public Node createPoolNoTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidURI owner, @Nullable final Node parent, @Nonnull final LSDType type, @Nonnull final String poolName, final double x, final double y, @Nullable final String title, final boolean listed) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (parent == null) {
                throw new DataStoreException("Tried to create a pool with a null parent node.");
            }
            fountainNeo.assertAuthorized(parent, identity, LiquidPermission.MODIFY, LiquidPermission.VIEW);
            final Node node = fountainNeo.createNode();
            node.setIDIfNotSetOnNode();
            node.setAttribute(LSDAttribute.LISTED, listed);

            String parentURI = parent.getProperty(LSDAttribute.URI);
            if (!parentURI.endsWith("/")) {
                parentURI += "/";
            }
            final String newURI = parentURI + poolName.toLowerCase();
            node.setProperty(LSDAttribute.URI, newURI);
            node.setProperty(LSDAttribute.NAME, poolName);
            if (title != null) {
                node.setProperty(LSDAttribute.TITLE, title);
            }
            node.setProperty(LSDAttribute.TYPE, type.asString());
            if (!parent.hasAttribute(LSDAttribute.PERMISSIONS)) {
                throw new DataStoreException("The parent pool %s had no permissions, all pools must have permissions.", parentURI);
            }
            node.inheritPermissions(parent);
            parent.createRelationshipTo(node, FountainRelationships.CHILD);
            final Node ownerNode = fountainNeo.findByURI(owner);
            node.createRelationshipTo(ownerNode, FountainRelationships.OWNER);
            node.createRelationshipTo(ownerNode, FountainRelationships.CREATOR);
            node.createRelationshipTo(ownerNode, FountainRelationships.EDITOR);
            final LSDSimpleEntity view = LSDSimpleEntity.createEmpty();
            view.setAttribute(LSDAttribute.VIEW_X, String.valueOf(x));
            view.setAttribute(LSDAttribute.VIEW_Y, String.valueOf(y));
            view.setAttribute(LSDAttribute.VIEW_WIDTH, "200");
            view.setAttribute(LSDAttribute.VIEW_HEIGHT, "200");
            createView(node, view);
            userDAO.addAuthorToNodeNoTX(owner, false, node);
            fountainNeo.indexBy(node, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(node, LSDAttribute.URI, LSDAttribute.URI, true);
            node.timestamp();
            assertHasOwner(node);
            indexDAO.syncBoard(node);
            return node;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    public Node createPoolObjectNoTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final Node pool, @Nonnull final LSDEntity entity, @Nonnull final LiquidURI owner, @Nullable final LiquidURI author, final boolean createAuthor) throws InterruptedException {
        fountainNeo.begin();
        try {
            fountainNeo.assertAuthorized(pool, identity, LiquidPermission.MODIFY, LiquidPermission.VIEW);
            final LSDEntity entityCopy = entity.copy();
            //We shouldn't be using the ID supplied to us.
            entityCopy.removeCompletely(LSDAttribute.ID);
            String name = entityCopy.getAttribute(LSDAttribute.NAME);
            if (name == null) {
                name = entityCopy.getTypeDef().getPrimaryType().getGenus().toLowerCase() + System.currentTimeMillis();
                entityCopy.setAttribute(LSDAttribute.NAME, name);
            }
            final LSDEntity viewEntity = entityCopy.removeSubEntity(LSDAttribute.VIEW);
            viewEntity.setAttribute(LSDAttribute.ID, "");
            if (author == null) {
                throw new NullPointerException("Null author passed to createPoolObjectNoTx().");
            }

            final Node node = fountainNeo.createNode();
            node.mergeProperties(entityCopy, false, false, null);
            fountainNeo.freeTextIndexNoTx(node);

            node.setIDIfNotSetOnNode();
            final String uri = pool.getProperty(LSDAttribute.URI) + "#" + name.toLowerCase();
            node.setProperty(LSDAttribute.URI, uri);
            pool.createRelationshipTo(node, FountainRelationships.CHILD);
            final Node ownerNode = fountainNeo.findByURI(owner);
            node.createRelationshipTo(ownerNode, FountainRelationships.OWNER);
            node.createRelationshipTo(ownerNode, FountainRelationships.CREATOR);
            node.createRelationshipTo(ownerNode, FountainRelationships.EDITOR);
            userDAO.addAuthorToNodeNoTX(author, createAuthor, node);
            node.inheritPermissions(pool);
            fountainNeo.indexBy(node, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(node, LSDAttribute.URI, LSDAttribute.URI, true);
            node.timestamp();
            final Node view = createView(node, viewEntity);
            assertHasOwner(node);
            indexDAO.incrementBoardActivity(pool);
            return node;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    Node createView(@Nonnull final Node object, @Nonnull final LSDEntity viewEntity) throws InterruptedException {
        final Node node = fountainNeo.createNode();
        node.setProperty(LSDAttribute.TYPE, LSDDictionaryTypes.VIEW.getValue());
        node.mergeProperties(viewEntity, false, false, null);
        fountainNeo.freeTextIndexNoTx(node);
        node.setIDIfNotSetOnNode();
        final String uri = object.getProperty(LSDAttribute.URI) + ":view";
        node.setProperty(LSDAttribute.URI, uri);
        object.createRelationshipTo(node, FountainRelationships.VIEW);
        node.setProperty(LSDAttribute.PERMISSIONS, object.getProperty(LSDAttribute.PERMISSIONS));
        node.setAttribute(LSDAttribute.VIEW_RADIUS, node.calculateRadius());
        if (!node.hasAttribute(LSDAttribute.VIEW_X)) {
            node.setAttribute(LSDAttribute.VIEW_X, Math.random() * 100 - 50);
        }

        if (!node.hasAttribute(LSDAttribute.VIEW_Y)) {
            node.setAttribute(LSDAttribute.VIEW_Y, (Math.random() * 100) - 50);
        }

        fountainNeo.indexBy(node, LSDAttribute.ID, LSDAttribute.ID, true);
        fountainNeo.indexBy(node, LSDAttribute.URI, LSDAttribute.URI, true);
        node.timestamp();
        return node;
    }

    @Nonnull
    Node copyPoolObjectForUpdate(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final Node node, final boolean fork) throws InterruptedException {
        node.assertLatestVersion();
        return fountainNeo.cloneNodeForNewVersion(editor, node, fork);
    }

    @Nullable
    @Override
    public LSDEntity updatePoolObjectNoTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidSessionIdentifier editor, @Nonnull final LSDEntity entity, @Nullable final Node pool, @Nonnull final Node origNode, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {

        final LSDEntity entityCopy = entity.copy();
        entityCopy.removeSubEntity(LSDAttribute.VIEW);
        entityCopy.removeSubEntity(LSDAttribute.AUTHOR);
        entityCopy.removeSubEntity(LSDAttribute.OWNER);
        entityCopy.removeValue(LSDAttribute.ID);
        final LSDEntity newObject;

        final Node node = copyPoolObjectForUpdate(editor, origNode, false);
        if (!entity.getURI().toString().toLowerCase().equals(origNode.getAttribute(LSDAttribute.URI))) {
            throw new CannotChangeURIException("Tried to change the URI of %s to %s", origNode.getAttribute(LSDAttribute.URI), entity.getURI().toString());
        }
        newObject = convertNodeToEntityWithRelatedEntitiesNoTX(identity, node.mergeProperties(entityCopy, true, false, new Runnable() {
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
        fountainNeo.freeTextIndexNoTx(node);
        return newObject;

    }

    @Nullable
    @Override
    public LSDEntity getPoolObjectTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidURI uri, final boolean internal, final boolean historical, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final Node node = fountainNeo.findByURI(uri);
                final Node parent = fountainNeo.findByURI(uri.getWithoutFragmentOrComment());

                if (node == null) {
                    return null;
                }
                assertHasOwner(node);
                assertHasOwner(parent);
                return convertNodeToEntityWithRelatedEntitiesNoTX(identity, node.getLatestVersionFromFork(), parent, detail, internal, historical);
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

    @Nullable
    @Deprecated //Use URI based methods instead
    public LSDEntity getPoolObjectTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidUUID uuid, final boolean internal, final boolean historical, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final Node node = fountainNeo.findByUUID(uuid);
                if (node == null) {
                    return null;
                }
                return convertNodeToEntityWithRelatedEntitiesNoTX(identity, node.getLatestVersionFromFork(), null, detail, internal, historical);
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
    public LSDEntity linkPoolObjectTx(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LiquidURI newOwner, @Nonnull final LiquidURI target, @Nonnull final LiquidURI to, final LiquidRequestDetailLevel detail, final boolean internal) throws Exception {
        return fountainNeo.doInTransaction(new Callable<LSDEntity>() {
            @Nullable
            @Override
            public LSDEntity call() throws Exception {
                return convertNodeToEntityWithRelatedEntitiesNoTX(editor, linkPoolObject(editor, fountainNeo.findByURI(newOwner), fountainNeo.findByURI(target), fountainNeo.findByURI(to)),
                        null, detail, internal, false);
            }
        });
    }

    @Nonnull
    @Override
    public Node linkPoolObject(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final Node newOwner, @Nonnull final Node target, @Nonnull final Node to) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Node clone = fountainNeo.cloneNodeForNewVersion(editor, target, true);
            assertHasOwner(clone);
            final String candidateName = clone.getAttribute(LSDAttribute.NAME);
            String candidateURI = to.getAttribute(LSDAttribute.URI) + "#" + candidateName;
            String name;
            int count = 1;
            while (fountainNeo.findByURI(new LiquidURI(candidateURI)) != null) {
                name = candidateName + count++;
                candidateURI = to.getAttribute(LSDAttribute.URI) + "#" + name;
            }
            clone.setProperty(LSDAttribute.URI, candidateURI);
            fountainNeo.reindex(clone, LSDAttribute.URI, LSDAttribute.URI);
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

    @Nonnull
    @Override
    public Node linkPoolObject(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final Node newOwner, @Nonnull final Node target, final Node from, @Nonnull final Node to) throws InterruptedException {
        fountainNeo.begin();
        try {
            return linkPoolObject(editor, newOwner, target, to);
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    @Override
    public Node unlinkPoolObject(@Nonnull final Node target) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (target.hasRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING)) {
                throw new CannotUnlinkEntityException("Cannot unlink a node that is not the latest version.");
            }
            final Iterable<Relationship> relationships = target.getRelationships(FountainRelationships.CHILD, Direction.INCOMING);
            for (final Relationship relationship : relationships) {
                relationship.delete();
            }
            final Iterable<Relationship> linkedRelationships = target.getRelationships(FountainRelationships.LINKED_CHILD, Direction.INCOMING);
            for (final Relationship relationship : linkedRelationships) {
                relationship.delete();
            }
            fountainNeo.getIndexService().remove(target.getNeoNode(), LSDAttribute.URI.getKeyName());
            assertHasOwner(target);
            return target;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    @Override
    public Node linkPool(final Node newOwner, @Nonnull final Node target, @Nonnull final Node to) throws InterruptedException {
        fountainNeo.begin();
        try {
            final String candidateURI = to.getAttribute(LSDAttribute.URI) + "#" + target.getAttribute(LSDAttribute.NAME);
            String uri = candidateURI;
            int count = 1;
            while (fountainNeo.findByURI(new LiquidURI(uri)) != null) {
                uri = candidateURI + count++;
            }
            target.setProperty(LSDAttribute.URI, uri);
            fountainNeo.reindex(target, LSDAttribute.URI, LSDAttribute.URI);
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

    @Nonnull
    @Override
    public Node linkPool(final Node newOwner, @Nonnull final Node target, final Node from, @Nonnull final Node to) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Node clone = linkPool(newOwner, target, to);
            unlinkPool(target);
            assertHasOwner(clone);

            return clone;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    @Override
    public Node unlinkPool(@Nonnull final Node target) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Iterable<Relationship> relationships = target.getRelationships(FountainRelationships.CHILD, Direction.INCOMING);
            for (final Relationship relationship : relationships) {
                relationship.delete();
            }
            final Iterable<Relationship> linkedRelationships = target.getRelationships(FountainRelationships.LINKED_CHILD, Direction.INCOMING);
            for (final Relationship relationship : linkedRelationships) {
                relationship.delete();
            }
            assertHasOwner(target);

            fountainNeo.getIndexService().remove(target.getNeoNode(), LSDAttribute.URI.getKeyName());
            indexDAO.incrementBoardActivity(target);

            return target;
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable
    @Override
    public LSDEntity createPoolObjectTx(@Nonnull final Node poolNode, @Nonnull final LiquidSessionIdentifier identity, @Nullable final LiquidURI owner, final LiquidURI author, @Nonnull final LSDEntity entity, final LiquidRequestDetailLevel detail, final boolean internal, final boolean createAuthor) throws Exception {
        if (owner == null) {
            throw new NullPointerException("Tried to create a pool without an owner.");
        }
        fountainNeo.begin();
        try {
            final Node poolObject = createPoolObjectNoTx(identity, poolNode, entity, owner, author, createAuthor);
            assertHasOwner(poolObject);
            final Node parent = poolObject.parentNode();
//            recalculateCentreImage(parent, poolObject);
            return convertNodeToEntityWithRelatedEntitiesNoTX(identity, poolObject, parent, detail, internal, false);
        } finally {
            fountainNeo.end();
        }
    }

    private void assertHasOwner(@Nonnull final Node poolObject) {
        final Relationship ownerRel = poolObject.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
        if (ownerRel == null) {
            throw new IllegalStateException("We have a pool object with no owner.");
        }
    }

    @Nullable
    @Override
    public LSDEntity getPoolAndContentsNoTx(@Nonnull final Node targetNode, final LiquidRequestDetailLevel detail, final boolean contents, final ChildSortOrder order, final boolean internal, @Nonnull final LiquidSessionIdentifier identity, @Nullable Integer start, @Nullable Integer end, final boolean historical) throws Exception {
        fountainNeo.begin();
        try {
            final Node pool = convertToPoolFromPoolOrObject(targetNode);
            final LSDEntity entity = convertNodeToEntityWithRelatedEntitiesNoTX(identity, pool, null, detail, internal, historical);
            if (contents) {
                final List<LSDEntity> entities = new ArrayList<LSDEntity>();
                final int count = 0;
                pool.forEachChild(new NodeCallback() {
                    public void call(@Nonnull final Node child) throws Exception {
                        final LSDEntity poolObjectEntity = convertNodeToEntityWithRelatedEntitiesNoTX(identity, child, pool, detail, internal, false);
                        if (targetNode.equals(child)) {
                            poolObjectEntity.setAttribute(LSDAttribute.HAS_FOCUS, "true");
                        }
                        if (child.isAuthorized(identity, LiquidPermission.VIEW)) {
                            entities.add(poolObjectEntity);
                        }
                        poolObjectEntity.setAttribute(LSDAttribute.POPULARITY_METRIC, String.valueOf(child.popularity()));
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
                entity.setAttribute(LSDAttribute.POPULARITY_METRIC, String.valueOf(targetNode.popularity()));
                indexDAO.addMetrics(pool, entity);
            }
            return entity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable
    @Override
    public LSDEntity getPoolAndContentsNoTx(@Nonnull final LiquidURI uri, final LiquidRequestDetailLevel detail, final ChildSortOrder order, final boolean contents, final boolean internal, @Nonnull final LiquidSessionIdentifier identity, final Integer start, final Integer end, final boolean historical) throws Exception {
        fountainNeo.begin();
        try {
            final Node node = fountainNeo.findByURI(uri);
            if (node == null) {
                return null;
            }
            return getPoolAndContentsNoTx(node, detail, contents, order, internal, identity, start, end, historical);
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable
    public LSDEntity getPoolAndContentsNoTX(@Nonnull final LiquidUUID target, final LiquidRequestDetailLevel detail, final ChildSortOrder order, final boolean contents, final boolean internal, @Nonnull final LiquidSessionIdentifier identity, final Integer start, final Integer end, final boolean historical) throws Exception {
        fountainNeo.begin();
        try {
            final Node node = fountainNeo.findByUUID(target);
            if (node == null) {
                return null;
            }
            return getPoolAndContentsNoTx(node, detail, contents, order, internal, identity, start, end, historical);
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable
    public LSDEntity deletePoolObjectTx(@Nonnull final LiquidUUID target, final boolean internal, final LiquidRequestDetailLevel detail) throws Exception {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final Node node = fountainNeo.findByUUID(target);
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

    @Nullable
    @Override
    public LSDEntity deletePoolObjectTx(@Nonnull final LiquidURI uri, final boolean internal, final LiquidRequestDetailLevel detail) throws Exception {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final Node node = fountainNeo.findByURI(uri);
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

    @Nullable
    LSDEntity deletePoolObjectNoTx(final boolean internal, final LiquidRequestDetailLevel detail, @Nonnull final Transaction transaction, @Nonnull final Node node) throws Exception {
        if (node.isDeleted()) {
            throw new DeletedEntityException("The entity %s is already deleted so cannot be deleted again.", node.hasAttribute(LSDAttribute.URI) ? node.getAttribute(LSDAttribute.URI) : "<unknown-uri>");
        }
        fountainNeo.delete(node);
        final Relationship relationship = node.getSingleRelationship(FountainRelationships.CHILD, Direction.INCOMING);
        if (relationship == null) {
            throw new OrphanedEntityException("The entity %s is orphaned so cannot be deleted.", node.hasAttribute(LSDAttribute.URI) ? node.getAttribute(LSDAttribute.URI) : "<unknown-uri>");
        }
//        recalculateCentreImage(relationship.getOtherNode(node), node);
        transaction.success();
        return node.convertNodeToLSD(detail, internal);
    }

    @Override
    public void visitNodeNoTx(@Nonnull final Node node, @Nonnull final LiquidSessionIdentifier identity) throws InterruptedException {
        fountainNeo.begin();
        try {
            final LiquidUUID session = identity.getSession();
            final Node sessionNode = fountainNeo.findByUUID(session);
            for (final Relationship relationship : sessionNode.getRelationships(FountainRelationships.VISITING, Direction.OUTGOING)) {
                relationship.delete();
            }
            final Node pool = convertToPoolFromPoolOrObject(node);
            final Relationship relationshipTo = sessionNode.createRelationshipTo(pool, FountainRelationships.VISITING);
            relationshipTo.setProperty(LSDAttribute.UPDATED.getKeyName(), String.valueOf(System.currentTimeMillis()));
            sessionNode.setAttribute(LSDAttribute.ACTIVE, true);
            sessionNode.timestamp();
            indexDAO.incrementBoardActivity(node);
            indexDAO.visitBoard(node, identity.getAliasURL());
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    Node convertToPoolFromPoolOrObject(@Nonnull final Node node) {
        final Node pool;
        if (node.getAttribute(LSDAttribute.TYPE).startsWith(LSDDictionaryTypes.POOL.getValue())) {
            pool = node;
        } else {
            pool = node.parentNode();
        }
        return pool;
    }

    @Nullable
    @Override
    public LSDEntity selectPoolObjectTx(@Nonnull final LiquidSessionIdentifier identity, final boolean selected, @Nonnull final LiquidURI target, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            LSDEntity newObject;
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final Node node = fountainNeo.findByURI(target, true);
                node.setAttribute(LSDAttribute.SELECTED, selected);
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

    @Nonnull
    @Override
    public Node movePoolObjectNoTx(@Nonnull final LiquidURI object, @Nullable final Double x, @Nullable final Double y, @Nullable final Double z) throws Exception {
        fountainNeo.begin();
        try {
            final Node node = fountainNeo.findByURI(object);
            final Relationship relationship = node.getSingleRelationship(FountainRelationships.VIEW, Direction.OUTGOING);
            if (relationship == null) {
                throw new RelationshipNotFoundException("No view relationship for %s(%s)", node.getAttribute(LSDAttribute.URI), object);
            }
            final Node viewNode = relationship.getOtherNode(node);
            if (viewNode == null) {
                throw new EntityNotFoundException("No view relationship for %s(%s)", node.getAttribute(LSDAttribute.URI), object);
            }
            if (x != null) {
                viewNode.setProperty(LSDAttribute.VIEW_X, x.toString());
            }
            if (y != null) {
                viewNode.setProperty(LSDAttribute.VIEW_Y, y.toString());
            }
            if (z != null) {
                viewNode.setProperty(LSDAttribute.VIEW_Z, z.toString());
            }

            viewNode.setProperty(LSDAttribute.VIEW_RADIUS, String.valueOf(node.calculateRadius()));
            final Relationship parentRel = node.getSingleRelationship(FountainRelationships.CHILD, Direction.INCOMING);
            if (parentRel == null) {
                throw new OrphanedEntityException("The entity %s (%s) is orphaned so cannot be moved.", object.toString(), node.hasAttribute(LSDAttribute.URI) ? node.getAttribute(LSDAttribute.URI) : "<unknown-uri>");
            }
//            recalculateCentreImage(parentRel.getOtherNode(node), node);
            indexDAO.incrementBoardActivity(parentRel.getOtherNode(node));

            return viewNode;
        } finally {
            fountainNeo.end();
        }
    }


    @Nullable
    public LSDEntity convertNodeToEntityWithRelatedEntitiesNoTX(@Nonnull final LiquidSessionIdentifier identity, @Nullable final Node node, @Nullable final Node parent, final LiquidRequestDetailLevel detail, final boolean internal, final boolean historical) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (node == null) {
                return null;
            }
            final LSDEntity entity = node.convertNodeToLSD(detail, internal);
            boolean done = false;
            final LiquidRequestDetailLevel aliasDetailLevel = detail == LiquidRequestDetailLevel.COMPLETE ? LiquidRequestDetailLevel.COMPLETE : LiquidRequestDetailLevel.PERSON_MINIMAL;

            final Iterable<Relationship> iterable = node.getRelationships(VIEW, Direction.OUTGOING);
            for (final Relationship relationship : iterable) {
                if (done) {
                    throw new DuplicateEntityException("Found a second view for a single object.");
                }
                entity.addSubEntity(LSDAttribute.VIEW, relationship.getOtherNode(node).convertNodeToLSD(detail, internal), true);
                done = true;
            }

            if (detail == LiquidRequestDetailLevel.COMPLETE || detail == LiquidRequestDetailLevel.NORMAL || detail == LiquidRequestDetailLevel.BOARD_LIST) {
                final Relationship ownerRel = node.getSingleRelationship(OWNER, Direction.OUTGOING);
                if (ownerRel != null) {
                    entity.addSubEntity(LSDAttribute.OWNER, userDAO.getAliasFromNode(ownerRel.getOtherNode(node), internal, aliasDetailLevel), true);
                }

                if (detail != LiquidRequestDetailLevel.BOARD_LIST) {
                    final Relationship relationship = node.getSingleRelationship(FountainRelationships.AUTHOR, Direction.OUTGOING);
                    if (relationship != null) {
                        entity.addSubEntity(LSDAttribute.AUTHOR, userDAO.getAliasFromNode(relationship.getOtherNode(node), internal, aliasDetailLevel), true);
                    }

                    final Relationship editorRel = node.getSingleRelationship(EDITOR, Direction.OUTGOING);
                    if (editorRel != null) {
                        entity.addSubEntity(LSDAttribute.EDITOR, userDAO.getAliasFromNode(editorRel.getOtherNode(node), internal, aliasDetailLevel), true);
                    }
                }

            }

            if (historical) {
                final List<LSDEntity> history = new ArrayList<LSDEntity>();
                Node version = node;
                while (version.hasRelationship(VERSION_PARENT, Direction.OUTGOING)) {
                    version = version.getSingleRelationship(VERSION_PARENT, Direction.OUTGOING).getOtherNode(version);
                    history.add(version.convertNodeToLSD(detail, false));
                }
                entity.addSubEntities(LSDAttribute.HISTORY, history);
            }

            node.setPermissionFlagsOnEntity(identity, parent, entity);
            return entity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    public Node addCommentNoTX(@Nullable final Node targetNode, @Nonnull final LSDEntity entity, @Nullable final LiquidURI author) throws InterruptedException {
        fountainNeo.begin();
        try {
            final LSDEntity entityCopy = entity.copy();
            if (author == null) {
                throw new NullPointerException("Null author passed to addComment().");
            }
            if (targetNode == null) {
                throw new NullPointerException("Null node passed to addComment().");
            }
            entityCopy.removeSubEntity(LSDAttribute.AUTHOR);
            final Node commentNode = fountainNeo.createNode();
            commentNode.mergeProperties(entityCopy, false, false, null);
            commentNode.setProperty(LSDAttribute.TYPE, LSDDictionaryTypes.COMMENT.getValue());
            fountainNeo.freeTextIndexNoTx(commentNode);

            commentNode.setIDIfNotSetOnNode();
            String name = entityCopy.getAttribute(LSDAttribute.NAME);
            if (name == null) {
                name = entityCopy.getTypeDef().getPrimaryType().getGenus().toLowerCase() + System.currentTimeMillis();
                entityCopy.setAttribute(LSDAttribute.NAME, name);
            }
            final String uri = targetNode.getAttribute(LSDAttribute.URI) + "~" + "comment-" + author.getSubURI().getSubURI() + System.currentTimeMillis();
            commentNode.setProperty(LSDAttribute.URI, uri);

            if (targetNode.hasRelationship(COMMENT, Direction.OUTGOING)) {
                final Iterable<Relationship> comments = targetNode.getRelationships(COMMENT, Direction.OUTGOING);
                for (final Relationship relationship : comments) {
                    final Node previous = relationship.getEndNode();
                    relationship.delete();
                    final Relationship previousRel = commentNode.createRelationshipTo(previous, PREVIOUS);
                }
            }
            targetNode.createRelationshipTo(commentNode, COMMENT);
            final Node ownerNode = fountainNeo.findByURI(author);
            commentNode.createRelationshipTo(ownerNode, OWNER);
            commentNode.createRelationshipTo(ownerNode, CREATOR);
            commentNode.createRelationshipTo(ownerNode, EDITOR);
            userDAO.addAuthorToNodeNoTX(author, false, commentNode);
            commentNode.inheritPermissions(targetNode);
            fountainNeo.indexBy(commentNode, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(commentNode, LSDAttribute.URI, LSDAttribute.URI, true);
            commentNode.timestamp();


            final int commentCount;
            if (targetNode.hasAttribute(LSDAttribute.COMMENT_COUNT)) {
                commentCount = targetNode.getIntegerAttribute(LSDAttribute.COMMENT_COUNT) + 1;
            } else {
                commentCount = getCommentTraverser(targetNode, FountainNeo.MAX_COMMENTS_DEFAULT).getAllNodes().size();
            }
            log.debug("Comment count is now {0}", commentCount);
            targetNode.setProperty(LSDAttribute.COMMENT_COUNT, String.valueOf(commentCount));
            indexDAO.syncCommentCount(targetNode);
            indexDAO.incrementBoardActivity(targetNode);
            return commentNode;
        } finally {
            fountainNeo.end();
        }
    }


    @Override
    public void createPoolsForUserNoTx(@Nonnull final String username) throws InterruptedException {
        fountainNeo.begin();
        try {
            Node userParentPool;

            /**
             * The user pool, is being reserved for now, we may remove it later.
             */
            userParentPool = fountainNeo.findByURI(new LiquidURI("pool:///users"));

            final LiquidURI cazcadeAliasURI = new LiquidURI("alias:cazcade:" + username);
            if (userParentPool == null) {
                userParentPool = createPoolNoTx(new LiquidSessionIdentifier(FountainNeo.SYSTEM, null), cazcadeAliasURI, fountainNeo.getRootPool(), "users", 0, 0, null, false);
            }
            final Node userPool = createPoolNoTx(new LiquidSessionIdentifier(FountainNeo.SYSTEM, null), cazcadeAliasURI, userParentPool, username, 0, 0, null, false);
        } finally {
            fountainNeo.end();
        }

    }


    @Override
    public void createPoolsForAliasNoTx(@Nonnull final LiquidURI aliasURI, @Nonnull final String name, final String fullName, final boolean systemUser) throws InterruptedException {
        fountainNeo.begin();
        try {

            if (aliasURI.asString().startsWith("alias:cazcade:") && !systemUser) {
                final LiquidSessionIdentifier sessionIdentifier = new LiquidSessionIdentifier(name, null);

                final Node userPool = createPoolNoTx(FountainNeo.SYSTEM_FAKE_SESSION, aliasURI, fountainNeo.getPeoplePool(), name, 0, 0, null, false);
                userPool.setProperty(LSDAttribute.PERMISSIONS, FountainNeo.minimalPermissionNoDeleteValue);

                final Node dockPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".dock", 0, 0, null, false);
                dockPool.setProperty(LSDAttribute.PERMISSIONS, FountainNeo.minimalPermissionNoDeleteValue);

                final Node clipBoardPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".clipboard", 0, 0, null, false);
                clipBoardPool.setProperty(LSDAttribute.PERMISSIONS, FountainNeo.minimalPermissionNoDeleteValue);

                final Node streamPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "stream", 210, -210, null, false);
                streamPool.setProperty(LSDAttribute.DESCRIPTION, "The feeds that make up your stream go here.");
                streamPool.setProperty(LSDAttribute.PERMISSIONS, FountainNeo.privatePermissionNoDeleteValue);
                streamPool.setProperty(LSDAttribute.PINNED, "true");

                final LSDSimpleEntity streamFeedExplanation = LSDSimpleEntity.createEmpty();
                streamFeedExplanation.setType(LSDDictionaryTypes.HTML_FRAGMENT);
                streamFeedExplanation.setAttribute(LSDAttribute.NAME, "stream_feed_explanation");
                streamFeedExplanation.setAttribute(LSDAttribute.TEXT_EXTENDED, "This is where your web feeds are kept. These feeds create your stream. You can manage them in the same way as anywhere else.");
                streamFeedExplanation.setAttribute(LSDAttribute.DESCRIPTION, "Cazcade's company blog.");
                createPoolObjectNoTx(sessionIdentifier, streamPool, streamFeedExplanation, aliasURI, aliasURI, false);

                final LSDSimpleEntity defaultFeedEntity = LSDSimpleEntity.createEmpty();
                defaultFeedEntity.setType(LSDDictionaryTypes.RSS_FEED);
                defaultFeedEntity.setAttribute(LSDAttribute.NAME, "default_cazcade_feed");
                defaultFeedEntity.setAttribute(LSDAttribute.SOURCE, "http://blog.cazcade.com/feed/");
                defaultFeedEntity.setAttribute(LSDAttribute.TITLE, "Cazcade Blog");
                defaultFeedEntity.setAttribute(LSDAttribute.DESCRIPTION, "Cazcade's company blog.");
                createPoolObjectNoTx(sessionIdentifier, streamPool, defaultFeedEntity, aliasURI, aliasURI, false);

                final Node trashPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".trash", 0, 0, null, false);
                trashPool.setProperty(LSDAttribute.PERMISSIONS, FountainNeo.minimalPermissionNoDeleteValue);

                final Node inbox = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".inbox", 0, 0, null, false);
                inbox.setProperty(LSDAttribute.TITLE, "Inbox");
                inbox.setProperty(LSDAttribute.DESCRIPTION, "Your inbox.");
                inbox.setProperty(LSDAttribute.PERMISSIONS, FountainNeo.privatePermissionNoDeleteValue);

                final Node publicPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "public", -210, -210, null, false);
                publicPool.setProperty(LSDAttribute.PERMISSIONS, FountainNeo.publicPermissionNoDeleteValue);
                publicPool.setProperty(LSDAttribute.TITLE, fullName + "'s Public Board");
                publicPool.setProperty(LSDAttribute.DESCRIPTION, "Anyone can modify this.");
                publicPool.setProperty(LSDAttribute.PINNED, "true");

                final Node sharedPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "friends", -210, 210, null, false);
                sharedPool.setProperty(LSDAttribute.PERMISSIONS, FountainNeo.sharedPermissionNoDeleteValue);
                sharedPool.setProperty(LSDAttribute.TITLE, fullName + "'s Friends Board");
                sharedPool.setProperty(LSDAttribute.DESCRIPTION, "Friends can modify this.");
                sharedPool.setProperty(LSDAttribute.PINNED, "true");

                final Node privatePool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "private", 210, 210, null, false);
                privatePool.setProperty(LSDAttribute.TITLE, fullName + "'s Private Board");
                privatePool.setProperty(LSDAttribute.DESCRIPTION, "Only you can view this.");
                privatePool.setProperty(LSDAttribute.PERMISSIONS, FountainNeo.privateSharedPermissionNoDeleteValue);
                privatePool.setProperty(LSDAttribute.PINNED, "true");


                final Node profilePool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "profile", 0, 0, null, true);
                profilePool.setProperty(LSDAttribute.TITLE, fullName + "'s Profile Board");
                profilePool.setProperty(LSDAttribute.DESCRIPTION, "This is all about you.");
                profilePool.setProperty(LSDAttribute.PINNED, "true");
                profilePool.setProperty(LSDAttribute.PERMISSIONS, FountainNeo.defaultPermissionNoDeleteValue);
            }
        } finally {
            fountainNeo.end();
        }

    }

    @Override
    public void createPoolsForCazcadeAliasNoTx(@Nonnull final String name, final String fullName, final boolean systemUser) throws InterruptedException {
        createPoolsForAliasNoTx(new LiquidURI("alias:cazcade:" + name), name, fullName, systemUser);
    }


    @Nullable
    public Collection<LSDEntity> getCommentsTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidURI uri, final int max, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final List<LSDEntity> comments = new ArrayList<LSDEntity>();
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final Node node = fountainNeo.findByURI(uri);
                if (node == null) {
                    return null;
                }
                final Traverser traverser = getCommentTraverser(node, max);

                for (final org.neo4j.graphdb.Node comment : traverser) {
                    comments.add(convertNodeToEntityWithRelatedEntitiesNoTX(identity, new Node(comment), null, detail, internal, false));
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

    Traverser getCommentTraverser(@Nonnull final Node node, final int max) {
        final int[] count = new int[1];
        return node.traverse(Traverser.Order.DEPTH_FIRST, new StopEvaluator() {
                    @Override
                    public boolean isStopNode(final TraversalPosition currentPos) {
                        return count[0]++ >= max;
                    }
                }, new ReturnableEvaluator() {
                    public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                        return currentPos.currentNode().getProperty(LSDAttribute.TYPE.getKeyName()).equals(LSDDictionaryTypes.COMMENT.getValue());
                    }
                }, VERSION_PARENT, Direction.OUTGOING, COMMENT, Direction.OUTGOING, PREVIOUS, Direction.OUTGOING
        );
    }

}