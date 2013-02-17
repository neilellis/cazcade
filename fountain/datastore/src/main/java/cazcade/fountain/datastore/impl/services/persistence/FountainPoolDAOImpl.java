/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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

    @Override @Nonnull
    public LSDPersistedEntity addCommentNoTX(@Nullable final LSDPersistedEntity commentEntity, @Nonnull final LSDTransferEntity commentTransferEntity, @Nullable final LiquidURI author) throws InterruptedException {
        fountainNeo.begin();
        try {
            final LSDTransferEntity entityCopy = commentTransferEntity.copy();
            if (author == null) {
                throw new NullPointerException("Null author passed to addComment().");
            }
            if (commentEntity == null) {
                throw new NullPointerException("Null persistedEntityImpl passed to addComment().");
            }
            entityCopy.removeSubEntity(LSDAttribute.AUTHOR);
            final LSDPersistedEntity commentPersistedEntity = fountainNeo.createNode();
            commentPersistedEntity.mergeProperties(entityCopy, false, false, null);
            commentPersistedEntity.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.COMMENT.getValue());
            fountainNeo.freeTextIndexNoTx(commentPersistedEntity);

            commentPersistedEntity.setIDIfNotSetOnNode();
            if (!entityCopy.hasAttribute(LSDAttribute.NAME)) {
                final String name = entityCopy.getTypeDef().getPrimaryType().getGenus().toLowerCase() + System.currentTimeMillis();
                entityCopy.setAttribute(LSDAttribute.NAME, name);
            }
            final String uri = commentEntity.getAttribute(LSDAttribute.URI) +
                               '~' +
                               "comment-" +
                               author.getSubURI().getSubURI() +
                               System.currentTimeMillis();
            commentPersistedEntity.setAttribute(LSDAttribute.URI, uri);

            if (commentEntity.hasRelationship(COMMENT, Direction.OUTGOING)) {
                final Iterable<FountainRelationship> comments = commentEntity.getRelationships(COMMENT, Direction.OUTGOING);
                for (final FountainRelationship relationship : comments) {
                    final LSDPersistedEntity previous = relationship.getEndNode();
                    relationship.delete();
                    final FountainRelationship previousRel = commentPersistedEntity.createRelationshipTo(previous, PREVIOUS);
                }
            }
            commentEntity.createRelationshipTo(commentPersistedEntity, COMMENT);
            final LSDPersistedEntity ownerPersistedEntity = fountainNeo.findByURI(author);
            assert ownerPersistedEntity != null;
            commentPersistedEntity.createRelationshipTo(ownerPersistedEntity, OWNER);
            commentPersistedEntity.createRelationshipTo(ownerPersistedEntity, CREATOR);
            commentPersistedEntity.createRelationshipTo(ownerPersistedEntity, EDITOR);
            userDAO.addAuthorToNodeNoTX(author, false, commentPersistedEntity);
            commentPersistedEntity.inheritPermissions(commentEntity);
            fountainNeo.indexBy(commentPersistedEntity, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(commentPersistedEntity, LSDAttribute.URI, LSDAttribute.URI, true);
            commentPersistedEntity.timestamp();


            final int commentCount;
            if (commentEntity.hasAttribute(LSDAttribute.COMMENT_COUNT)) {
                commentCount = commentEntity.getIntegerAttribute(LSDAttribute.COMMENT_COUNT) + 1;
            } else {
                commentCount = getCommentTraverser(commentEntity, FountainNeoImpl.MAX_COMMENTS_DEFAULT).getAllNodes().size();
            }
            log.debug("Comment count is now {0}", commentCount);
            commentEntity.setAttribute(LSDAttribute.COMMENT_COUNT, String.valueOf(commentCount));
            indexDAO.syncCommentCount(commentEntity);
            indexDAO.incrementBoardActivity(commentEntity);
            return commentPersistedEntity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull @Override
    public LSDPersistedEntity createPoolNoTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidURI owner, @Nullable final LSDPersistedEntity parent, @Nonnull final LSDType type, @Nonnull final String poolName, final double x, final double y, @Nullable final String title, final boolean listed) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (parent == null) {
                throw new DataStoreException("Tried to create a pool with a null parent persistedEntityImpl.");
            }
            fountainNeo.assertAuthorized(parent, identity, LiquidPermission.MODIFY, LiquidPermission.VIEW);
            final LSDPersistedEntity persistedEntity = fountainNeo.createNode();
            persistedEntity.setIDIfNotSetOnNode();
            persistedEntity.setAttribute(LSDAttribute.LISTED, listed);

            String parentURI = parent.getAttribute(LSDAttribute.URI);
            if (!parentURI.endsWith("/")) {
                parentURI += "/";
            }
            final String newURI = parentURI + poolName.toLowerCase();
            persistedEntity.setAttribute(LSDAttribute.URI, newURI);
            persistedEntity.setAttribute(LSDAttribute.NAME, poolName);
            if (title != null) {
                persistedEntity.setAttribute(LSDAttribute.TITLE, title);
            }
            persistedEntity.setAttribute(LSDAttribute.TYPE, type.asString());
            if (!parent.hasAttribute(LSDAttribute.PERMISSIONS)) {
                throw new DataStoreException("The parent pool %s had no permissions, all pools must have permissions.", parentURI);
            }
            persistedEntity.inheritPermissions(parent);
            parent.createRelationshipTo(persistedEntity, FountainRelationships.CHILD);
            parent.modifiedTimestamp();

            final LSDPersistedEntity ownerPersistedEntity = fountainNeo.findByURIOrFail(owner);
            persistedEntity.createRelationshipTo(ownerPersistedEntity, FountainRelationships.OWNER);
            persistedEntity.createRelationshipTo(ownerPersistedEntity, FountainRelationships.CREATOR);
            persistedEntity.createRelationshipTo(ownerPersistedEntity, FountainRelationships.EDITOR);
            final LSDTransferEntity view = LSDSimpleEntity.createEmpty();
            view.setAttribute(LSDAttribute.VIEW_X, String.valueOf(x));
            view.setAttribute(LSDAttribute.VIEW_Y, String.valueOf(y));
            view.setAttribute(LSDAttribute.VIEW_WIDTH, "200");
            view.setAttribute(LSDAttribute.VIEW_HEIGHT, "200");
            createView(persistedEntity, view);
            userDAO.addAuthorToNodeNoTX(owner, false, persistedEntity);
            fountainNeo.indexBy(persistedEntity, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(persistedEntity, LSDAttribute.URI, LSDAttribute.URI, true);
            persistedEntity.timestamp();
            assertHasOwner(persistedEntity);
            indexDAO.syncBoard(persistedEntity);
            return persistedEntity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    LSDPersistedEntity createView(@Nonnull final LSDPersistedEntity object, @Nonnull final LSDTransferEntity viewEntity) throws InterruptedException {
        final LSDPersistedEntity persistedEntityImpl = fountainNeo.createNode();
        persistedEntityImpl.setAttribute(LSDAttribute.TYPE, LSDDictionaryTypes.VIEW.getValue());
        persistedEntityImpl.mergeProperties(viewEntity, false, false, null);
        fountainNeo.freeTextIndexNoTx(persistedEntityImpl);
        persistedEntityImpl.setIDIfNotSetOnNode();
        final String uri = object.getAttribute(LSDAttribute.URI) + ":view";
        persistedEntityImpl.setAttribute(LSDAttribute.URI, uri);
        object.createRelationshipTo(persistedEntityImpl, FountainRelationships.VIEW);
        persistedEntityImpl.setAttribute(LSDAttribute.PERMISSIONS, object.getAttribute(LSDAttribute.PERMISSIONS));
        persistedEntityImpl.setAttribute(LSDAttribute.VIEW_RADIUS, persistedEntityImpl.calculateRadius());
        if (!persistedEntityImpl.hasAttribute(LSDAttribute.VIEW_X)) {
            persistedEntityImpl.setAttribute(LSDAttribute.VIEW_X, Math.random() * 100 - 50);
        }

        if (!persistedEntityImpl.hasAttribute(LSDAttribute.VIEW_Y)) {
            persistedEntityImpl.setAttribute(LSDAttribute.VIEW_Y, Math.random() * 100 - 50);
        }

        fountainNeo.indexBy(persistedEntityImpl, LSDAttribute.ID, LSDAttribute.ID, true);
        fountainNeo.indexBy(persistedEntityImpl, LSDAttribute.URI, LSDAttribute.URI, true);
        persistedEntityImpl.timestamp();
        return persistedEntityImpl;
    }

    private void assertHasOwner(@Nonnull final LSDPersistedEntity poolObject) {
        final FountainRelationship ownerRel = poolObject.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
        if (ownerRel == null) {
            throw new IllegalStateException("We have a pool object with no owner.");
        }
    }

    @Nonnull @Override
    public LSDTransferEntity createPoolObjectTx(@Nonnull final LSDPersistedEntity pool, @Nonnull final LiquidSessionIdentifier identity, @Nullable final LiquidURI owner, final LiquidURI author, @Nonnull final LSDTransferEntity entity, final LiquidRequestDetailLevel detail, final boolean internal, final boolean createAuthor) throws Exception {
        if (owner == null) {
            throw new NullPointerException("Tried to create a pool without an owner.");
        }
        fountainNeo.begin();
        try {
            final LSDPersistedEntity poolObject = createPoolObjectNoTx(identity, pool, entity, owner, author, createAuthor);
            assertHasOwner(poolObject);
            final LSDPersistedEntity parent = poolObject.parentNode();
            recalculateCentreImage(parent, poolObject);
            return convertNodeToEntityWithRelatedEntitiesNoTX(identity, poolObject, parent, detail, internal, false);
        } finally {
            fountainNeo.end();
        }
    }

    private LSDPersistedEntity recalculateCentreImage(final LSDPersistedEntity pool, final LSDPersistedEntity object
                                                      /*Do not delete this parameter */) throws Exception {
        final double[] distance = {Double.MAX_VALUE};
        pool.forEachChild(new NodeCallback() {
            public void call(LSDPersistedEntity child) throws InterruptedException {
                if (!child.isDeleted() && !child.canBe(LSDDictionaryTypes.POOL) && child.hasAttribute(LSDAttribute.IMAGE_URL)) {
                    final FountainRelationship singleRelationship = child.getSingleRelationship(VIEW, Direction.OUTGOING);
                    assert singleRelationship != null;
                    LSDPersistedEntity viewNode = singleRelationship.getOtherNode(child);
                    double thisdistance = Double.valueOf(viewNode.getAttribute(LSDAttribute.VIEW_RADIUS).toString());
                    if (thisdistance < distance[0]) {
                        distance[0] = thisdistance;
                        if (child.hasAttribute(LSDAttribute.IMAGE_URL)) {
                            pool.setAttribute(LSDAttribute.ICON_URL, child.getAttribute(LSDAttribute.IMAGE_URL));
                        }
                        if (child.hasAttribute(LSDAttribute.IMAGE_WIDTH)) {
                            pool.setAttribute(LSDAttribute.ICON_WIDTH, child.getAttribute(LSDAttribute.IMAGE_WIDTH));
                        }
                        if (child.hasAttribute(LSDAttribute.IMAGE_HEIGHT)) {
                            pool.setAttribute(LSDAttribute.ICON_HEIGHT, child.getAttribute(LSDAttribute.IMAGE_HEIGHT));
                        }
                    }
                }
            }
        });
        pool.setAttribute(LSDAttribute.INTERNAL_MIN_IMAGE_RADIUS, String.valueOf(distance[0]));
        return pool;
    }

    @Nonnull
    public LSDPersistedEntity createPoolObjectNoTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LSDPersistedEntity pool, @Nonnull final LSDTransferEntity entity, @Nonnull final LiquidURI owner, @Nullable final LiquidURI author, final boolean createAuthor) throws InterruptedException {
        fountainNeo.begin();
        try {
            fountainNeo.assertAuthorized(pool, identity, LiquidPermission.MODIFY, LiquidPermission.VIEW);
            final LSDTransferEntity entityCopy = entity.copy();
            //We shouldn't be using the ID supplied to us.
            entityCopy.removeCompletely(LSDAttribute.ID);
            final String name;
            if (!entityCopy.hasAttribute(LSDAttribute.NAME)) {
                name = entityCopy.getTypeDef().getPrimaryType().getGenus().toLowerCase() + System.currentTimeMillis();
                entityCopy.setAttribute(LSDAttribute.NAME, name);
            } else {
                name = entityCopy.getAttribute(LSDAttribute.NAME);

            }

            final LSDTransferEntity viewEntity = entityCopy.removeSubEntity(LSDAttribute.VIEW);
            viewEntity.setAttribute(LSDAttribute.ID, "");
            if (author == null) {
                throw new NullPointerException("Null author passed to createPoolObjectNoTx().");
            }

            final LSDPersistedEntity persistedEntityImpl = fountainNeo.createNode();
            persistedEntityImpl.mergeProperties(entityCopy, false, false, null);
            fountainNeo.freeTextIndexNoTx(persistedEntityImpl);

            persistedEntityImpl.setIDIfNotSetOnNode();
            final String uri = pool.getAttribute(LSDAttribute.URI) + "#" + name.toLowerCase();
            persistedEntityImpl.setAttribute(LSDAttribute.URI, uri);
            pool.createRelationshipTo(persistedEntityImpl, FountainRelationships.CHILD);
            final LSDPersistedEntity ownerPersistedEntity = fountainNeo.findByURI(owner, true);
            assert ownerPersistedEntity != null;
            persistedEntityImpl.createRelationshipTo(ownerPersistedEntity, FountainRelationships.OWNER);
            persistedEntityImpl.createRelationshipTo(ownerPersistedEntity, FountainRelationships.CREATOR);
            persistedEntityImpl.createRelationshipTo(ownerPersistedEntity, FountainRelationships.EDITOR);
            userDAO.addAuthorToNodeNoTX(author, createAuthor, persistedEntityImpl);
            persistedEntityImpl.inheritPermissions(pool);
            fountainNeo.indexBy(persistedEntityImpl, LSDAttribute.ID, LSDAttribute.ID, true);
            fountainNeo.indexBy(persistedEntityImpl, LSDAttribute.URI, LSDAttribute.URI, true);
            persistedEntityImpl.timestamp();
            final LSDPersistedEntity view = createView(persistedEntityImpl, viewEntity);
            assertHasOwner(persistedEntityImpl);
            indexDAO.incrementBoardActivity(pool);
            return persistedEntityImpl;
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public void createPoolsForCazcadeAliasNoTx(@Nonnull final String name, final String fullName, final boolean systemUser) throws InterruptedException {
        createPoolsForAliasNoTx(new LiquidURI("alias:cazcade:" + name), name, fullName, systemUser);
    }

    @Override
    public void createPoolsForAliasNoTx(@Nonnull final LiquidURI aliasURI, @Nonnull final String name, final String fullName, final boolean systemUser) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (aliasURI.asString().startsWith("alias:cazcade:") && !systemUser) {
                final LiquidSessionIdentifier sessionIdentifier = new LiquidSessionIdentifier(name, null);

                final LSDPersistedEntity userPool = createPoolNoTx(FountainNeoImpl.SYSTEM_FAKE_SESSION, aliasURI, fountainNeo.getPeoplePool(), name, 0, 0, null, false);
                userPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.minimalPermissionNoDeleteValue);

                final LSDPersistedEntity dockPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".dock", 0, 0, null, false);
                dockPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.minimalPermissionNoDeleteValue);

                final LSDPersistedEntity clipBoardPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".clipboard", 0, 0, null, false);
                clipBoardPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.minimalPermissionNoDeleteValue);

                final LSDPersistedEntity streamPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "stream", 210, -210, null, false);
                streamPool.setAttribute(LSDAttribute.DESCRIPTION, "The feeds that make up your stream go here.");
                streamPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.privatePermissionNoDeleteValue);
                streamPool.setAttribute(LSDAttribute.PINNED, "true");

                final LSDTransferEntity streamFeedExplanation = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.HTML_FRAGMENT);
                streamFeedExplanation.setAttribute(LSDAttribute.NAME, "stream_feed_explanation");
                streamFeedExplanation.setAttribute(LSDAttribute.TEXT_EXTENDED, "This is where your web feeds are kept. These feeds create your stream. You can manage them in the same way as anywhere else.");
                streamFeedExplanation.setAttribute(LSDAttribute.DESCRIPTION, "Cazcade's company blog.");
                createPoolObjectNoTx(sessionIdentifier, streamPool, streamFeedExplanation, aliasURI, aliasURI, false);

                final LSDTransferEntity defaultFeedEntity = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.RSS_FEED);
                defaultFeedEntity.setAttribute(LSDAttribute.NAME, "default_cazcade_feed");
                defaultFeedEntity.setAttribute(LSDAttribute.SOURCE, "http://blog.cazcade.com/feed/");
                defaultFeedEntity.setAttribute(LSDAttribute.TITLE, "Cazcade Blog");
                defaultFeedEntity.setAttribute(LSDAttribute.DESCRIPTION, "Cazcade's company blog.");
                createPoolObjectNoTx(sessionIdentifier, streamPool, defaultFeedEntity, aliasURI, aliasURI, false);

                final LSDPersistedEntity trashPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".trash", 0, 0, null, false);
                trashPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.minimalPermissionNoDeleteValue);

                final LSDPersistedEntity inbox = createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".inbox", 0, 0, null, false);
                inbox.setAttribute(LSDAttribute.TITLE, "Inbox");
                inbox.setAttribute(LSDAttribute.DESCRIPTION, "Your inbox.");
                inbox.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.privatePermissionNoDeleteValue);

                final LSDPersistedEntity publicPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "public", -210, -210, null, false);
                publicPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.publicPermissionNoDeleteValue);
                publicPool.setAttribute(LSDAttribute.TITLE, fullName + "'s Public Board");
                publicPool.setAttribute(LSDAttribute.DESCRIPTION, "Anyone can modify this.");
                publicPool.setAttribute(LSDAttribute.PINNED, "true");

                final LSDPersistedEntity sharedPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "friends", -210, 210, null, false);
                sharedPool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.sharedPermissionNoDeleteValue);
                sharedPool.setAttribute(LSDAttribute.TITLE, fullName + "'s Friends Board");
                sharedPool.setAttribute(LSDAttribute.DESCRIPTION, "Friends can modify this.");
                sharedPool.setAttribute(LSDAttribute.PINNED, "true");

                final LSDPersistedEntity privatePool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "private", 210, 210, null, false);
                privatePool.setAttribute(LSDAttribute.TITLE, fullName + "'s Private Board");
                privatePool.setAttribute(LSDAttribute.DESCRIPTION, "Only you can view this.");
                privatePool.setAttribute(LSDAttribute.PERMISSIONS, FountainNeoImpl.privateSharedPermissionNoDeleteValue);
                privatePool.setAttribute(LSDAttribute.PINNED, "true");


                final LSDPersistedEntity profilePool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "profile", 0, 0, null, true);
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
    public void createPoolsForUserNoTx(@Nonnull final String username) throws InterruptedException {
        fountainNeo.begin();
        try {
            LSDPersistedEntity userParentPool;

            /**
             * The user pool, is being reserved for now, we may remove it later.
             */
            userParentPool = fountainNeo.findByURI(new LiquidURI("pool:///users"));

            final LiquidURI cazcadeAliasURI = new LiquidURI("alias:cazcade:" + username);
            if (userParentPool == null) {
                userParentPool = createPoolNoTx(new LiquidSessionIdentifier(FountainNeoImpl.SYSTEM, null), cazcadeAliasURI, fountainNeo
                        .getRootPool(), "users", 0, 0, null, false);
            }
            final LSDPersistedEntity userPool = createPoolNoTx(new LiquidSessionIdentifier(FountainNeoImpl.SYSTEM, null), cazcadeAliasURI, userParentPool, username, 0, 0, null, false);
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull @Override
    public LSDPersistedEntity createPoolNoTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidURI owner, final LSDPersistedEntity parent, @Nonnull final String poolName, final double x, final double y, @Nullable final String title, final boolean listed) throws InterruptedException {
        return createPoolNoTx(identity, owner, parent, LSDDictionaryTypes.POOL2D, poolName, x, y, title, listed);
    }

    @Nullable
    public LSDBaseEntity deletePoolObjectTx(@Nonnull final LiquidUUID target, final boolean internal, final LiquidRequestDetailLevel detail) throws Exception {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final LSDPersistedEntity persistedEntity = fountainNeo.findByUUID(target);
                return deletePoolObjectNoTx(internal, detail, transaction, persistedEntity);
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

    @Nonnull
    LSDTransferEntity deletePoolObjectNoTx(final boolean internal, final LiquidRequestDetailLevel detail, @Nonnull final Transaction transaction, @Nonnull final LSDPersistedEntity persistedEntity) throws Exception {
        if (persistedEntity.isDeleted()) {
            throw new DeletedEntityException("The entity %s is already deleted so cannot be deleted again.", persistedEntity.hasAttribute(LSDAttribute.URI)
                                                                                                             ? persistedEntity.getAttribute(LSDAttribute.URI)
                                                                                                             : "<unknown-uri>");
        }
        fountainNeo.delete(persistedEntity);
        final FountainRelationship relationship = persistedEntity.getSingleRelationship(FountainRelationships.CHILD, Direction.INCOMING);
        if (relationship == null) {
            throw new OrphanedEntityException("The entity %s is orphaned so cannot be deleted.", persistedEntity.hasAttribute(LSDAttribute.URI)
                                                                                                 ? persistedEntity.getAttribute(LSDAttribute.URI)
                                                                                                 : "<unknown-uri>");
        }
        recalculateCentreImage(relationship.getOtherNode(persistedEntity), persistedEntity);
        transaction.success();
        return persistedEntity.toLSD(detail, internal);
    }

    @Nonnull @Override
    public LSDTransferEntity deletePoolObjectTx(@Nonnull final LiquidURI uri, final boolean internal, final LiquidRequestDetailLevel detail) throws Exception {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final LSDPersistedEntity persistedEntity = fountainNeo.findByURI(uri);
                assert persistedEntity != null;
                return deletePoolObjectNoTx(internal, detail, transaction, persistedEntity);
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
    public Collection<LSDTransferEntity> getCommentsTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidURI uri, final int max, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final List<LSDTransferEntity> comments = new ArrayList<LSDTransferEntity>();
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final LSDPersistedEntity persistedEntity = fountainNeo.findByURI(uri);
                if (persistedEntity == null) {
                    return null;
                }
                final Traverser traverser = getCommentTraverser(persistedEntity, max);

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

    Traverser getCommentTraverser(@Nonnull final LSDPersistedEntity persistedEntity, final int max) {
        final int[] count = new int[1];
        return persistedEntity.traverse(Traverser.Order.DEPTH_FIRST, new StopEvaluator() {
                    @Override
                    public boolean isStopNode(final TraversalPosition currentPos) {
                        return count[0]++ >= max;
                    }
                }, new ReturnableEvaluator() {
                    public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                        return currentPos.currentNode()
                                         .getProperty(LSDAttribute.TYPE.getKeyName())
                                         .equals(LSDDictionaryTypes.COMMENT.getValue());
                    }
                }, VERSION_PARENT, Direction.OUTGOING, COMMENT, Direction.OUTGOING, PREVIOUS, Direction.OUTGOING
                                       );
    }

    @Nullable
    public LSDBaseEntity getPoolAndContentsNoTX(@Nonnull final LiquidUUID target, final LiquidRequestDetailLevel detail, final ChildSortOrder order, final boolean contents, final boolean internal, @Nonnull final LiquidSessionIdentifier identity, final Integer start, final Integer end, final boolean historical) throws Exception {
        fountainNeo.begin();
        try {
            final LSDPersistedEntity persistedEntityImpl = fountainNeo.findByUUID(target);
            return getPoolAndContentsNoTx(persistedEntityImpl, detail, contents, order, internal, identity, start, end, historical);
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable @Override
    public LSDTransferEntity getPoolAndContentsNoTx(@Nonnull final LiquidURI uri, final LiquidRequestDetailLevel detail, final ChildSortOrder order, final boolean contents, final boolean internal, @Nonnull final LiquidSessionIdentifier identity, final Integer start, final Integer end, final boolean historical) throws Exception {
        fountainNeo.begin();
        try {
            final LSDPersistedEntity persistedEntityImpl = fountainNeo.findByURI(uri);
            if (persistedEntityImpl == null) {
                return null;
            }
            return getPoolAndContentsNoTx(persistedEntityImpl, detail, contents, order, internal, identity, start, end, historical);
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable @Override
    public LSDTransferEntity getPoolObjectTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidURI uri, final boolean internal, final boolean historical, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final LSDPersistedEntity persistedEntity = fountainNeo.findByURI(uri);
                final LSDPersistedEntity parent = fountainNeo.findByURI(uri.getWithoutFragmentOrComment());

                if (persistedEntity == null) {
                    return null;
                }
                assertHasOwner(persistedEntity);
                assert parent != null;
                assertHasOwner(parent);
                return convertNodeToEntityWithRelatedEntitiesNoTX(identity, persistedEntity.getLatestVersionFromFork(), parent, detail, internal, historical);
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

    @Nullable @Deprecated //Use URI based methods instead
    public LSDBaseEntity getPoolObjectTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidUUID uuid, final boolean internal, final boolean historical, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final LSDPersistedEntity persistedEntity = fountainNeo.findByUUID(uuid);
                return convertNodeToEntityWithRelatedEntitiesNoTX(identity, persistedEntity.getLatestVersionFromFork(), null, detail, internal, historical);
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

    @Nonnull @Override
    public LSDPersistedEntity linkPool(final LSDPersistedEntity newOwner, @Nonnull final LSDPersistedEntity target, final LSDPersistedEntity from, @Nonnull final LSDPersistedEntity to) throws InterruptedException {
        fountainNeo.begin();
        try {
            final LSDPersistedEntity clone = linkPool(newOwner, target, to);
            unlinkPool(target);
            assertHasOwner(clone);

            return clone;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull @Override
    public LSDPersistedEntity unlinkPool(@Nonnull final LSDPersistedEntity target) throws InterruptedException {
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

    @Nonnull @Override
    public LSDPersistedEntity linkPool(final LSDPersistedEntity newOwner, @Nonnull final LSDPersistedEntity target, @Nonnull final LSDPersistedEntity to) throws InterruptedException {
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

    @Nonnull @Override
    public LSDPersistedEntity linkPoolObject(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LSDPersistedEntity newOwner, @Nonnull final LSDPersistedEntity target, final LSDPersistedEntity from, @Nonnull final LSDPersistedEntity to) throws InterruptedException {
        fountainNeo.begin();
        try {
            return linkPoolObject(editor, newOwner, target, to);
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public LSDTransferEntity linkPoolObjectTx(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LiquidURI newOwner, @Nonnull final LiquidURI target, @Nonnull final LiquidURI to, final LiquidRequestDetailLevel detail, final boolean internal) throws Exception {
        return fountainNeo.doInTransaction(new Callable<LSDTransferEntity>() {
            @Nullable @Override
            public LSDTransferEntity call() throws Exception {
                return convertNodeToEntityWithRelatedEntitiesNoTX(editor, linkPoolObject(editor, fountainNeo.findByURIOrFail(newOwner), fountainNeo
                        .findByURIOrFail(target), fountainNeo.findByURIOrFail(to)), null, detail, internal, false);
            }
        });
    }

    @Nonnull @Override
    public LSDPersistedEntity linkPoolObject(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LSDPersistedEntity newOwner, @Nonnull final LSDPersistedEntity target, @Nonnull final LSDPersistedEntity to) throws InterruptedException {
        fountainNeo.begin();
        try {
            final LSDPersistedEntity clone = fountainNeo.cloneNodeForNewVersion(editor, target, true);
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
            assert ownerRel != null;
            ownerRel.delete();
            clone.createRelationshipTo(newOwner, FountainRelationships.OWNER);
            recalculatePoolURIs(clone);
            indexDAO.incrementBoardActivity(to);

            return clone;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull @Override
    public LSDPersistedEntity movePoolObjectNoTx(@Nonnull final LiquidURI object, @Nullable final Double x, @Nullable final Double y, @Nullable final Double z) throws Exception {
        fountainNeo.begin();
        try {
            final LSDPersistedEntity persistedEntity = fountainNeo.findByURIOrFail(object);
            final FountainRelationship relationship = persistedEntity.getSingleRelationship(FountainRelationships.VIEW, Direction.OUTGOING);

            persistedEntity.parentNode().modifiedTimestamp();
            if (relationship == null) {
                throw new RelationshipNotFoundException("No view relationship for %s(%s)", persistedEntity.getAttribute(LSDAttribute.URI), object);
            }
            final LSDPersistedEntity viewPersistedEntity = relationship.getOtherNode(persistedEntity);
            //            if (viewPersistedEntity == null) {
            //                throw new EntityNotFoundException("No view relationship for %s(%s)", persistedEntity.getAttribute(LSDAttribute.URI),
            //                                                  object
            //                );
            //            }
            if (x != null) {
                viewPersistedEntity.setAttribute(LSDAttribute.VIEW_X, x.toString());
            }
            if (y != null) {
                viewPersistedEntity.setAttribute(LSDAttribute.VIEW_Y, y.toString());
            }
            if (z != null) {
                viewPersistedEntity.setAttribute(LSDAttribute.VIEW_Z, z.toString());
            }

            viewPersistedEntity.setAttribute(LSDAttribute.VIEW_RADIUS, String.valueOf(persistedEntity.calculateRadius()));
            final FountainRelationship parentRel = persistedEntity.getSingleRelationship(FountainRelationships.CHILD, Direction.INCOMING);
            if (parentRel == null) {
                throw new OrphanedEntityException("The entity %s (%s) is orphaned so cannot be moved.", object.toString(),
                        persistedEntity.hasAttribute(LSDAttribute.URI)
                        ? persistedEntity.getAttribute(LSDAttribute.URI)
                        : "<unknown-uri>");
            }
            recalculateCentreImage(parentRel.getOtherNode(persistedEntity), persistedEntity);
            indexDAO.incrementBoardActivity(parentRel.getOtherNode(persistedEntity));

            return viewPersistedEntity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable @Override
    public LSDTransferEntity selectPoolObjectTx(@Nonnull final LiquidSessionIdentifier identity, final boolean selected, @Nonnull final LiquidURI target, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {
        fountainNeo.begin();
        try {
            LSDTransferEntity newObject;
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final LSDPersistedEntity persistedEntity = fountainNeo.findByURIAndLockForWrite(target);
                persistedEntity.setAttribute(LSDAttribute.SELECTED, selected);
                newObject = convertNodeToEntityWithRelatedEntitiesNoTX(identity, persistedEntity, null, detail, internal, false);
                indexDAO.incrementBoardActivity(persistedEntity);
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

    @Nonnull @Override
    public LSDPersistedEntity unlinkPoolObject(@Nonnull final LSDPersistedEntity target) throws InterruptedException {
        fountainNeo.begin();
        try {
            target.parentNode().modifiedTimestamp();

            if (target.hasRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING)) {
                throw new CannotUnlinkEntityException("Cannot unlink a persistedEntityImpl that is not the latest version.");
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

    @Nonnull @Override
    public LSDTransferEntity updatePool(@Nonnull final LiquidSessionIdentifier sessionIdentifier, @Nonnull final LSDPersistedEntity pool, final LiquidRequestDetailLevel detail, final boolean internal, final boolean historical, final Integer end, final int start, final ChildSortOrder order, final boolean contents, @Nonnull final LSDTransferEntity requestEntity, final Runnable onRenameAction) throws Exception {
        final LSDPersistedEntity resultPersistedEntity = fountainNeo.updateNodeAndReturnNodeNoTx(sessionIdentifier, pool, requestEntity, onRenameAction);
        indexDAO.syncBoard(pool);
        pool.modifiedTimestamp();

        return getPoolAndContentsNoTx(resultPersistedEntity, detail, contents, order, internal, sessionIdentifier, start, end, historical);
    }

    @Nonnull @Override
    public LSDTransferEntity getPoolAndContentsNoTx(@Nonnull final LSDPersistedEntity targetPersistedEntity, final LiquidRequestDetailLevel detail, final boolean contents, final ChildSortOrder order, final boolean internal, @Nonnull final LiquidSessionIdentifier identity, @Nullable Integer start, @Nullable Integer end, final boolean historical) throws Exception {
        Integer endActual = end;
        Integer startActual = start;
        fountainNeo.begin();
        try {
            final LSDPersistedEntity pool = convertToPoolFromPoolOrObject(targetPersistedEntity);
            final LSDTransferEntity entity = convertNodeToEntityWithRelatedEntitiesNoTX(identity, pool, null, detail, internal, historical);
            if (contents) {
                final List<LSDBaseEntity> entities = new ArrayList<LSDBaseEntity>();
                final int count = 0;
                pool.forEachChild(new NodeCallback() {
                    public void call(@Nonnull final LSDPersistedEntity child) throws Exception {
                        final LSDBaseEntity poolObjectEntity = convertNodeToEntityWithRelatedEntitiesNoTX(identity, child, pool, detail, internal, false);
                        if (targetPersistedEntity.equals(child)) {
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
                if (endActual == null) {
                    endActual = entities.size() - 1;
                }
                if (startActual == null) {
                    startActual = 0;
                }
                entity.addSubEntities(LSDAttribute.CHILD, entities.subList(startActual, endActual >= entities.size()
                                                                                        ? entities.size()
                                                                                        : endActual + 1));
                entity.setAttribute(LSDAttribute.POPULARITY_METRIC, String.valueOf(targetPersistedEntity.popularity()));
                indexDAO.addMetrics(pool, entity);
            }
            return entity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull LSDPersistedEntity convertToPoolFromPoolOrObject(@Nonnull final LSDPersistedEntity persistedEntityImpl) {
        final LSDPersistedEntity pool;
        if (persistedEntityImpl.getAttribute(LSDAttribute.TYPE).startsWith(LSDDictionaryTypes.POOL.getValue())) {
            pool = persistedEntityImpl;
        } else {
            pool = persistedEntityImpl.parentNode();
        }
        return pool;
    }

    @Nonnull @Override
    public LSDTransferEntity updatePoolObjectNoTx(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LiquidSessionIdentifier editor, @Nonnull final LSDTransferEntity entity, @Nullable final LSDPersistedEntity pool, @Nonnull final LSDPersistedEntity origPersistedEntity, final boolean internal, final LiquidRequestDetailLevel detail) throws InterruptedException {
        final LSDTransferEntity entityCopy = entity.copy();
        entityCopy.removeSubEntity(LSDAttribute.VIEW);
        entityCopy.removeSubEntity(LSDAttribute.AUTHOR);
        entityCopy.removeSubEntity(LSDAttribute.OWNER);
        entityCopy.removeValue(LSDAttribute.ID);
        final LSDTransferEntity newObject;
        origPersistedEntity.parentNode().modifiedTimestamp();

        final LSDPersistedEntity persistedEntity = copyPoolObjectForUpdate(editor, origPersistedEntity, false);
        if (!entity.getURI().toString().toLowerCase().equals(origPersistedEntity.getAttribute(LSDAttribute.URI))) {
            throw new CannotChangeURIException("Tried to change the URI of %s to %s", origPersistedEntity.getAttribute(LSDAttribute.URI), entity
                    .getURI()
                    .toString());
        }
        newObject = convertNodeToEntityWithRelatedEntitiesNoTX(identity, persistedEntity.mergeProperties(entityCopy, true, false, new Runnable() {
            @Override
            public void run() {
                try {
                    recalculatePoolURIs(persistedEntity);
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }
        }), pool, detail, internal, false);
        assertHasOwner(persistedEntity);
        if (pool != null) {
            indexDAO.incrementBoardActivity(pool);
        }
        fountainNeo.freeTextIndexNoTx(persistedEntity);
        return newObject;
    }

    @Nonnull
    LSDPersistedEntity copyPoolObjectForUpdate(@Nonnull final LiquidSessionIdentifier editor, @Nonnull final LSDPersistedEntity persistedEntityImpl, final boolean fork) throws InterruptedException {
        persistedEntityImpl.assertLatestVersion();
        return fountainNeo.cloneNodeForNewVersion(editor, persistedEntityImpl, fork);
    }

    @Nonnull
    public LSDTransferEntity convertNodeToEntityWithRelatedEntitiesNoTX(@Nonnull final LiquidSessionIdentifier identity, @Nonnull final LSDPersistedEntity persistedEntity, @Nullable final LSDPersistedEntity parent, final LiquidRequestDetailLevel detail, final boolean internal, final boolean historical) throws InterruptedException {
        fountainNeo.begin();
        try {

            final LSDTransferEntity entity = persistedEntity.toLSD(detail, internal);
            boolean done = false;
            final LiquidRequestDetailLevel aliasDetailLevel = detail == LiquidRequestDetailLevel.COMPLETE
                                                              ? LiquidRequestDetailLevel.COMPLETE
                                                              : LiquidRequestDetailLevel.PERSON_MINIMAL;

            final Iterable<FountainRelationship> iterable = persistedEntity.getRelationships(VIEW, Direction.OUTGOING);
            for (final FountainRelationship relationship : iterable) {
                if (done) {
                    throw new DuplicateEntityException("Found a second view for a single object.");
                }
                entity.addSubEntity(LSDAttribute.VIEW, relationship.getOtherNode(persistedEntity).toLSD(detail, internal), true);
                done = true;
            }

            if (detail == LiquidRequestDetailLevel.COMPLETE ||
                detail == LiquidRequestDetailLevel.NORMAL ||
                detail == LiquidRequestDetailLevel.BOARD_LIST) {
                final FountainRelationship ownerRel = persistedEntity.getSingleRelationship(OWNER, Direction.OUTGOING);
                if (ownerRel != null) {
                    entity.addSubEntity(LSDAttribute.OWNER, userDAO.getAliasFromNode(ownerRel.getOtherNode(persistedEntity), internal, aliasDetailLevel), true);
                }

                if (detail != LiquidRequestDetailLevel.BOARD_LIST) {
                    final FountainRelationship relationship = persistedEntity.getSingleRelationship(FountainRelationships.AUTHOR, Direction.OUTGOING);
                    if (relationship != null) {
                        entity.addSubEntity(LSDAttribute.AUTHOR, userDAO.getAliasFromNode(relationship.getOtherNode(persistedEntity), internal, aliasDetailLevel), true);
                    }

                    final FountainRelationship editorRel = persistedEntity.getSingleRelationship(EDITOR, Direction.OUTGOING);
                    if (editorRel != null) {
                        entity.addSubEntity(LSDAttribute.EDITOR, userDAO.getAliasFromNode(editorRel.getOtherNode(persistedEntity), internal, aliasDetailLevel), true);
                    }
                }
            }

            if (historical) {
                final List<LSDBaseEntity> history = new ArrayList<LSDBaseEntity>();
                LSDPersistedEntity version = persistedEntity;
                while (version.hasRelationship(VERSION_PARENT, Direction.OUTGOING)) {
                    final FountainRelationship relationship = version.getSingleRelationship(VERSION_PARENT, Direction.OUTGOING);
                    assert relationship != null;
                    version = relationship.getOtherNode(version);
                    history.add(version.toLSD(detail, false));
                }
                entity.addSubEntities(LSDAttribute.HISTORY, history);
            }

            persistedEntity.setPermissionFlagsOnEntity(identity, parent, entity);
            return entity;
        } finally {
            fountainNeo.end();
        }
    }

    public void recalculatePoolURIs(@Nonnull final LSDPersistedEntity persistedEntity) throws InterruptedException {
        fountainNeo.recalculateURI(persistedEntity);
        final Traverser traverser = persistedEntity.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return currentPos.currentNode().hasProperty(LSDAttribute.URI.getKeyName());
            }
        }, FountainRelationships.CHILD, Direction.OUTGOING);
        for (final org.neo4j.graphdb.Node childNode : traverser) {
            fountainNeo.recalculateURI(new FountainEntityImpl(childNode));
        }
    }

    @Override
    public void visitNodeNoTx(@Nonnull final LSDPersistedEntity entity, @Nonnull final LiquidSessionIdentifier identity) throws InterruptedException {
        fountainNeo.begin();
        try {
            final LiquidUUID session = identity.getSession();
            final LSDPersistedEntity sessionPersistedEntity = fountainNeo.findByUUID(session);
            for (final FountainRelationship relationship : sessionPersistedEntity.getRelationships(FountainRelationships.VISITING, Direction.OUTGOING)) {
                relationship.delete();
            }
            final LSDPersistedEntity pool = convertToPoolFromPoolOrObject(entity);
            final FountainRelationship relationshipTo = sessionPersistedEntity.createRelationshipTo(pool, FountainRelationships.VISITING);
            relationshipTo.setProperty(LSDAttribute.UPDATED.getKeyName(), String.valueOf(System.currentTimeMillis()));
            sessionPersistedEntity.setAttribute(LSDAttribute.ACTIVE, true);
            sessionPersistedEntity.timestamp();
            indexDAO.incrementBoardActivity(entity);
            indexDAO.visitBoard(entity, identity.getAliasURL());
        } finally {
            fountainNeo.end();
        }
    }
}