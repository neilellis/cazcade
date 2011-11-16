package cazcade.fountain.datastore.impl;

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
import cazcade.liquid.api.lsd.LSDTypeImpl;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

    private boolean isBoard(Node node) {
        return node.getProperty(FountainNeo.TYPE).toString().startsWith(LSDDictionaryTypes.BOARD.getValue());
    }

    private void addCoreMetadataToBoard(Node node, BoardIndexEntity board) {
        board.setDescription((String) node.getProperty(FountainNeo.DESCRIPTION, null));
        board.setText((String) node.getProperty(FountainNeo.TEXT_EXTENDED, null));
        board.setTitle((String) node.getProperty(FountainNeo.TITLE, null));
    }


    @Transactional
    public void syncBoard(Node node) {
        if (!isBoard(node)) {
            return;
        }

        String uri = (String) node.getProperty(FountainNeo.URI);
        if (LiquidBoardURL.isConvertable(uri)) {
            BoardIndexEntity board = boardDAO.getOrCreateBoard(uri);
            LiquidBoardURL shortUrl = new LiquidBoardURL(uri);
            board.setShortUrl(shortUrl.toString());
            boolean listed = node.getProperty(LSDAttribute.LISTED.getKeyName(), "false").equals("true");
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
            board.setUpdated(new Date(Long.parseLong(node.getProperty(FountainNeo.UPDATED, "0").toString())));
            boardDAO.saveBoard(board);
        }
    }

    private void addOwnershipToBoard(Node node, BoardIndexEntity board) {
        final Relationship ownerRel = node.getSingleRelationship(FountainRelationships.OWNER, Direction.OUTGOING);
        if (ownerRel != null) {
            final String owner = ownerRel.getOtherNode(node).getProperty(FountainNeo.URI, "unknown").toString();
            log.debug("Setting owner as {0} on {1}", owner, board.getUri());
            board.setOwner(aliasDAO.getOrCreateUser(owner));
        }
        final Relationship authorRel = node.getSingleRelationship(FountainRelationships.AUTHOR, Direction.OUTGOING);
        if (authorRel != null) {
            final String author = authorRel.getOtherNode(node).getProperty(FountainNeo.URI, "unknown").toString();
            log.debug("Setting author as {0} on {1}", author, board.getUri());
            board.setAuthor(aliasDAO.getOrCreateUser(author));
        }
        final Relationship creatorRel = node.getSingleRelationship(FountainRelationships.CREATOR, Direction.OUTGOING);
        if (creatorRel != null) {
            final String creator = creatorRel.getOtherNode(node).getProperty(FountainNeo.URI, "unknown").toString();
            log.debug("Setting creator as {0} on {1}", creator, board.getUri());
            board.setCreator(aliasDAO.getOrCreateUser(creator));
        }
    }

    private void syncFollowsCountsInternal(Node node, AliasEntity board) {

        //todo: this is for aliases

    }

    private void syncFollowerCountInternal(Node node, BoardIndexEntity board) {
        long aliasFollowsCount = Long.valueOf(node.getProperty(LSDAttribute.FOLLOWERS_COUNT.getKeyName(), "0").toString());
        board.setFollowerCount(aliasFollowsCount);
    }

    private void syncCommentCountInternal(Node node, BoardIndexEntity board) {
        long commentCount = Long.valueOf(node.getProperty(LSDAttribute.COMMENT_COUNT.getKeyName(), "0").toString());
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
    public void incrementBoardActivity(Node pool) {
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

    private BoardIndexEntity getBoardForNode(Node node) {

        String uri = (String) node.getProperty(FountainNeo.URI);
        LSDTypeImpl type = new LSDTypeImpl(node.getProperty(LSDAttribute.TYPE.getKeyName()).toString());
        if (type.canBe(LSDDictionaryTypes.BOARD)) {
            BoardIndexEntity board = boardDAO.getOrCreateBoard(uri);
            return board;
        } else {
            return null;
        }
    }

    @Transactional
    public void syncCommentCount(Node node) {
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
    public void visitBoard(Node node, LiquidURI visitor) {
        if (!isBoard(node)) {
            return;
        }

        BoardIndexEntity board = getBoardForNode(node);
        if (board == null) {
            return;
        }
        VisitEntity visitEntity = new VisitEntity();
        visitEntity.setBoard(board);
        visitEntity.setVisitor(aliasDAO.getOrCreateUser(visitor.toString()));
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
    public void syncFollowerCount(Node node) {
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

    private void updateBoardPopularity(BoardIndexEntity board) {
        board.setPopularity((long) (10000 * (Math.log10(board.getCommentCount() + 1) + Math.log10(board.getActivityCount() + 1) + Math.log10(board.getFollowerCount() + 1) + Math.log10(board.getLikeCount() + 1))));
    }

    public void addMetrics(Node pool, LSDEntity entity) {
        final BoardIndexEntity board = boardDAO.getOrCreateBoard(pool.getProperty(FountainNeo.URI).toString());
        entity.setAttribute(LSDAttribute.VISITS_METRIC, String.valueOf(board.getVisitCount()));
        entity.setAttribute(LSDAttribute.UNIQUE_VISITORS_METRIC, boardDAO.getUniqueVisitorCount(board));
    }
}
