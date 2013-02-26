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
import static cazcade.liquid.api.lsd.Types.T_HTML_FRAGMENT;
import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;

public class FountainPoolDAOImpl implements FountainPoolDAO {
    @Nonnull
    private static final Logger log = Logger.getLogger(FountainPoolDAOImpl.class);
    @Autowired
    private FountainNeo              fountainNeo;
    @Autowired
    private FountainIndexServiceImpl indexDAO;
    @Autowired
    private FountainUserDAO          userDAO;


    public FountainPoolDAOImpl() {


    }

    @Override @Nonnull
    public PersistedEntity addCommentNoTX(@Nullable final PersistedEntity entityToCommentOn, @Nonnull final TransferEntity comment, @Nullable final LiquidURI author) throws InterruptedException {
        fountainNeo.begin();
        try {
            final TransferEntity entityCopy = comment.$();
            if (author == null) {
                throw new NullPointerException("Null author passed to addComment().");
            }
            if (entityToCommentOn == null) {
                throw new NullPointerException("Null persistedEntityImpl passed to addComment().");
            }
            entityCopy.removeChild(Dictionary.AUTHOR_A);
            final PersistedEntity newComment = fountainNeo.createNode();
            newComment.mergeProperties(entityCopy, false, false, null);
            newComment.$(Dictionary.TYPE, Types.T_COMMENT.getValue());
            fountainNeo.freeTextIndexNoTx(newComment);

            newComment.setIDIfNotSetOnNode();
            if (!entityCopy.has$(Dictionary.NAME)) {
                final String name = entityCopy.type().getPrimaryType().getGenus().toLowerCase() + System.currentTimeMillis();
                entityCopy.$(Dictionary.NAME, name);
            }
            final String uri = entityToCommentOn.$(Dictionary.URI) + '~' + "comment-" + author.sub().sub() +
                               System.currentTimeMillis();
            newComment.$(Dictionary.URI, uri);

            if (entityToCommentOn.has(FountainRelationships.COMMENT, OUTGOING)) {
                final Iterable<FountainRelationship> comments = entityToCommentOn.relationships(FountainRelationships.COMMENT, OUTGOING);
                for (final FountainRelationship relationship : comments) {
                    final PersistedEntity previous = relationship.end();
                    relationship.delete();
                    final FountainRelationship previousRel = newComment.relate(previous, PREVIOUS);
                }
            }
            entityToCommentOn.relate(newComment, FountainRelationships.COMMENT);
            final PersistedEntity ownerEntity = fountainNeo.find(author);
            assert ownerEntity != null;
            newComment.relate(ownerEntity, OWNER);
            newComment.relate(ownerEntity, CREATOR);
            newComment.relate(ownerEntity, EDITOR);
            userDAO.addAuthorToNodeNoTX(author, false, newComment);
            newComment.inheritPermissions(entityToCommentOn);
            fountainNeo.indexBy(newComment, Dictionary.ID, Dictionary.ID, true);
            fountainNeo.indexBy(newComment, Dictionary.URI, Dictionary.URI, true);
            newComment.timestamp();


            final int commentCount;
            if (entityToCommentOn.has$(Dictionary.COMMENT_COUNT)) {
                commentCount = entityToCommentOn.$i(Dictionary.COMMENT_COUNT) + 1;
            } else {
                commentCount = getCommentTraverser(entityToCommentOn, FountainNeoImpl.MAX_COMMENTS_DEFAULT).getAllNodes().size();
            }
            log.debug("Comment count is now {0}", commentCount);
            entityToCommentOn.$(Dictionary.COMMENT_COUNT, String.valueOf(commentCount));
            indexDAO.syncCommentCount(entityToCommentOn);
            indexDAO.incrementBoardActivity(entityToCommentOn);
            return newComment;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull
    public TransferEntity convertNodeToEntityWithRelatedEntitiesNoTX(@Nonnull final SessionIdentifier identity, @Nonnull final PersistedEntity persistedEntity, @Nullable final PersistedEntity parent, final RequestDetailLevel detail, final boolean internal, final boolean historical) throws Exception {
        fountainNeo.begin();
        try {

            final TransferEntity entity = persistedEntity.toTransfer(detail, internal);
            boolean done = false;
            final RequestDetailLevel aliasDetailLevel = detail == RequestDetailLevel.COMPLETE
                                                        ? RequestDetailLevel.COMPLETE
                                                        : RequestDetailLevel.PERSON_MINIMAL;

            final Iterable<FountainRelationship> iterable = persistedEntity.relationships(VIEW, OUTGOING);
            for (final FountainRelationship relationship : iterable) {
                if (done) {
                    throw new DuplicateEntityException("Found a second view for a single object.");
                }
                entity.child(Dictionary.VIEW_ENTITY, relationship.other(persistedEntity).toTransfer(detail, internal), true);
                done = true;
            }

            if (detail == RequestDetailLevel.COMPLETE ||
                detail == RequestDetailLevel.NORMAL ||
                detail == RequestDetailLevel.BOARD_LIST) {
                final FountainRelationship ownerRel = persistedEntity.relationship(OWNER, OUTGOING);
                if (ownerRel != null) {
                    entity.child(Dictionary.A_OWNER, userDAO.getAliasFromNode(ownerRel.other(persistedEntity), internal, aliasDetailLevel), true);
                }

                if (detail != RequestDetailLevel.BOARD_LIST) {
                    final FountainRelationship relationship = persistedEntity.relationship(AUTHOR, OUTGOING);
                    if (relationship != null) {
                        entity.child(Dictionary.AUTHOR_A, userDAO.getAliasFromNode(relationship.other(persistedEntity), internal, aliasDetailLevel), true);
                    }

                    final FountainRelationship editorRel = persistedEntity.relationship(EDITOR, OUTGOING);
                    if (editorRel != null) {
                        entity.child(Dictionary.EDITOR_A, userDAO.getAliasFromNode(editorRel.other(persistedEntity), internal, aliasDetailLevel), true);
                    }
                }
            }

            if (historical) {
                final List<Entity> history = new ArrayList<Entity>();
                PersistedEntity version = persistedEntity;
                while (version.has(VERSION_PARENT, OUTGOING)) {
                    final FountainRelationship relationship = version.relationship(VERSION_PARENT, OUTGOING);
                    assert relationship != null;
                    version = relationship.other(version);
                    history.add(version.toTransfer(detail, false));
                }
                entity.children(Dictionary.HISTORY_A, history);
            }

            persistedEntity.setPermissionFlagsOnEntity(identity, parent, entity);
            return entity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull @Override
    public FountainEntity createPoolNoTx(@Nonnull final SessionIdentifier identity, @Nonnull final LiquidURI owner, @Nullable final PersistedEntity parent, @Nonnull final Type type, @Nonnull final String poolName, final double x, final double y, @Nullable final String title, final boolean listed) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (parent == null) {
                throw new DataStoreException("Tried to create a pool with a null parent persistedEntityImpl.");
            }
            fountainNeo.assertAuthorized(parent, identity, Permission.MODIFY_PERM, Permission.VIEW_PERM);
            final FountainEntity poolEntity = (FountainEntity) fountainNeo.createNode();
            poolEntity.setIDIfNotSetOnNode();
            poolEntity.$(Dictionary.LISTED, listed);

            String parentURI = parent.$(Dictionary.URI);
            if (!parentURI.endsWith("/")) {
                parentURI += "/";
            }
            final String newURI = parentURI + poolName.toLowerCase();
            poolEntity.$(Dictionary.URI, newURI);
            poolEntity.$(Dictionary.NAME, poolName);
            if (title != null) {
                poolEntity.$(Dictionary.TITLE, title);
            }
            poolEntity.$(Dictionary.TYPE, type.asString());
            if (!parent.has$(Dictionary.PERMISSIONS)) {
                throw new DataStoreException("The parent pool %s had no permissions, all pools must have permissions.", parentURI);
            }
            poolEntity.inheritPermissions(parent);
            parent.relate(poolEntity, CHILD);
            parent.modifiedTimestamp();

            final PersistedEntity ownerEntity = fountainNeo.findOrFail(owner);
            poolEntity.relate(ownerEntity, OWNER);
            poolEntity.relate(ownerEntity, CREATOR);
            poolEntity.relate(ownerEntity, EDITOR);
            createView(poolEntity, SimpleEntity.createEmpty()
                                               .$(Dictionary.VIEW_X, String.valueOf(x))
                                               .$(Dictionary.VIEW_Y, String.valueOf(y))
                                               .$(Dictionary.VIEW_WIDTH, "200")
                                               .$(Dictionary.VIEW_HEIGHT, "200"));
            userDAO.addAuthorToNodeNoTX(owner, false, poolEntity);
            fountainNeo.indexBy(poolEntity, Dictionary.ID, Dictionary.ID, true);
            fountainNeo.indexBy(poolEntity, Dictionary.URI, Dictionary.URI, true);
            poolEntity.timestamp();
            assertHasOwner(poolEntity);
            indexDAO.syncBoard(poolEntity);
            return poolEntity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull @Override
    public FountainEntity createPoolNoTx(@Nonnull final SessionIdentifier identity, @Nonnull final LiquidURI owner, final PersistedEntity parent, @Nonnull final String poolName, final double x, final double y, @Nullable final String title, final boolean listed) throws InterruptedException {
        return createPoolNoTx(identity, owner, parent, Types.T_POOL2D, poolName, x, y, title, listed);
    }

    @Nonnull @Override
    public TransferEntity createPoolObjectTx(@Nonnull final PersistedEntity pool, @Nonnull final SessionIdentifier identity, @Nullable final LiquidURI owner, final LiquidURI author, @Nonnull final TransferEntity entity, final RequestDetailLevel detail, final boolean internal, final boolean createAuthor) throws Exception {
        if (owner == null) {
            throw new NullPointerException("Tried to create a pool without an owner.");
        }
        fountainNeo.begin();
        try {
            final PersistedEntity poolObject = createPoolObjectNoTx(identity, pool, entity, owner, author, createAuthor);
            assertHasOwner(poolObject);
            final PersistedEntity parent = poolObject.parent();
            recalculateCentreImage(parent, poolObject);
            return convertNodeToEntityWithRelatedEntitiesNoTX(identity, poolObject, parent, detail, internal, false);
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public void createPoolsForAliasNoTx(@Nonnull final LiquidURI aliasURI, @Nonnull final String name, final String fullName, final boolean systemUser) throws InterruptedException {
        fountainNeo.begin();
        try {
            if (aliasURI.asString().startsWith("alias:cazcade:") && !systemUser) {
                final SessionIdentifier sessionIdentifier = new SessionIdentifier(name, null);

                final PersistedEntity userPool = createPoolNoTx(FountainNeoImpl.SYSTEM_FAKE_SESSION, aliasURI, fountainNeo.getPeoplePool(), name, 0, 0, null, false)
                        .$(Dictionary.PERMISSIONS, FountainNeoImpl.minimalPermissionNoDeleteValue);

                createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".dock", 0, 0, null, false).$(Dictionary.PERMISSIONS, FountainNeoImpl.minimalPermissionNoDeleteValue);

                createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".clipboard", 0, 0, null, false).$(Dictionary.PERMISSIONS, FountainNeoImpl.minimalPermissionNoDeleteValue);

                final PersistedEntity streamPool = createPoolNoTx(sessionIdentifier, aliasURI, userPool, "stream", 210, -210, null, false)
                        .$(Dictionary.DESCRIPTION, "The feeds that make up your stream go here.")
                        .$(Dictionary.PERMISSIONS, FountainNeoImpl.privatePermissionNoDeleteValue)
                        .$(Dictionary.PINNED, "true");

                SimpleEntity<? extends TransferEntity<?>> streamfeed = SimpleEntity.create(T_HTML_FRAGMENT);
                createPoolObjectNoTx(sessionIdentifier, streamPool, streamfeed.$(Dictionary.NAME, "stream_feed_explanation")
                                                                              .$(Dictionary.TEXT_EXTENDED, "This is where your web feeds are kept. These feeds create your stream. You can manage them in the same way as anywhere else.")
                                                                              .$(Dictionary.DESCRIPTION, "Cazcade's company blog."), aliasURI, aliasURI, false);

                SimpleEntity<? extends TransferEntity<?>> lsdTransferEntityLSDSimpleEntity = SimpleEntity.create(Types.T_RSS_FEED);
                createPoolObjectNoTx(sessionIdentifier, streamPool, lsdTransferEntityLSDSimpleEntity.$(Dictionary.NAME, "default_cazcade_feed")
                                                                                                    .$(Dictionary.SOURCE, "http://blog.cazcade.com/feed/")
                                                                                                    .$(Dictionary.TITLE, "Cazcade Blog")
                                                                                                    .$(Dictionary.DESCRIPTION, "Cazcade's company blog."), aliasURI, aliasURI, false);

                createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".trash", 0, 0, null, false).$(Dictionary.PERMISSIONS, FountainNeoImpl.minimalPermissionNoDeleteValue);

                createPoolNoTx(sessionIdentifier, aliasURI, userPool, ".inbox", 0, 0, null, false).$(Dictionary.TITLE, "Inbox")
                        .$(Dictionary.DESCRIPTION, "Your inbox.")
                        .$(Dictionary.PERMISSIONS, FountainNeoImpl.privatePermissionNoDeleteValue);

                createPoolNoTx(sessionIdentifier, aliasURI, userPool, "public", -210, -210, null, false).$(Dictionary.PERMISSIONS, FountainNeoImpl.publicPermissionNoDeleteValue)
                        .$(Dictionary.TITLE, fullName + "'s Public Board")
                        .$(Dictionary.DESCRIPTION, "Anyone can modify this.")
                        .$(Dictionary.PINNED, "true");

                createPoolNoTx(sessionIdentifier, aliasURI, userPool, "friends", -210, 210, null, false).$(Dictionary.PERMISSIONS, FountainNeoImpl.sharedPermissionNoDeleteValue)
                        .$(Dictionary.TITLE, fullName + "'s Friends Board")
                        .$(Dictionary.DESCRIPTION, "Friends can modify this.")
                        .$(Dictionary.PINNED, "true");

                createPoolNoTx(sessionIdentifier, aliasURI, userPool, "private", 210, 210, null, false).$(Dictionary.TITLE, fullName
                                                                                                                            + "'s Private Board")
                        .$(Dictionary.DESCRIPTION, "Only you can view this.")
                        .$(Dictionary.PERMISSIONS, FountainNeoImpl.privateSharedPermissionNoDeleteValue)
                        .$(Dictionary.PINNED, "true");


                createPoolNoTx(sessionIdentifier, aliasURI, userPool, "profile", 0, 0, null, true).$(Dictionary.TITLE, fullName
                                                                                                                       + "'s Profile Board")
                        .$(Dictionary.DESCRIPTION, "This is all about you.")
                        .$(Dictionary.PINNED, "true")
                        .$(Dictionary.PERMISSIONS, FountainNeoImpl.defaultPermissionNoDeleteValue);
            }
        } finally {
            fountainNeo.end();
        }
    }

    @Override
    public void createPoolsForCazcadeAliasNoTx(@Nonnull final String name, final String fullName, final boolean systemUser) throws InterruptedException {
        createPoolsForAliasNoTx(new LiquidURI("alias:cazcade:" + name), name, fullName, systemUser);
    }

    @Override
    public void createPoolsForUserNoTx(@Nonnull final String username) throws InterruptedException {
        fountainNeo.begin();
        try {
            PersistedEntity userParentPool;

            /**
             * The user pool, is being reserved for now, we may remove it later.
             */
            userParentPool = fountainNeo.find(new LiquidURI("pool:///users"));

            final LiquidURI cazcadeAliasURI = new LiquidURI("alias:cazcade:" + username);
            if (userParentPool == null) {
                userParentPool = createPoolNoTx(new SessionIdentifier(FountainNeoImpl.SYSTEM, null), cazcadeAliasURI, fountainNeo.getRootPool(), "users", 0, 0, null, false);
            }
            final PersistedEntity userPool = createPoolNoTx(new SessionIdentifier(FountainNeoImpl.SYSTEM, null), cazcadeAliasURI, userParentPool, username, 0, 0, null, false);
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull @Override
    public TransferEntity deletePoolObjectTx(@Nonnull final LiquidURI uri, final boolean internal, final RequestDetailLevel detail) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<TransferEntity>() {
            @Override public TransferEntity call() throws Exception {
                return deletePoolObjectNoTx(internal, detail, fountainNeo.getCurrentTransaction(), fountainNeo.findOrFail(uri));
            }
        });
    }

    @Nullable
    public Collection<TransferEntity> getCommentsTx(@Nonnull final SessionIdentifier identity, @Nonnull final LiquidURI uri, final int max, final boolean internal, final RequestDetailLevel detail) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<Collection<TransferEntity>>() {
            @Override public Collection<TransferEntity> call() throws Exception {
                final List<TransferEntity> comments = new ArrayList<TransferEntity>();
                for (final org.neo4j.graphdb.Node comment : getCommentTraverser(fountainNeo.findOrFail(uri), max)) {
                    comments.add(convertNodeToEntityWithRelatedEntitiesNoTX(identity, new FountainEntity(comment), null, detail, internal, false));
                }
                SortUtil.dateSortEntities(comments);
                return comments;
            }
        });

    }

    @Nonnull @Override
    public TransferEntity getPoolAndContentsNoTx(@Nonnull final PersistedEntity targetPersistedEntity, final RequestDetailLevel detail, final boolean contents, final ChildSortOrder order, final boolean internal, @Nonnull final SessionIdentifier identity, @Nullable Integer start, @Nullable Integer end, final boolean historical) throws Exception {
        Integer endActual = end;
        Integer startActual = start;
        fountainNeo.begin();
        try {
            final PersistedEntity pool = convertToPoolFromPoolOrObject(targetPersistedEntity);
            final TransferEntity entity = convertNodeToEntityWithRelatedEntitiesNoTX(identity, pool, null, detail, internal, historical);
            if (contents) {
                final List<Entity> entities = new ArrayList<Entity>();
                final int count = 0;
                pool.forEachChild(new NodeCallback() {
                    public void call(@Nonnull final PersistedEntity child) throws Exception {
                        final Entity poolObjectEntity = convertNodeToEntityWithRelatedEntitiesNoTX(identity, child, pool, detail, internal, false);
                        if (targetPersistedEntity.equals(child)) {
                            poolObjectEntity.$(Dictionary.HAS_FOCUS, "true");
                        }
                        if (child.isAuthorized(identity, Permission.VIEW_PERM)) {
                            entities.add(poolObjectEntity);
                        }
                        poolObjectEntity.$(Dictionary.POPULARITY_METRIC, child.popularity());
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
                entity.children(Dictionary.CHILD_A, entities.subList(startActual, endActual >= entities.size()
                                                                                  ? entities.size()
                                                                                  : endActual + 1));
                entity.$(Dictionary.POPULARITY_METRIC, targetPersistedEntity.popularity());
                indexDAO.addMetrics(pool, entity);
            }
            return entity;
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable @Override
    public TransferEntity getPoolAndContentsNoTx(@Nonnull final LiquidURI uri, final RequestDetailLevel detail, final ChildSortOrder order, final boolean contents, final boolean internal, @Nonnull final SessionIdentifier identity, final Integer start, final Integer end, final boolean historical) throws Exception {
        return fountainNeo.doInTransaction(new Callable<TransferEntity>() {
            @Override public TransferEntity call() throws Exception {
                return getPoolAndContentsNoTx(fountainNeo.findOrFail(uri), detail, contents, order, internal, identity, start, end, historical);
            }
        });
    }

    @Nullable @Override
    public TransferEntity getPoolObjectTx(@Nonnull final SessionIdentifier identity, @Nonnull final LiquidURI uri, final boolean internal, final boolean historical, final RequestDetailLevel detail) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<TransferEntity>() {
            @Override public TransferEntity call() throws Exception {
                final PersistedEntity persistedEntity = fountainNeo.findOrFail(uri);
                final PersistedEntity parent = fountainNeo.findOrFail(uri.withoutFragmentOrComment());
                assertHasOwner(persistedEntity);
                assertHasOwner(parent);
                return convertNodeToEntityWithRelatedEntitiesNoTX(identity, persistedEntity.getLatestVersionFromFork(), parent, detail, internal, historical);
            }
        });
    }

    @Nonnull @Override
    public PersistedEntity linkPool(final PersistedEntity newOwner, @Nonnull final PersistedEntity target, final PersistedEntity from, @Nonnull final PersistedEntity to) throws Exception {
        return fountainNeo.doInBeginBlock(new Callable<PersistedEntity>() {
            @Override public PersistedEntity call() throws Exception {
                final PersistedEntity clone = linkPool(newOwner, target, to);
                unlinkPool(target);
                assertHasOwner(clone);

                return clone;
            }
        });
    }

    @Nonnull @Override
    public PersistedEntity linkPool(final PersistedEntity newOwner, @Nonnull final PersistedEntity target, @Nonnull final PersistedEntity to) throws Exception {
        return fountainNeo.doInBeginBlock(new Callable<PersistedEntity>() {
            @Override public PersistedEntity call() throws Exception {
                final String candidateURI = to.$(Dictionary.URI) + "#" + target.$(Dictionary.NAME);
                String uri = candidateURI;
                int count = 1;
                while (fountainNeo.find(new LiquidURI(uri)) != null) {
                    uri = candidateURI + count++;
                }
                target.$(Dictionary.URI, uri);
                fountainNeo.reindex(target, Dictionary.URI, Dictionary.URI);
                to.relate(target, LINKED_CHILD);
                //            target.relate(newOwner, FountainRelationships.OWNER);
                assertHasOwner(target);
                indexDAO.incrementBoardActivity(to);
                indexDAO.incrementBoardActivity(target);

                return target;
            }
        });
    }

    @Nonnull @Override
    public PersistedEntity linkPoolObject(@Nonnull final SessionIdentifier editor, @Nonnull final PersistedEntity newOwner, @Nonnull final PersistedEntity target, final PersistedEntity from, @Nonnull final PersistedEntity to) throws Exception {
        return linkPoolObject(editor, newOwner, target, to);
    }

    @Nonnull @Override
    public PersistedEntity linkPoolObject(@Nonnull final SessionIdentifier editor, @Nonnull final PersistedEntity newOwner, @Nonnull final PersistedEntity target, @Nonnull final PersistedEntity to) throws Exception {
        return fountainNeo.doInBeginBlock(new Callable<PersistedEntity>() {
            @Override public PersistedEntity call() throws Exception {
                final PersistedEntity clone = fountainNeo.cloneNodeForNewVersion(editor, target, true);
                assertHasOwner(clone);
                String candidateURI = to.$(Dictionary.URI) + "#" + clone.$(Dictionary.NAME);
                String name;
                int count = 1;
                while (fountainNeo.find(new LiquidURI(candidateURI)) != null) {
                    name = clone.$(Dictionary.NAME) + count++;
                    candidateURI = to.$(Dictionary.URI) + "#" + name;
                }
                clone.$(Dictionary.URI, candidateURI);
                fountainNeo.reindex(clone, Dictionary.URI, Dictionary.URI);
                to.relate(clone, CHILD);
                clone.relationship(OWNER, OUTGOING).delete();
                clone.relate(newOwner, OWNER);
                recalculatePoolURIs(clone);
                indexDAO.incrementBoardActivity(to);

                return clone;
            }
        });
    }

    @Override
    public TransferEntity linkPoolObjectTx(@Nonnull final SessionIdentifier editor, @Nonnull final LiquidURI newOwner, @Nonnull final LiquidURI target, @Nonnull final LiquidURI to, final RequestDetailLevel detail, final boolean internal) throws Exception {
        return fountainNeo.doInTransaction(new Callable<TransferEntity>() {
            @Nullable @Override
            public TransferEntity call() throws Exception {
                return convertNodeToEntityWithRelatedEntitiesNoTX(editor, linkPoolObject(editor, fountainNeo.findOrFail(newOwner), fountainNeo
                        .findOrFail(target), fountainNeo.findOrFail(to)), null, detail, internal, false);
            }
        });
    }

    @Nonnull @Override
    public PersistedEntity movePoolObjectNoTx(@Nonnull final LiquidURI object, @Nullable final Double x, @Nullable final Double y, @Nullable final Double z) throws Exception {
        return fountainNeo.doInBeginBlock(new Callable<PersistedEntity>() {
            @Override public PersistedEntity call() throws Exception {
                final PersistedEntity persisted = fountainNeo.findOrFail(object);
                final FountainRelationship relationship = persisted.relationship(VIEW, OUTGOING);

                persisted.parent().modifiedTimestamp();
                if (relationship == null) {
                    throw new RelationshipNotFoundException("No view relationship for %s(%s)", persisted.$(Dictionary.URI), object);
                }
                final PersistedEntity view = relationship.other(persisted);
                if (x != null) {
                    view.$(Dictionary.VIEW_X, x);
                }
                if (y != null) {
                    view.$(Dictionary.VIEW_Y, y);
                }
                if (z != null) {
                    view.$(Dictionary.VIEW_Z, z);
                }

                view.$(Dictionary.VIEW_RADIUS, persisted.calculateRadius());
                final FountainRelationship parentRel = persisted.relationship(CHILD, INCOMING);
                if (parentRel == null) {
                    throw new OrphanedEntityException("The entity %s (%s) is orphaned so cannot be moved.", object.toString(),
                            persisted.has$(Dictionary.URI)
                            ? persisted.$(Dictionary.URI)
                            : "<unknown-uri>");
                }
                recalculateCentreImage(parentRel.other(persisted), persisted);
                indexDAO.incrementBoardActivity(parentRel.other(persisted));

                return view;
            }
        });
    }

    public void recalculatePoolURIs(@Nonnull final PersistedEntity persistedEntity) throws InterruptedException {
        fountainNeo.recalculateURI(persistedEntity);
        final Traverser traverser = persistedEntity.traverse(Traverser.Order.DEPTH_FIRST, StopEvaluator.END_OF_GRAPH, new ReturnableEvaluator() {
            public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                return currentPos.currentNode().hasProperty(Dictionary.URI.getKeyName());
            }
        }, CHILD, OUTGOING);
        for (final org.neo4j.graphdb.Node childNode : traverser) {
            fountainNeo.recalculateURI(new FountainEntity(childNode));
        }
    }

    @Nullable @Override
    public TransferEntity selectPoolObjectTx(@Nonnull final SessionIdentifier identity, final boolean selected, @Nonnull final LiquidURI target, final boolean internal, final RequestDetailLevel detail) throws Exception {
        return fountainNeo.doInTransactionAndBeginBlock(new Callable<TransferEntity>() {
            @Override public TransferEntity call() throws Exception {
                TransferEntity newObject;
                final FountainEntity poolObject = (FountainEntity) fountainNeo.findForWrite(target)
                                                                              .$(Dictionary.SELECTED, selected);
                newObject = convertNodeToEntityWithRelatedEntitiesNoTX(identity, poolObject, null, detail, internal, false);
                indexDAO.incrementBoardActivity(poolObject);
                return newObject;
            }
        });

    }

    @Nonnull @Override
    public PersistedEntity unlinkPool(@Nonnull final PersistedEntity target) throws InterruptedException {
        fountainNeo.begin();
        try {
            Iterable<FountainRelationship> relationships = target.relationships(CHILD, INCOMING);
            for (final FountainRelationship relationship : relationships) {
                relationship.delete();
            }
            Iterable<FountainRelationship> relationships1 = target.relationships(LINKED_CHILD, INCOMING);
            for (final FountainRelationship relationship : relationships1) {
                relationship.delete();
            }
            assertHasOwner(target);

            fountainNeo.getIndexService().remove(target.getNeoNode(), Dictionary.URI.getKeyName());
            indexDAO.incrementBoardActivity(target);

            return target;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull @Override
    public PersistedEntity unlinkPoolObject(@Nonnull final PersistedEntity target) throws InterruptedException {
        fountainNeo.begin();
        try {
            target.parent().modifiedTimestamp();

            if (target.has(VERSION_PARENT, INCOMING)) {
                throw new CannotUnlinkEntityException("Cannot unlink a persistedEntityImpl that is not the latest version.");
            }
            Iterable<FountainRelationship> relationships = target.relationships(CHILD, INCOMING);
            for (final FountainRelationship relationship : relationships) {
                relationship.delete();
            }
            Iterable<FountainRelationship> relationships1 = target.relationships(LINKED_CHILD, INCOMING);
            for (final FountainRelationship relationship : relationships1) {
                relationship.delete();
            }
            fountainNeo.getIndexService().remove(target.getNeoNode(), Dictionary.URI.getKeyName());
            assertHasOwner(target);
            return target;
        } finally {
            fountainNeo.end();
        }
    }

    @Nonnull @Override
    public TransferEntity updatePool(@Nonnull final SessionIdentifier sessionIdentifier, @Nonnull final PersistedEntity pool, final RequestDetailLevel detail, final boolean internal, final boolean historical, final Integer end, final int start, final ChildSortOrder order, final boolean contents, @Nonnull final TransferEntity requestEntity, final Runnable onRenameAction) throws Exception {
        final PersistedEntity resultPersistedEntity = fountainNeo.updateNodeAndReturnNodeNoTx(sessionIdentifier, pool, requestEntity, onRenameAction);
        indexDAO.syncBoard(pool);
        pool.modifiedTimestamp();

        return getPoolAndContentsNoTx(resultPersistedEntity, detail, contents, order, internal, sessionIdentifier, start, end, historical);
    }

    @Nonnull @Override
    public TransferEntity updatePoolObjectNoTx(@Nonnull final SessionIdentifier identity, @Nonnull final SessionIdentifier editor, @Nonnull final TransferEntity entity, @Nullable final PersistedEntity pool, @Nonnull final PersistedEntity origPersistedEntity, final boolean internal, final RequestDetailLevel detail) throws Exception {
        final TransferEntity entityCopy = entity.$();
        entityCopy.removeChild(Dictionary.VIEW_ENTITY);
        entityCopy.removeChild(Dictionary.AUTHOR_A);
        entityCopy.removeChild(Dictionary.A_OWNER);
        entityCopy.remove$(Dictionary.ID);
        final TransferEntity newObject;
        origPersistedEntity.parent().modifiedTimestamp();

        final PersistedEntity persistedEntity = copyPoolObjectForUpdate(editor, origPersistedEntity, false);
        if (!entity.uri().toString().toLowerCase().equals(origPersistedEntity.$(Dictionary.URI))) {
            throw new CannotChangeURIException("Tried to change the URI of %s to %s", origPersistedEntity.$(Dictionary.URI), entity.uri()
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

    @Override
    public void visitNodeNoTx(@Nonnull final PersistedEntity entity, @Nonnull final SessionIdentifier identity) throws Exception {
        fountainNeo.doInBeginBlock(new Callable<Object>() {
            @Override public Object call() throws Exception {
                final LiquidUUID session = identity.session();
                final FountainEntity sessionPersistedEntity = fountainNeo.find(session);
                for (final FountainRelationship relationship : sessionPersistedEntity.relationships(VISITING, OUTGOING)) {
                    relationship.delete();
                }
                final PersistedEntity pool = convertToPoolFromPoolOrObject(entity);
                sessionPersistedEntity.$(Dictionary.ACTIVE, true)
                                      .timestamp()
                                      .relate(pool, VISITING)
                                      .$(Dictionary.UPDATED.getKeyName(), System.currentTimeMillis());
                indexDAO.incrementBoardActivity(entity);
                indexDAO.visitBoard(entity, identity.aliasURI());
                return null;
            }
        });
    }

    @Nonnull
    PersistedEntity createView(@Nonnull final PersistedEntity object, @Nonnull final TransferEntity viewEntity) throws InterruptedException {
        final PersistedEntity view = fountainNeo.createNode()
                                                .$(Dictionary.TYPE, Types.T_VIEW.getValue())
                                                .mergeProperties(viewEntity, false, false, null);
        fountainNeo.freeTextIndexNoTx(view);
        final String uri = object.$(Dictionary.URI) + ":view";
        view.setIDIfNotSetOnNode()
            .$(Dictionary.URI, uri)
            .$(Dictionary.PERMISSIONS, object.$(Dictionary.PERMISSIONS))
            .$(Dictionary.VIEW_RADIUS, view.calculateRadius());
        if (!view.has$(Dictionary.VIEW_X)) {
            view.$(Dictionary.VIEW_X, Math.random() * 100 - 50);
        }

        if (!view.has$(Dictionary.VIEW_Y)) {
            view.$(Dictionary.VIEW_Y, Math.random() * 100 - 50);
        }
        object.relate(view, VIEW);
        fountainNeo.indexBy(view, Dictionary.ID, Dictionary.ID, true);
        fountainNeo.indexBy(view, Dictionary.URI, Dictionary.URI, true);
        view.timestamp();
        return view;
    }

    private void assertHasOwner(@Nonnull final PersistedEntity poolObject) {
        final FountainRelationship ownerRel = poolObject.relationship(OWNER, OUTGOING);
        if (ownerRel == null) {
            throw new IllegalStateException("We have a pool object with no owner.");
        }
    }

    private PersistedEntity recalculateCentreImage(final PersistedEntity pool, final PersistedEntity object
                                                      /*Do not delete this parameter */) throws Exception {
        final double[] distance = {Double.MAX_VALUE};
        pool.forEachChild(new NodeCallback() {
            public void call(PersistedEntity child) throws InterruptedException {
                if (!child.deleted() && !child.canBe(Types.T_POOL) && child.has$(Dictionary.IMAGE_URL)) {
                    double thisdistance = child.relationship(VIEW, OUTGOING).other(child).$d(Dictionary.VIEW_RADIUS);
                    if (thisdistance < distance[0]) {
                        distance[0] = thisdistance;
                        if (child.has$(Dictionary.IMAGE_URL)) {
                            pool.$(Dictionary.ICON_URL, child.$(Dictionary.IMAGE_URL));
                        }
                        if (child.has$(Dictionary.IMAGE_WIDTH)) {
                            pool.$(Dictionary.ICON_WIDTH, child.$(Dictionary.IMAGE_WIDTH));
                        }
                        if (child.has$(Dictionary.IMAGE_HEIGHT)) {
                            pool.$(Dictionary.ICON_HEIGHT, child.$(Dictionary.IMAGE_HEIGHT));
                        }
                    }
                }
            }
        });
        pool.$(Dictionary.INTERNAL_MIN_IMAGE_RADIUS, String.valueOf(distance[0]));
        return pool;
    }

    @Nonnull
    public PersistedEntity createPoolObjectNoTx(@Nonnull final SessionIdentifier identity, @Nonnull final PersistedEntity parent, @Nonnull final TransferEntity entity, @Nonnull final LiquidURI owner, @Nullable final LiquidURI author, final boolean createAuthor) throws InterruptedException {
        fountainNeo.begin();
        try {
            fountainNeo.assertAuthorized(parent, identity, Permission.MODIFY_PERM, Permission.VIEW_PERM);
            final TransferEntity entityCopy = entity.$();
            //We shouldn't be using the ID supplied to us.
            entityCopy.removeCompletely(Dictionary.ID);
            final String name;
            if (!entityCopy.has$(Dictionary.NAME)) {
                name = entityCopy.type().getPrimaryType().getGenus().toLowerCase() + System.currentTimeMillis();
                entityCopy.$(Dictionary.NAME, name);
            } else {
                name = entityCopy.$(Dictionary.NAME);

            }

            final TransferEntity viewEntity = (TransferEntity) entityCopy.removeChild(Dictionary.VIEW_ENTITY);
            viewEntity.$(Dictionary.ID, "");
            if (author == null) {
                throw new NullPointerException("Null author passed to createPoolObjectNoTx().");
            }

            final PersistedEntity persistedPool = fountainNeo.createNode();
            persistedPool.mergeProperties(entityCopy, false, false, null);
            fountainNeo.freeTextIndexNoTx(persistedPool);

            final String uri = parent.$(Dictionary.URI) + "#" + name.toLowerCase();
            persistedPool.setIDIfNotSetOnNode().$(Dictionary.URI, uri);
            parent.relate(persistedPool, CHILD);

            final PersistedEntity ownerEntity = fountainNeo.findOrFail(owner);
            persistedPool.relate(ownerEntity, OWNER);
            persistedPool.relate(ownerEntity, CREATOR);
            persistedPool.relate(ownerEntity, EDITOR);
            userDAO.addAuthorToNodeNoTX(author, createAuthor, persistedPool);
            persistedPool.inheritPermissions(parent).timestamp();
            fountainNeo.indexBy(persistedPool, Dictionary.ID, Dictionary.ID, true);
            fountainNeo.indexBy(persistedPool, Dictionary.URI, Dictionary.URI, true);
            final PersistedEntity view = createView(persistedPool, viewEntity);
            assertHasOwner(persistedPool);
            indexDAO.incrementBoardActivity(parent);
            return persistedPool;
        } finally {
            fountainNeo.end();
        }
    }

    @Nullable
    public Entity deletePoolObjectTx(@Nonnull final LiquidUUID target, final boolean internal, final RequestDetailLevel detail) throws Exception {
        fountainNeo.begin();
        try {
            final Transaction transaction = fountainNeo.beginTx();
            try {
                final PersistedEntity persistedEntity = fountainNeo.find(target);
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
    TransferEntity deletePoolObjectNoTx(final boolean internal, final RequestDetailLevel detail, @Nonnull final Transaction transaction, @Nonnull final PersistedEntity persistedEntity) throws Exception {
        if (persistedEntity.deleted()) {
            throw new DeletedEntityException("The entity %s is already deleted so cannot be deleted again.", persistedEntity.has$(Dictionary.URI)
                                                                                                             ? persistedEntity.$(Dictionary.URI)
                                                                                                             : "<unknown-uri>");
        }
        fountainNeo.delete(persistedEntity);
        final FountainRelationship relationship = persistedEntity.relationship(CHILD, INCOMING);
        if (relationship == null) {
            throw new OrphanedEntityException("The entity %s is orphaned so cannot be deleted.", persistedEntity.has$(Dictionary.URI)
                                                                                                 ? persistedEntity.$(Dictionary.URI)
                                                                                                 : "<unknown-uri>");
        }
        recalculateCentreImage(relationship.other(persistedEntity), persistedEntity);
        transaction.success();
        return persistedEntity.toTransfer(detail, internal);
    }

    Traverser getCommentTraverser(@Nonnull final PersistedEntity persistedEntity, final int max) {
        final int[] count = new int[1];
        return persistedEntity.traverse(Traverser.Order.DEPTH_FIRST, new StopEvaluator() {
                    @Override
                    public boolean isStopNode(final TraversalPosition currentPos) {
                        return count[0]++ >= max;
                    }
                }, new ReturnableEvaluator() {
                    public boolean isReturnableNode(@Nonnull final TraversalPosition currentPos) {
                        return currentPos.currentNode()
                                         .getProperty(Dictionary.TYPE.getKeyName())
                                         .equals(Types.T_COMMENT.getValue());
                    }
                }, VERSION_PARENT, OUTGOING, FountainRelationships.COMMENT, OUTGOING, PREVIOUS, OUTGOING
                                       );
    }


    @Nonnull PersistedEntity convertToPoolFromPoolOrObject(@Nonnull final PersistedEntity persistedEntityImpl) {
        final PersistedEntity pool;
        if (persistedEntityImpl.$(Dictionary.TYPE).startsWith(Types.T_POOL.getValue())) {
            pool = persistedEntityImpl;
        } else {
            pool = persistedEntityImpl.parent();
        }
        return pool;
    }

    @Nonnull
    PersistedEntity copyPoolObjectForUpdate(@Nonnull final SessionIdentifier editor, @Nonnull final PersistedEntity pool, final boolean fork) throws InterruptedException {
        pool.assertLatestVersion();
        return fountainNeo.cloneNodeForNewVersion(editor, pool, fork);
    }
}