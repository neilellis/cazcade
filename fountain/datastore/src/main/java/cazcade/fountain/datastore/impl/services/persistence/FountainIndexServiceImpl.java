/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.fountain.index.model.BoardType;
import cazcade.fountain.index.persistence.dao.AliasDAO;
import cazcade.fountain.index.persistence.dao.BoardDAO;
import cazcade.fountain.index.persistence.entities.AliasEntity;
import cazcade.fountain.index.persistence.entities.BoardIndexEntity;
import cazcade.fountain.index.persistence.entities.VisitEntity;
import cazcade.liquid.api.BoardURL;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
import cazcade.liquid.api.lsd.Types;
import org.neo4j.graphdb.Direction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class FountainIndexServiceImpl {
    private static final Logger log = Logger.getLogger(FountainIndexServiceImpl.class);


    @Autowired
    private FountainNeo fountainNeo;
    @Autowired
    private BoardDAO    boardDAO;

    @Autowired
    private AliasDAO aliasDAO;

    public void addMetrics(@Nonnull final PersistedEntity pool, @Nonnull final Entity entity) {
        final BoardIndexEntity board = boardDAO.getOrCreateBoard(pool.uri().asString());
        entity.$(Dictionary.VISITS_METRIC, board.getVisitCount());
        entity.$(Dictionary.REGISTERED_VISITORS_METRIC, boardDAO.getUniqueVisitorCount(board));
    }

    @Transactional
    public void incrementBoardActivity(@Nonnull final PersistedEntity pool) {
        if (!isBoard(pool)) {
            return;
        }

        final BoardIndexEntity board = getBoardForNode(pool);
        if (board == null) {
            return;
        }
        board.incrementActivity();
        updateBoardPopularity(board);
    }

    @Nullable
    private BoardIndexEntity getBoardForNode(@Nonnull final PersistedEntity persistedEntity) {
        final String uri = persistedEntity.$(Dictionary.URI);
        if (persistedEntity.canBe(Types.T_BOARD)) {
            final BoardIndexEntity board = boardDAO.getOrCreateBoard(uri);
            return board;
        } else {
            return null;
        }
    }

    @Transactional
    public void syncBoard(@Nonnull final PersistedEntity persistedEntity) {
        if (!isBoard(persistedEntity)) {
            return;
        }

        final String uri = persistedEntity.$(Dictionary.URI);
        if (BoardURL.isConvertable(uri)) {
            final BoardIndexEntity board = boardDAO.getOrCreateBoard(uri);
            final BoardURL shortUrl = new BoardURL(uri);
            board.setShortUrl(shortUrl.toString());
            final boolean listed = persistedEntity.$bool(Dictionary.LISTED);
            board.setListed(listed);
            if (shortUrl.publicBoard()) {
                board.setType(BoardType.PUBLIC);
            } else if (shortUrl.personal()) {
                board.setType(BoardType.PERSONAL);
            } else if (shortUrl.profile()) {
                board.setType(BoardType.PROFILE);
            }
            addCoreMetadataToBoard(persistedEntity, board);
            addOwnershipToBoard(persistedEntity, board);
            syncCommentCountInternal(persistedEntity, board);
            syncFollowerCountInternal(persistedEntity, board);
            updateBoardPopularity(board);
            board.setUpdated(persistedEntity.updated());
            boardDAO.saveBoard(board);
        }
    }

    private boolean isBoard(@Nonnull final PersistedEntity persistedEntity) {
        final Object type = persistedEntity.$(Dictionary.TYPE);
        return type.toString().startsWith(Types.T_BOARD.getValue());
    }

    private static void addCoreMetadataToBoard(@Nonnull final PersistedEntity entity, @Nonnull final BoardIndexEntity board) {
        if (entity.has(Dictionary.DESCRIPTION)) {
            board.setDescription(entity.$(Dictionary.DESCRIPTION));
        }
        if (entity.has(Dictionary.TEXT_EXTENDED)) {
            board.setText(entity.$(Dictionary.TEXT_EXTENDED));
        }
        if (entity.has(Dictionary.TITLE)) {
            board.setTitle(entity.$(Dictionary.TITLE));
        }
    }

    private void addOwnershipToBoard(@Nonnull final PersistedEntity persistedEntity, @Nonnull final BoardIndexEntity board) {
        final FountainRelationship ownerRel = persistedEntity.relationship(FountainRelationships.OWNER, Direction.OUTGOING);
        if (ownerRel != null) {
            final String owner = ownerRel.other(persistedEntity).default$(Dictionary.URI, "unknown");
            log.debug("Setting owner as {0} on {1}", owner, board.getUri());
            board.setOwner(aliasDAO.getOrCreateAlias(owner));
        }
        final FountainRelationship authorRel = persistedEntity.relationship(FountainRelationships.AUTHOR, Direction.OUTGOING);
        if (authorRel != null) {
            final String author = authorRel.other(persistedEntity).default$(Dictionary.URI, "unknown");
            log.debug("Setting author as {0} on {1}", author, board.getUri());
            board.setAuthor(aliasDAO.getOrCreateAlias(author));
        }
        final FountainRelationship creatorRel = persistedEntity.relationship(FountainRelationships.CREATOR, Direction.OUTGOING);
        if (creatorRel != null) {
            final String creator = creatorRel.other(persistedEntity).default$(Dictionary.URI, "unknown");
            log.debug("Setting creator as {0} on {1}", creator, board.getUri());
            board.setCreator(aliasDAO.getOrCreateAlias(creator));
        }
    }

    private void syncCommentCountInternal(@Nonnull final Entity persistedEntity, @Nonnull final BoardIndexEntity board) {
        long commentCount = 0;
        try {
            commentCount = persistedEntity.default$i(Dictionary.COMMENT_COUNT, 0);
        } catch (NumberFormatException e) {
            log.error(e);
        }
        board.setCommentCount(commentCount);
    }

    private static void syncFollowerCountInternal(@Nonnull final Entity persistedEntity, @Nonnull final BoardIndexEntity board) {
        long aliasFollowsCount = 0;
        try {
            aliasFollowsCount = persistedEntity.default$i(Dictionary.FOLLOWERS_COUNT, 0);
        } catch (NumberFormatException e) {
            log.error(e);
            aliasFollowsCount = 0;
        }
        board.setFollowerCount(aliasFollowsCount);
    }

    private static void updateBoardPopularity(@Nonnull final BoardIndexEntity board) {
        board.setPopularity((long) (10000 * (Math.log10(board.getCommentCount() + 1) +
                                             Math.log10(board.getActivityCount() + 1) +
                                             Math.log10(board.getFollowerCount() + 1) +
                                             Math.log10(board.getLikeCount() + 1))));
    }

    @Transactional
    public void syncCommentCount(@Nonnull final PersistedEntity persistedEntity) {
        if (!isBoard(persistedEntity)) {
            return;
        }

        final BoardIndexEntity board = getBoardForNode(persistedEntity);
        if (board == null) {
            return;
        }
        syncCommentCountInternal(persistedEntity, board);
        updateBoardPopularity(board);
    }

    @Transactional
    public void syncFollowCounts(final PersistedEntity persistedEntity) {
        //this is for aliases, we don't support this yet
    }

    @Transactional
    public void syncFollowerCount(@Nonnull final PersistedEntity persistedEntity) {
        if (!isBoard(persistedEntity)) {
            return;
        }

        final BoardIndexEntity board = getBoardForNode(persistedEntity);
        if (board == null) {
            return;
        }
        syncFollowerCountInternal(persistedEntity, board);
        updateBoardPopularity(board);
    }

    @Transactional
    public void visitBoard(@Nonnull final PersistedEntity persistedEntity, @Nonnull final LURI visitor) {
        if (!isBoard(persistedEntity)) {
            return;
        }

        final BoardIndexEntity board = getBoardForNode(persistedEntity);
        if (board == null) {
            return;
        }
        final VisitEntity visitEntity = new VisitEntity();
        visitEntity.setBoard(board);
        visitEntity.setVisitor(aliasDAO.getOrCreateAlias(visitor.toString()));
        visitEntity.setCreated(new Date());
        boardDAO.addVisit(visitEntity);
        board.incrementVisits();
        updateBoardPopularity(board);
    }

    private void syncFollowsCountsInternal(final PersistedEntity persistedEntity, final AliasEntity board) {
        //todo: this is for aliases
    }

    public void setBoardDAO(final BoardDAO boardDAO) {
        this.boardDAO = boardDAO;
    }

    /*

        Just copy these values from the node.

        Change the client's visit pool request to specify an optional type for the pool.

        Make all boards have a different type to plain pools.

        Then just add the query methods.


     */


    public void setFountainNeo(final FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }
}
