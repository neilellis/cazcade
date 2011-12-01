package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.fountain.datastore.impl.FountainEntity;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.index.model.BoardType;
import cazcade.fountain.index.persistence.dao.AliasDAO;
import cazcade.fountain.index.persistence.dao.BoardDAO;
import cazcade.fountain.index.persistence.entities.AliasEntity;
import cazcade.fountain.index.persistence.entities.BoardIndexEntity;
import cazcade.fountain.index.persistence.entities.VisitEntity;
import cazcade.liquid.api.LiquidBoardURL;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import org.neo4j.graphdb.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

/**
 * @author neilellis@cazcade.com
 */
public class FountainIndexServiceImpl {

    private final Logger log = LoggerFactory.getLogger(FountainIndexServiceImpl.class);


    @Autowired
    private FountainNeo fountainNeo;
    @Autowired
    private BoardDAO boardDAO;

    @Autowired
    private AliasDAO aliasDAO;

    private boolean isBoard(@Nonnull final FountainEntity fountainEntity) {
        final Object type = fountainEntity.getAttribute(LSDAttribute.TYPE);
        if (type == null) {
            throw new NullPointerException("FountainEntity " + fountainEntity.getAttribute(LSDAttribute.URI) + " has no type");
        }
        return type.toString().startsWith(LSDDictionaryTypes.BOARD.getValue());
    }

    private void addCoreMetadataToBoard(@Nonnull final FountainEntity fountainEntity, @Nonnull final BoardIndexEntity board) {
        board.setDescription(fountainEntity.getAttribute(LSDAttribute.DESCRIPTION, null));
        board.setText(fountainEntity.getAttribute(LSDAttribute.TEXT_EXTENDED, null));
        board.setTitle(fountainEntity.getAttribute(LSDAttribute.TITLE, null));
    }


    @Transactional
    public void syncBoard(@Nonnull final FountainEntity fountainEntity) {
        if (!isBoard(fountainEntity)) {
            return;
        }

        final String uri = fountainEntity.getAttribute(LSDAttribute.URI);
        if (LiquidBoardURL.isConvertable(uri)) {
            final BoardIndexEntity board = boardDAO.getOrCreateBoard(uri);
            final LiquidBoardURL shortUrl = new LiquidBoardURL(uri);
            board.setShortUrl(shortUrl.toString());
            final boolean listed = fountainEntity.getBooleanAttribute(LSDAttribute.LISTED);
            board.setListed(listed);
            if (shortUrl.isPublicBoard()) {
                board.setType(BoardType.PUBLIC);
            } else if (shortUrl.isPersonalBoard()) {
                board.setType(BoardType.PERSONAL);
            } else if (shortUrl.isProfileBoard()) {
                board.setType(BoardType.PROFILE);
            }
            addCoreMetadataToBoard(fountainEntity, board);
            addOwnershipToBoard(fountainEntity, board);
            syncCommentCountInternal(fountainEntity, board);
            syncFollowerCountInternal(fountainEntity, board);
            updateBoardPopularity(board);
            board.setUpdated(fountainEntity.getUpdated());
            boardDAO.saveBoard(board);
        }
    }

    private void addOwnershipToBoard(@Nonnull final FountainEntity fountainEntity, @Nonnull final BoardIndexEntity board) {
        final FountainRelationship ownerRel = fountainEntity.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
        if (ownerRel != null) {
            final String owner = ownerRel.getOtherNode(fountainEntity).getAttribute(LSDAttribute.URI, "unknown");
            log.debug("Setting owner as {0} on {1}", owner, board.getUri());
            board.setOwner(aliasDAO.getOrCreateAlias(owner));
        }
        final FountainRelationship authorRel = fountainEntity.getSingleRelationship(FountainRelationships.AUTHOR, Direction.OUTGOING);
        if (authorRel != null) {
            final String author = authorRel.getOtherNode(fountainEntity).getAttribute(LSDAttribute.URI, "unknown");
            log.debug("Setting author as {0} on {1}", author, board.getUri());
            board.setAuthor(aliasDAO.getOrCreateAlias(author));
        }
        final FountainRelationship creatorRel = fountainEntity.getSingleRelationship(FountainRelationships.CREATOR, Direction.OUTGOING);
        if (creatorRel != null) {
            final String creator = creatorRel.getOtherNode(fountainEntity).getAttribute(LSDAttribute.URI, "unknown");
            log.debug("Setting creator as {0} on {1}", creator, board.getUri());
            board.setCreator(aliasDAO.getOrCreateAlias(creator));
        }
    }

    private void syncFollowsCountsInternal(final FountainEntity fountainEntity, final AliasEntity board) {

        //todo: this is for aliases

    }

    private void syncFollowerCountInternal(@Nonnull final FountainEntity fountainEntity, @Nonnull final BoardIndexEntity board) {
        final long aliasFollowsCount = fountainEntity.getIntegerAttribute(LSDAttribute.FOLLOWERS_COUNT, 0);
        board.setFollowerCount(aliasFollowsCount);
    }

    private void syncCommentCountInternal(@Nonnull final FountainEntity fountainEntity, @Nonnull final BoardIndexEntity board) {
        final long commentCount = fountainEntity.getIntegerAttribute(LSDAttribute.COMMENT_COUNT, 0);
        board.setCommentCount(commentCount);
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

    public void setBoardDAO(final BoardDAO boardDAO) {
        this.boardDAO = boardDAO;
    }

    @Transactional
    public void incrementBoardActivity(@Nonnull final FountainEntity pool) {
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
    private BoardIndexEntity getBoardForNode(@Nonnull final FountainEntity fountainEntity) {

        final String uri = fountainEntity.getAttribute(LSDAttribute.URI);
        if (fountainEntity.canBe(LSDDictionaryTypes.BOARD)) {
            final BoardIndexEntity board = boardDAO.getOrCreateBoard(uri);
            return board;
        } else {
            return null;
        }
    }

    @Transactional
    public void syncCommentCount(@Nonnull final FountainEntity fountainEntity) {
        if (!isBoard(fountainEntity)) {
            return;
        }

        final BoardIndexEntity board = getBoardForNode(fountainEntity);
        if (board == null) {
            return;
        }
        syncCommentCountInternal(fountainEntity, board);
        updateBoardPopularity(board);
    }


    @Transactional
    public void visitBoard(@Nonnull final FountainEntity fountainEntity, @Nonnull final LiquidURI visitor) {
        if (!isBoard(fountainEntity)) {
            return;
        }

        final BoardIndexEntity board = getBoardForNode(fountainEntity);
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

    @Transactional
    public void syncFollowCounts(final FountainEntity fountainEntity) {
        //this is for aliases, we don't support this yet
    }

    @Transactional
    public void syncFollowerCount(@Nonnull final FountainEntity fountainEntity) {
        if (!isBoard(fountainEntity)) {
            return;
        }

        final BoardIndexEntity board = getBoardForNode(fountainEntity);
        if (board == null) {
            return;
        }
        syncFollowerCountInternal(fountainEntity, board);
        updateBoardPopularity(board);
    }

    private void updateBoardPopularity(@Nonnull final BoardIndexEntity board) {
        board.setPopularity((long) (10000 * (Math.log10(board.getCommentCount() + 1) + Math.log10(board.getActivityCount() + 1) + Math.log10(board.getFollowerCount() + 1) + Math.log10(board.getLikeCount() + 1))));
    }

    public void addMetrics(@Nonnull final FountainEntity pool, @Nonnull final LSDEntity entity) {
        final BoardIndexEntity board = boardDAO.getOrCreateBoard(pool.getURI().asString());
        entity.setAttribute(LSDAttribute.VISITS_METRIC, String.valueOf(board.getVisitCount()));
        entity.setAttribute(LSDAttribute.REGISTERED_VISITORS_METRIC, boardDAO.getUniqueVisitorCount(board));
    }
}
