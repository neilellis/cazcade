package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.*;
import cazcade.fountain.datastore.impl.*;
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
    private static final Logger log = Logger.getLogger(FountainPoolDAOImpl.class);

    @Autowired
    private FountainNeo fountainNeo;


    @Autowired
    private FountainIndexServiceImpl indexDAO;


    @Autowired
    private FountainUserDAO userDAO;


    public FountainPoolDAOImpl() {


    }

    public void recalculatePoolURIs(@Nonnull final FountainEntity fountainEntity) throws InterruptedException {
        fountainNeo.recalculateURI(fountainEntity);
        final Traverser traverser = fountainEntity.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return currentPos.currentNode().hasProperty(LSDAttribute.URI.getKeyName());
            }
        }, FountainRelationships.CHILD, Direction.OUTGOING);
        for (final org.neo4j.graphdb.Node childNode : traverser) {
            fountainNeo.recalculateURI(new FountainEntityImpl(childNode));
        }
    }

    @Nullable
    @Override
    public LSDEntity updatePool(@Nonnull final LiquidSessionIdentifier sessionIdentifier, @Nonnull final FountainEntity fountainEntityImpl, final LiquidRequestDetailLevel detail, final boolean internal, final boolean historical, final Integer end, final int start, final ChildSortOrder order, final boolean contents, @Nonnull final LSDEntity requestEntity, final Runnable onRenameAction) throws Exception {
        final FountainEntity resultFountainEntity = fountainNeo.updateNodeAndReturnNodeNoTx(sessionIdentifier, fountainEntityImpl, requestEntity, onRenameAction);
        indexDAO.syncBoard(fountainEntityImpl);
        return getPoolAndContentsNoTx(resultFountainEntity, detail, contents, order, internal, sessionIdentifier, start, end, historical);
    }


    @Nonnull
    @Override
    public FountainEntity createPoolNoTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidURI owner, final FountainEntity parent, @Nonnull final String poolName, final double x, final double y, @Nullable final String title, final boolean listed) throws InterruptedException {
        return createPoolNoTx(identity, owner, parent, LSDDictionaryTypes.POOL2D, poolName, x, y, title, listed);

    }

    @Nonnull
    @Override
    public FountainEntity createPoolNoTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidURI owner, @Nullable final FountainEntity parent, @Nonnull final LSDType type, @Nonnull final String poolName, final double x, final double y, @Nullable final String title, final boolean listed) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (parent == null) {
                throw new DataStoreException("Tried to create a pool with a null parent fountainEntityImpl.");
            }
            fountainNeo.assertAuthorized(parent, identity, LiquidPermission.MODIFY, LiquidPermission.VIEW);
            final FountainEntity fountainEntityImpl = fountainNeo.createNode();
            fountainEntityImpl.setIDIfNotSetOnNode();
            fountainEntityImpl.setAttribute(LSDAttribute.LISTED, listed);

            String parentURI = parent.getAttribute(LSDAttribute.URI);
            if (!parentURI.endsWith("/")) {
                parentURI += "/";
            }
            final String newURI = parentURI + poolName.toLowerCase();
            fountainEntityImpl.setAttribute(LSDAttribute.URI, newURI);
            fountainEntityImpl.setAttribute(LSDAttribute.NAME, poolName);
            if (title != null) {
                fountainEntityImpl.setAttribute(LSDAttribute.TITLE, title);
            }
            fountainEntityImpl.setAttribute(LSDAttribute.TYPE, type.asString());
            if (!parent.hasAttribute(LSDAttribute.PERMISSIONS)) {
                throw new DataStoreException("The parent pool %s had no permissions, all pools must have permissions.", parentURI);
            }
            fountainEntityImpl.inheritPermissions(parent);
            parent.createRelationshipTo(fountainEntityImpl, FountainRelationships.CHILD);
            final FountainEntity ownerFountainEntity = fountainNeo.findByURI(owner);
            fountainEntityImpl.createRelationshipTo(ownerFountainEntity, FountainRelationships.OWNER);
            fountainEntityImpl.createRelationshipTo(ownerFountainEntity, FountainRelationships.CREATOR);
            fountainEntityImpl.createRelationshipTo(ownerFountainEntity, FountainRelationships.EDITOR);
            final LSDSimpleEntity view = LSDSimpleEntity.createEmpty();
            view.setAttribute(LSDAttribute.VIEW_X, String.valueOf(x));
            view.setAttribute(LSDAttribute.VIEW_Y, String.valueOf(y));
            view.setAttribute(LSDAttribute.VIEW_WIDTH, "200");
            view.setAttribute(LSDAttribute.VIEW_HEIGHT, "200");
            createView(fountainEntityImpl, view);
            userDAO.addAuthorToNodeNoTX(owner, false, fountainEntityImpl);
            fountainNeo.indexBy(fountainEntityImpl, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(fountainEntityImpl, LSDAttribute.URI, LSDAttribute.URI, true);
            fountainEntityImpl.timestamp();
            assertHasOwner(fountainEntityImpl);
            indexDAO.syncBoard(fountainEntityImpl);
            return fountainEntityImpl;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    public FountainEntity createPoolObjectNoTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final FountainEntity pool, @Nonnull final LSDEntity entity, @Nonnull final LiquidURI owner, @Nullable final LiquidURI author, final boolean createAuthor) throws InterruptedException {
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

            final FountainEntity fountainEntityImpl = fountainNeo.createNode();
            fountainEntityImpl.mergeProperties(entityCopy, false, false, null);
            fountainNeo.freeTextIndexNoTx(fountainEntityImpl);

            fountainEntityImpl.setIDIfNotSetOnNode();
            final String uri = pool.getAttribute(LSDAttribute.URI) + "#" + name.toLowerCase();
            fountainEntityImpl.setAttribute(LSDAttribute.URI, uri);
            pool.createRelationshipTo(fountainEntityImpl, FountainRelationships.CHILD);
            final FountainEntity ownerFountainEntity = fountainNeo.findByURI(owner);
            fountainEntityImpl.createRelationshipTo(ownerFountainEntity, FountainRelationships.OWNER);
            fountainEntityImpl.createRelationshipTo(ownerFountainEntity, FountainRelationships.CREATOR);
            fountainEntityImpl.createRelationshipTo(ownerFountainEntity, FountainRelationships.EDITOR);
            userDAO.addAuthorToNodeNoTX(author, createAuthor, fountainEntityImpl);
            fountainEntityImpl.inheritPermissions(pool);
            fountainNeo.indexBy(fountainEntityImpl, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(fountainEntityImpl, LSDAttribute.URI, LSDAttribute.URI, true);
            fountainEntityImpl.timestamp();
            final FountainEntity view = createView(fountainEntityImpl, viewEntity);
            assertHasOwner(fountainEntityImpl);
            indexDAO.incrementBoardActivity(pool);
            return fountainEntityImpl;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    FountainEntity createView(@Nonnull final FountainEntity object, @Nonnull final LSDEntity viewEntity) throws InterruptedException {
        final FountainEntity fountainEntityImpl = fountainNeo.createNode();
        fountainEntityImpl.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.VIEW.getValue());
        fountainEntityImpl.mergeProperties(viewEntity, false, false, null);
        fountainNeo.freeTextIndexNoTx(fountainEntityImpl);
        fountainEntityImpl.setIDIfNotSetOnNode();
        final String uri = object.getAttribute(LSDAttribute.URI) + ":view";
        fountainEntityImpl.setAttribute(LSDAttribute.URI, uri);
        object.createRelationshipTo(fountainEntityImpl, FountainRelationships.VIEW);
        fountainEntityImpl.setAttribute(LSDAttribute.PERMISSIONS, object.getAttribute(LSDAttribute.PERMISSIONS));
        fountainEntityImpl.setAttribute(LSDAttribute.VIEW_RADIUS, fountainEntityImpl.calculateRadius());
        if (!fountainEntityImpl.hasAttribute(LSDAttribute.VIEW_X)) {
            fountainEntityImpl.setAttribute(LSDAttribute.VIEW_X, Math.random() * 100 - 50);
        }

        if (!fountainEntityImpl.hasAttribute(LSDAttribute.VIEW_Y)) {
            fountainEntityImpl.setAttribute(LSDAttribute.VIEW_Y, Math.random() * 100 - 50);
        }

        fountainNeo.indexBy(fountainEntityImpl, LSDAttribute.ID, LSDAttribute.ID, true);
        fountainNeo.indexBy(fountainEntityImpl, LSDAttribute.URI, LSDAttribute.URI, true);
        fountainEntityImpl.timestamp();
        return fountainEntityImpl;
    }

    @Nonnull
    FountainEntity copyPoolObjectForUpdate(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final FountainEntity fountainEntityImpl, final boolean fork) throws InterruptedException {
        fountainEntityImpl.assertLatestVersion();
        return fountainNeo.cloneNodeForNewVersion(editor, fountainEntityImpl, fork);
    }

    @Nullable
    @Override
    public LSDEntity updatePoolObjectNoTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidSessionIdentifier editor, @Nonnull final LSDEntity entity, @Nullable final FountainEntity pool, @Nonnull final FountainEntity origFountainEntity, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {

        final LSDEntity entityCopy = entity.copy();
        entityCopy.removeSubEntity(LSDAttribute.VIEW);
        entityCopy.removeSubEntity(LSDAttribute.AUTHOR);
        entityCopy.removeSubEntity(LSDAttribute.OWNER);
        entityCopy.removeValue(LSDAttribute.ID);
        final LSDEntity newObject;

        final FountainEntity fountainEntity = copyPoolObjectForUpdate(editor, origFountainEntity, false);
        if (!entity.getURI().toString().toLowerCase().equals(origFountainEntity.getAttribute(LSDAttribute.URI))) {
            throw new CannotChangeURIException("Tried to change the URI of %s to %s", origFountainEntity.getAttribute(LSDAttribute.URI), entity.getURI().toString());
        }
        newObject = convertNodeToEntityWithRelatedEntitiesNoTX(identity, fountainEntity.mergeProperties(entityCopy, true, false, new Runnable() {
            @Override
            public void run() {
                try {
                    recalculatePoolURIs(fountainEntity);
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }
        }), pool, detail, internal, false);
        assertHasOwner(fountainEntity);
        if (pool != null) {
            indexDAO.incrementBoardActivity(pool);
        }
        fountainNeo.freeTextIndexNoTx(fountainEntity);
        return newObject;

    }

    @Nullable
    @Override
    public LSDEntity getPoolObjectTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidURI uri, final boolean internal, final boolean historical, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final FountainEntity fountainEntity = fountainNeo.findByURI(uri);
                final FountainEntity parent = fountainNeo.findByURI(uri.getWithoutFragmentOrComment());

                if (fountainEntity == null) {
                    return null;
                }
                assertHasOwner(fountainEntity);
                assertHasOwner(parent);
                return convertNodeToEntityWithRelatedEntitiesNoTX(identity, fountainEntity.getLatestVersionFromFork(), parent, detail, internal, historical);
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
                final FountainEntity fountainEntity = fountainNeo.findByUUID(uuid);
                if (fountainEntity == null) {
                    return null;
                }
                return convertNodeToEntityWithRelatedEntitiesNoTX(identity, fountainEntity.getLatestVersionFromFork(), null, detail, internal, historical);
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
    public FountainEntity linkPoolObject(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final FountainEntity newOwner, @Nonnull final FountainEntity target, @Nonnull final FountainEntity to) throws InterruptedException {
        fountainNeo.begin();
        try {
            final FountainEntity clone = fountainNeo.cloneNodeForNewVersion(editor, target, true);
            assertHasOwner(clone);
            final String candidateName = clone.getAttribute(LSDAttribute.NAME);
            String candidateURI = to.getAttribute(LSDAttribute.URI) + "#" + candidateName;
            String name;
            int count = 1;
            while (fountainNeo.findByURI(new LiquidURI(candidateURI)) != null) {
                name = candidateName + count++;
                candidateURI = to.getAttribute(LSDAttribute.URI) + "#" + name;
            }
            clone.setAttribute(LSDAttribute.URI, candidateURI);
            fountainNeo.reindex(clone, LSDAttribute.URI, LSDAttribute.URI);
            to.createRelationshipTo(clone, FountainRelationships.CHILD);
            final FountainRelationship ownerRel = clone.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
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
    public FountainEntity linkPoolObject(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final FountainEntity newOwner, @Nonnull final FountainEntity target, final FountainEntity from, @Nonnull final FountainEntity to) throws InterruptedException {
        fountainNeo.begin();
        try {
            return linkPoolObject(editor, newOwner, target, to);
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    @Override
    public FountainEntity unlinkPoolObject(@Nonnull final FountainEntity target) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (target.hasRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING)) {
                throw new CannotUnlinkEntityException("Cannot unlink a fountainEntityImpl that is not the latest version.");
            }
            final Iterable<FountainRelationship> relationships = target.getRelationships(FountainRelationships.CHILD, Direction.INCOMING);
            for (final FountainRelationship relationship : relationships) {
                relationship.delete();
            }
            final Iterable<FountainRelationship> linkedRelationships = target.getRelationships(FountainRelationships.LINKED_CHILD, Direction.INCOMING);
            for (final FountainRelationship relationship : linkedRelationships) {
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
    public FountainEntity linkPool(final FountainEntity newOwner, @Nonnull final FountainEntity target, @Nonnull final FountainEntity to) throws InterruptedException {
        fountainNeo.begin();
        try {
            final String candidateURI = to.getAttribute(LSDAttribute.URI) + "#" + target.getAttribute(LSDAttribute.NAME);
            String uri = candidateURI;
            int count = 1;
            while (fountainNeo.findByURI(new LiquidURI(uri)) != null) {
                uri = candidateURI + count++;
            }
            target.setAttribute(LSDAttribute.URI, uri);
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
    public FountainEntity linkPool(final FountainEntity newOwner, @Nonnull final FountainEntity target, final FountainEntity from, @Nonnull final FountainEntity to) throws InterruptedException {
        fountainNeo.begin();
        try {
            final FountainEntity clone = linkPool(newOwner, target, to);
            unlinkPool(target);
            assertHasOwner(clone);

            return clone;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    @Override
    public FountainEntity unlinkPool(@Nonnull final FountainEntity target) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Iterable<FountainRelationship> relationships = target.getRelationships(FountainRelationships.CHILD, Direction.INCOMING);
            for (final FountainRelationship relationship : relationships) {
                relationship.delete();
            }
            final Iterable<FountainRelationship> linkedRelationships = target.getRelationships(FountainRelationships.LINKED_CHILD, Direction.INCOMING);
            for (final FountainRelationship relationship : linkedRelationships) {
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
    public LSDEntity createPoolObjectTx(@Nonnull final FountainEntity poolFountainEntity, @Nonnull final LiquidSessionIdentifier identity, @Nullable final LiquidURI owner, final LiquidURI author, @Nonnull final LSDEntity entity, final LiquidRequestDetailLevel detail, final boolean internal, final boolean createAuthor) throws Exception {
        if (owner == null) {
            throw new NullPointerException("Tried to create a pool without an owner.");
        }
        fountainNeo.begin();
        try {
            final FountainEntity poolObject = createPoolObjectNoTx(identity, poolFountainEntity, entity, owner, author, createAuthor);
            assertHasOwner(poolObject);
            final FountainEntity parent = poolObject.parentNode();
//            recalculateCentreImage(parent, poolObject);
            return convertNodeToEntityWithRelatedEntitiesNoTX(identity, poolObject, parent, detail, internal, false);
        } finally {
            fountainNeo.end();
        }
    }

    private void assertHasOwner(@Nonnull final FountainEntity poolObject) {
        final FountainRelationship ownerRel = poolObject.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
        if (ownerRel == null) {
            throw new IllegalStateException("We have a pool object with no owner.");
        }
    }

    @Nullable
    @Override
    public LSDEntity getPoolAndContentsNoTx(@Nonnull final FountainEntity targetFountainEntity, final LiquidRequestDetailLevel detail, final boolean contents, final ChildSortOrder order, final boolean internal, @Nonnull final LiquidSessionIdentifier identity, @Nullable Integer start, @Nullable Integer end, final boolean historical) throws Exception {
        fountainNeo.begin();
        try {
            final FountainEntity pool = convertToPoolFromPoolOrObject(targetFountainEntity);
            final LSDEntity entity = convertNodeToEntityWithRelatedEntitiesNoTX(identity, pool, null, detail, internal, historical);
            if (contents) {
                final List<LSDEntity> entities = new ArrayList<LSDEntity>();
                final int count = 0;
                pool.forEachChild(new NodeCallback() {
                    public void call(@Nonnull final FountainEntity child) throws Exception {
                        final LSDEntity poolObjectEntity = convertNodeToEntityWithRelatedEntitiesNoTX(identity, child, pool, detail, internal, false);
                        if (targetFountainEntity.equals(child)) {
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
                entity.setAttribute(LSDAttribute.POPULARITY_METRIC, String.valueOf(targetFountainEntity.popularity()));
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
            final FountainEntity fountainEntityImpl = fountainNeo.findByURI(uri);
            if (fountainEntityImpl == null) {
                return null;
            }
            return getPoolAndContentsNoTx(fountainEntityImpl, detail, contents, order, internal, identity, start, end, historical);
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable
    public LSDEntity getPoolAndContentsNoTX(@Nonnull final LiquidUUID target, final LiquidRequestDetailLevel detail, final ChildSortOrder order, final boolean contents, final boolean internal, @Nonnull final LiquidSessionIdentifier identity, final Integer start, final Integer end, final boolean historical) throws Exception {
        fountainNeo.begin();
        try {
            final FountainEntity fountainEntityImpl = fountainNeo.findByUUID(target);
            if (fountainEntityImpl == null) {
                return null;
            }
            return getPoolAndContentsNoTx(fountainEntityImpl, detail, contents, order, internal, identity, start, end, historical);
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
                final FountainEntity fountainEntity = fountainNeo.findByUUID(target);
                return deletePoolObjectNoTx(internal, detail, transaction, fountainEntity);
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
                final FountainEntity fountainEntity = fountainNeo.findByURI(uri);
                return deletePoolObjectNoTx(internal, detail, transaction, fountainEntity);
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
    LSDEntity deletePoolObjectNoTx(final boolean internal, final LiquidRequestDetailLevel detail, @Nonnull final Transaction transaction, @Nonnull final FountainEntity fountainEntity) throws Exception {
        if (fountainEntity.isDeleted()) {
            throw new DeletedEntityException("The entity %s is already deleted so cannot be deleted again.", fountainEntity.hasAttribute(LSDAttribute.URI) ? fountainEntity.getAttribute(LSDAttribute.URI) : "<unknown-uri>");
        }
        fountainNeo.delete(fountainEntity);
        final FountainRelationship relationship = fountainEntity.getSingleRelationship(FountainRelationships.CHILD, Direction.INCOMING);
        if (relationship == null) {
            throw new OrphanedEntityException("The entity %s is orphaned so cannot be deleted.", fountainEntity.hasAttribute(LSDAttribute.URI) ? fountainEntity.getAttribute(LSDAttribute.URI) : "<unknown-uri>");
        }
//        recalculateCentreImage(relationship.getOtherNode(fountainEntityImpl), fountainEntityImpl);
        transaction.success();
        return fountainEntity.convertNodeToLSD(detail, internal);
    }

    @Override
    public void visitNodeNoTx(@Nonnull final FountainEntity fountainEntityImpl, @Nonnull final LiquidSessionIdentifier identity) throws InterruptedException {
        fountainNeo.begin();
        try {
            final LiquidUUID session = identity.getSession();
            final FountainEntity sessionFountainEntity = fountainNeo.findByUUID(session);
            for (final FountainRelationship relationship : sessionFountainEntity.getRelationships(FountainRelationships.VISITING, Direction.OUTGOING)) {
                relationship.delete();
            }
            final FountainEntity pool = convertToPoolFromPoolOrObject(fountainEntityImpl);
            final FountainRelationship relationshipTo = sessionFountainEntity.createRelationshipTo(pool, FountainRelationships.VISITING);
            relationshipTo.setProperty(LSDAttribute.UPDATED.getKeyName(), String.valueOf(System.currentTimeMillis()));
            sessionFountainEntity.setAttribute(LSDAttribute.ACTIVE, true);
            sessionFountainEntity.timestamp();
            indexDAO.incrementBoardActivity(fountainEntityImpl);
            indexDAO.visitBoard(fountainEntityImpl, identity.getAliasURL());
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    FountainEntity convertToPoolFromPoolOrObject(@Nonnull final FountainEntity fountainEntityImpl) {
        final FountainEntity pool;
        if (fountainEntityImpl.getAttribute(LSDAttribute.TYPE).startsWith(LSDDictionaryTypes.POOL.getValue())) {
            pool = fountainEntityImpl;
        } else {
            pool = fountainEntityImpl.parentNode();
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
                final FountainEntity fountainEntity = fountainNeo.findByURI(target, true);
                fountainEntity.setAttribute(LSDAttribute.SELECTED, selected);
                newObject = convertNodeToEntityWithRelatedEntitiesNoTX(identity, fountainEntity, null, detail, internal, false);
                indexDAO.incrementBoardActivity(fountainEntity);
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
    public FountainEntity movePoolObjectNoTx(@Nonnull final LiquidURI object, @Nullable final Double x, @Nullable final Double y, @Nullable final Double z) throws Exception {
        fountainNeo.begin();
        try {
            final FountainEntity fountainEntity = fountainNeo.findByURI(object);
            final FountainRelationship relationship = fountainEntity.getSingleRelationship(FountainRelationships.VIEW, Direction.OUTGOING);
            if (relationship == null) {
                throw new RelationshipNotFoundException("No view relationship for %s(%s)", fountainEntity.getAttribute(LSDAttribute.URI), object);
            }
            final FountainEntity viewFountainEntity = relationship.getOtherNode(fountainEntity);
            if (viewFountainEntity == null) {
                throw new EntityNotFoundException("No view relationship for %s(%s)", fountainEntity.getAttribute(LSDAttribute.URI), object);
            }
            if (x != null) {
                viewFountainEntity.setAttribute(LSDAttribute.VIEW_X, x.toString());
            }
            if (y != null) {
                viewFountainEntity.setAttribute(LSDAttribute.VIEW_Y, y.toString());
            }
            if (z != null) {
                viewFountainEntity.setAttribute(LSDAttribute.VIEW_Z, z.toString());
            }

            viewFountainEntity.setAttribute(LSDAttribute.VIEW_RADIUS, String.valueOf(fountainEntity.calculateRadius()));
            final FountainRelationship parentRel = fountainEntity.getSingleRelationship(FountainRelationships.CHILD, Direction.INCOMING);
            if (parentRel == null) {
                throw new OrphanedEntityException("The entity %s (%s) is orphaned so cannot be moved.", object.toString(), fountainEntity.hasAttribute(LSDAttribute.URI) ? fountainEntity.getAttribute(LSDAttribute.URI) : "<unknown-uri>");
            }
//            recalculateCentreImage(parentRel.getOtherNode(fountainEntityImpl), fountainEntityImpl);
            indexDAO.incrementBoardActivity(parentRel.getOtherNode(fountainEntity));

            return viewFountainEntity;
        } finally {
            fountainNeo.end();
        }
    }


    @Nullable
    public LSDEntity convertNodeToEntityWithRelatedEntitiesNoTX(@Nonnull final LiquidSessionIdentifier identity, @Nullable final FountainEntity fountainEntity, @Nullable final FountainEntity parent, final LiquidRequestDetailLevel detail, final boolean internal, final boolean historical) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (fountainEntity == null) {
                return null;
            }
            final LSDEntity entity = fountainEntity.convertNodeToLSD(detail, internal);
            boolean done = false;
            final LiquidRequestDetailLevel aliasDetailLevel = detail == LiquidRequestDetailLevel.COMPLETE ? LiquidRequestDetailLevel.COMPLETE : LiquidRequestDetailLevel.PERSON_MINIMAL;

            final Iterable<FountainRelationship> iterable = fountainEntity.getRelationships(VIEW, Direction.OUTGOING);
            for (final FountainRelationship relationship : iterable) {
                if (done) {
                    throw new DuplicateEntityException("Found a second view for a single object.");
                }
                entity.addSubEntity(LSDAttribute.VIEW, relationship.getOtherNode(fountainEntity).convertNodeToLSD(detail, internal), true);
                done = true;
            }

            if (detail == LiquidRequestDetailLevel.COMPLETE || detail == LiquidRequestDetailLevel.NORMAL || detail == LiquidRequestDetailLevel.BOARD_LIST) {
                final FountainRelationship ownerRel = fountainEntity.getSingleRelationship(OWNER, Direction.OUTGOING);
                if (ownerRel != null) {
                    entity.addSubEntity(LSDAttribute.OWNER, userDAO.getAliasFromNode(ownerRel.getOtherNode(fountainEntity), internal, aliasDetailLevel), true);
                }

                if (detail != LiquidRequestDetailLevel.BOARD_LIST) {
                    final FountainRelationship relationship = fountainEntity.getSingleRelationship(FountainRelationships.AUTHOR, Direction.OUTGOING);
                    if (relationship != null) {
                        entity.addSubEntity(LSDAttribute.AUTHOR, userDAO.getAliasFromNode(relationship.getOtherNode(fountainEntity), internal, aliasDetailLevel), true);
                    }

                    final FountainRelationship editorRel = fountainEntity.getSingleRelationship(EDITOR, Direction.OUTGOING);
                    if (editorRel != null) {
                        entity.addSubEntity(LSDAttribute.EDITOR, userDAO.getAliasFromNode(editorRel.getOtherNode(fountainEntity), internal, aliasDetailLevel), true);
                    }
                }

            }

            if (historical) {
                final List<LSDEntity> history = new ArrayList<LSDEntity>();
                FountainEntity version = fountainEntity;
                while (version.hasRelationship(VERSION_PARENT, Direction.OUTGOING)) {
                    version = version.getSingleRelationship(VERSION_PARENT, Direction.OUTGOING).getOtherNode(version);
                    history.add(version.convertNodeToLSD(detail, false));
                }
                entity.addSubEntities(LSDAttribute.HISTORY, history);
            }

            fountainEntity.setPermissionFlagsOnEntity(identity, parent, entity);
            return entity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    public FountainEntity addCommentNoTX(@Nullable final FountainEntity targetFountainEntity, @Nonnull final LSDEntity entity, @Nullable final LiquidURI author) throws InterruptedException {
        fountainNeo.begin();
        try {
            final LSDEntity entityCopy = entity.copy();
            if (author == null) {
                throw new NullPointerException("Null author passed to addComment().");
            }
            if (targetFountainEntity == null) {
                throw new NullPointerException("Null fountainEntityImpl passed to addComment().");
            }
            entityCopy.removeSubEntity(LSDAttribute.AUTHOR);
            final FountainEntity commentFountainEntity = fountainNeo.createNode();
            commentFountainEntity.mergeProperties(entityCopy, false, false, null);
            commentFountainEntity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.COMMENT.getValue());
            fountainNeo.freeTextIndexNoTx(commentFountainEntity);

            commentFountainEntity.setIDIfNotSetOnNode();
            String name = entityCopy.getAttribute(LSDAttribute.NAME);
            if (name == null) {
                name = entityCopy.getTypeDef().getPrimaryType().getGenus().toLowerCase() + System.currentTimeMillis();
                entityCopy.setAttribute(LSDAttribute.NAME, name);
            }
            final String uri = targetFountainEntity.getAttribute(LSDAttribute.URI) + "~" + "comment-" + author.getSubURI().getSubURI() + System.currentTimeMillis();
            commentFountainEntity.setAttribute(LSDAttribute.URI, uri);

            if (targetFountainEntity.hasRelationship(COMMENT, Direction.OUTGOING)) {
                final Iterable<FountainRelationship> comments = targetFountainEntity.getRelationships(COMMENT, Direction.OUTGOING);
                for (final FountainRelationship relationship : comments) {
                    final FountainEntity previous = relationship.getEndNode();
                    relationship.delete();
                    final FountainRelationship previousRel = commentFountainEntity.createRelationshipTo(previous, PREVIOUS);
                }
            }
            targetFountainEntity.createRelationshipTo(commentFountainEntity, COMMENT);
            final FountainEntity ownerFountainEntity = fountainNeo.findByURI(author);
            commentFountainEntity.createRelationshipTo(ownerFountainEntity, OWNER);
            commentFountainEntity.createRelationshipTo(ownerFountainEntity, CREATOR);
            commentFountainEntity.createRelationshipTo(ownerFountainEntity, EDITOR);
            userDAO.addAuthorToNodeNoTX(author, false, commentFountainEntity);
            commentFountainEntity.inheritPermissions(targetFountainEntity);
            fountainNeo.indexBy(commentFountainEntity, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(commentFountainEntity, LSDAttribute.URI, LSDAttribute.URI, true);
            commentFountainEntity.timestamp();


            final int commentCount;
            if (targetFountainEntity.hasAttribute(LSDAttribute.COMMENT_COUNT)) {
                commentCount = targetFountainEntity.getIntegerAttribute(LSDAttribute.COMMENT_COUNT) + 1;
            } else {
                commentCount = getCommentTraverser(targetFountainEntity, FountainNeoImpl.MAX_COMMENTS_DEFAULT).getAllNodes().size();
            }
            log.debug("Comment count is now {0}", commentCount);
            targetFountainEntity.setAttribute(LSDAttribute.COMMENT_COUNT, String.valueOf(commentCount));
            indexDAO.syncCommentCount(targetFountainEntity);
            indexDAO.incrementBoardActivity(targetFountainEntity);
            return commentFountainEntity;
        } finally {
            fountainNeo.end();
        }
    }


    @Override
    public void createPoolsForUserNoTx(@Nonnull final String username) throws InterruptedException {
        fountainNeo.begin();
        try {
            FountainEntity userParentPool;

            /**
             * The user pool, is being reserved for now, we may remove it later.
             */
            userParentPool = fountainNeo.findByURI(new LiquidURI("pool:///users"));

            final LiquidURI cazcadeAliasURI = new LiquidURI("alias:cazcade:" + username);
            if (userParentPool == null) {
                userParentPool = createPoolNoTx(new LiquidSessionIdentifier(FountainNeoImpl.SYSTEM, null), cazcadeAliasURI, fountainNeo.getRootPool(), "users", 0, 0, null, false);
            }
            final FountainEntity userPool = createPoolNoTx(new LiquidSessionIdentifier(FountainNeoImpl.SYSTEM, null), cazcadeAliasURI, userParentPool, username, 0, 0, null, false);
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

                final FountainEntity userPool = createPoolNoTx(FountainNeoImpl.SYSTEM_FAKE_SESSION, aliasURI, fountainNeo.getPeoplePool(), name, 0, 0, null, false);
                userPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.minimalPermissionNoDeleteValue);

                final FountainEntity dockPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".dock", 0, 0, null, false);
                dockPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.minimalPermissionNoDeleteValue);

                final FountainEntity clipBoardPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".clipboard", 0, 0, null, false);
                clipBoardPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.minimalPermissionNoDeleteValue);

                final FountainEntity streamPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "stream", 210, -210, null, false);
                streamPool.setAttribute(LSDAttribute.DESCRIPTION, "The feeds that make up your stream go here.");
                streamPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.privatePermissionNoDeleteValue);
                streamPool.setAttribute(LSDAttribute.PINNED, "true");

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

                final FountainEntity trashPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".trash", 0, 0, null, false);
                trashPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.minimalPermissionNoDeleteValue);

                final FountainEntity inbox = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".inbox", 0, 0, null, false);
                inbox.setAttribute(LSDAttribute.TITLE, "Inbox");
                inbox.setAttribute(LSDAttribute.DESCRIPTION, "Your inbox.");
                inbox.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.privatePermissionNoDeleteValue);

                final FountainEntity publicPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "public", -210, -210, null, false);
                publicPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.publicPermissionNoDeleteValue);
                publicPool.setAttribute(LSDAttribute.TITLE, fullName + "'s Public Board");
                publicPool.setAttribute(LSDAttribute.DESCRIPTION, "Anyone can modify this.");
                publicPool.setAttribute(LSDAttribute.PINNED, "true");

                final FountainEntity sharedPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "friends", -210, 210, null, false);
                sharedPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.sharedPermissionNoDeleteValue);
                sharedPool.setAttribute(LSDAttribute.TITLE, fullName + "'s Friends Board");
                sharedPool.setAttribute(LSDAttribute.DESCRIPTION, "Friends can modify this.");
                sharedPool.setAttribute(LSDAttribute.PINNED, "true");

                final FountainEntity privatePool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "private", 210, 210, null, false);
                privatePool.setAttribute(LSDAttribute.TITLE, fullName + "'s Private Board");
                privatePool.setAttribute(LSDAttribute.DESCRIPTION, "Only you can view this.");
                privatePool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.privateSharedPermissionNoDeleteValue);
                privatePool.setAttribute(LSDAttribute.PINNED, "true");


                final FountainEntity profilePool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "profile", 0, 0, null, true);
                profilePool.setAttribute(LSDAttribute.TITLE, fullName + "'s Profile Board");
                profilePool.setAttribute(LSDAttribute.DESCRIPTION, "This is all about you.");
                profilePool.setAttribute(LSDAttribute.PINNED, "true");
                profilePool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.defaultPermissionNoDeleteValue);
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
                final FountainEntity fountainEntity = fountainNeo.findByURI(uri);
                if (fountainEntity == null) {
                    return null;
                }
                final Traverser traverser = getCommentTraverser(fountainEntity, max);

                for (final org.neo4j.graphdb.Node comment : traverser) {
                    comments.add(convertNodeToEntityWithRelatedEntitiesNoTX(identity, new FountainEntityImpl(comment), null, detail, internal, false));
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

    Traverser getCommentTraverser(@Nonnull final FountainEntity fountainEntity, final int max) {
        final int[] count = new int[1];
        return fountainEntity.traverse(Traverser.Order.DEPTH_FIRST, new StopEvaluator() {
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