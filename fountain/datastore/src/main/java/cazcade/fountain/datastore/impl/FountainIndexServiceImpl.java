package cazcade.fountain.datastore.impl;

import cazcade.fountain.datastore.Node;
import cazcade.fountain.datastore.Relationship;
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

    private boolean isBoard(@Nonnull Node node) {
        final Object type = node.getProperty(LSDAttribute.TYPE);
        if (type == null) {
            throw new NullPointerException("Node " + node.getProperty(LSDAttribute.URI) + " has no type");
        }
        return type.toString().startsWith(LSDDictionaryTypes.BOARD.getValue());
    }

    private void addCoreMetadataToBoard(@Nonnull Node node, @Nonnull BoardIndexEntity board) {
        board.setDescription(node.getProperty(LSDAttribute.DESCRIPTION, null));
        board.setText(node.getProperty(LSDAttribute.TEXT_EXTENDED, null));
        board.setTitle(node.getProperty(LSDAttribute.TITLE, null));
    }


    @Transactional
    public void syncBoard(@Nonnull Node node) {
        if (!isBoard(node)) {
            return;
        }

        String uri = node.getProperty(LSDAttribute.URI);
        if (LiquidBoardURL.isConvertable(uri)) {
            BoardIndexEntity board = boardDAO.getOrCreateBoard(uri);
            LiquidBoardURL shortUrl = new LiquidBoardURL(uri);
            board.setShortUrl(shortUrl.toString());
            boolean listed = node.getBooleanAttribute(LSDAttribute.LISTED);
            board.setListed(listed);
            if (shortUrl.isPublicBoard()) {
                board.setType(BoardType.PUBLIC);
            } else if (shortUrl.isPersonalBoard()) {
                board.setType(BoardType.PERSONAL);
            } else if (shortUrl.isProfileBoard()) {
                board.setType(BoardType.PROFILE);
            }
            addCoreMetadataToBoard(node, board);
            addOwnershipToBoard(node, board);
            syncCommentCountInternal(node, board);
            syncFollowerCountInternal(node, board);
            updateBoardPopularity(board);
            board.setUpdated(node.getUpdated());
            boardDAO.saveBoard(board);
        }
    }

    private void addOwnershipToBoard(@Nonnull Node node, @Nonnull BoardIndexEntity board) {
        final Relationship ownerRel = node.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
        if (ownerRel != null) {
            final String owner = ownerRel.getOtherNode(node).getProperty(LSDAttribute.URI, "unknown");
            log.debug("Setting owner as {0} on {1}", owner, board.getUri());
            board.setOwner(aliasDAO.getOrCreateAlias(owner));
        }
        final Relationship authorRel = node.getSingleRelationship(FountainRelationships.AUTHOR, Direction.OUTGOING);
        if (authorRel != null) {
            final String author = authorRel.getOtherNode(node).getProperty(LSDAttribute.URI, "unknown");
            log.debug("Setting author as {0} on {1}", author, board.getUri());
            board.setAuthor(aliasDAO.getOrCreateAlias(author));
        }
        final Relationship creatorRel = node.getSingleRelationship(FountainRelationships.CREATOR, Direction.OUTGOING);
        if (creatorRel != null) {
            final String creator = creatorRel.getOtherNode(node).getProperty(LSDAttribute.URI, "unknown");
            log.debug("Setting creator as {0} on {1}", creator, board.getUri());
            board.setCreator(aliasDAO.getOrCreateAlias(creator));
        }
    }

    private void syncFollowsCountsInternal(Node node, AliasEntity board) {

        //todo: this is for aliases

    }

    private void syncFollowerCountInternal(@Nonnull Node node, @Nonnull BoardIndexEntity board) {
        long aliasFollowsCount = node.getIntegerAttribute(LSDAttribute.FOLLOWERS_COUNT, 0);
        board.setFollowerCount(aliasFollowsCount);
    }

    private void syncCommentCountInternal(@Nonnull Node node, @Nonnull BoardIndexEntity board) {
        long commentCount = node.getIntegerAttribute(LSDAttribute.COMMENT_COUNT, 0);
        board.setCommentCount(commentCount);
    }

    /*

        Just copy these values from the node.

        Change the client's visit pool request to specify an optional type for the pool.

        Make all boards have a different type to plain pools.

        Then just add the query methods.


     */


    public void setFountainNeo(FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }

    public void setBoardDAO(BoardDAO boardDAO) {
        this.boardDAO = boardDAO;
    }

    @Transactional
    public void incrementBoardActivity(@Nonnull Node pool) {
        if (!isBoard(pool)) {
            return;
        }

        BoardIndexEntity board = getBoardForNode(pool);
        if (board == null) {
            return;
        }
        board.incrementActivity();
        updateBoardPopularity(board);
    }

    @Nullable
    private BoardIndexEntity getBoardForNode(@Nonnull Node node) {

        String uri = node.getProperty(LSDAttribute.URI);
        if (node.canBe(LSDDictionaryTypes.BOARD)) {
            BoardIndexEntity board = boardDAO.getOrCreateBoard(uri);
            return board;
        } else {
            return null;
        }
    }

    @Transactional
    public void syncCommentCount(@Nonnull Node node) {
        if (!isBoard(node)) {
            return;
        }

        BoardIndexEntity board = getBoardForNode(node);
        if (board == null) {
            return;
        }
        syncCommentCountInternal(node, board);
        updateBoardPopularity(board);
    }


    @Transactional
    public void visitBoard(@Nonnull Node node, @Nonnull LiquidURI visitor) {
        if (!isBoard(node)) {
            return;
        }

        BoardIndexEntity board = getBoardForNode(node);
        if (board == null) {
            return;
        }
        VisitEntity visitEntity = new VisitEntity();
        visitEntity.setBoard(board);
        visitEntity.setVisitor(aliasDAO.getOrCreateAlias(visitor.toString()));
        visitEntity.setCreated(new Date());
        boardDAO.addVisit(visitEntity);
        board.incrementVisits();
        updateBoardPopularity(board);

    }

    @Transactional
    public void syncFollowCounts(Node node) {
        //this is for aliases, we don't support this yet
    }

    @Transactional
    public void syncFollowerCount(@Nonnull Node node) {
        if (!isBoard(node)) {
            return;
        }

        BoardIndexEntity board = getBoardForNode(node);
        if (board == null) {
            return;
        }
        syncFollowerCountInternal(node, board);
        updateBoardPopularity(board);
    }

    private void updateBoardPopularity(@Nonnull BoardIndexEntity board) {
        board.setPopularity((long) (10000 * (Math.log10(board.getCommentCount() + 1) + Math.log10(board.getActivityCount() + 1) + Math.log10(board.getFollowerCount() + 1) + Math.log10(board.getLikeCount() + 1))));
    }

    public void addMetrics(@Nonnull Node pool, @Nonnull LSDEntity entity) {
        final BoardIndexEntity board = boardDAO.getOrCreateBoard(pool.getURI().asString());
        entity.setAttribute(LSDAttribute.VISITS_METRIC, String.valueOf(board.getVisitCount()));
        entity.setAttribute(LSDAttribute.REGISTERED_VISITORS_METRIC, boardDAO.getUniqueVisitorCount(board));
    }
}
