package cazcade.fountain.datastore.impl;

import cazcade.common.Logger;
import cazcade.fountain.index.persistence.dao.BoardDAO;
import cazcade.fountain.index.persistence.entities.BoardIndexEntity;
import cazcade.liquid.api.LiquidRequestDetailLevel;
import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDDictionaryTypes;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class FountainBoardQueryDAOImpl implements FountainBoardQueryDAO {
    @Nonnull
    private final static Logger log = Logger.getLogger(FountainBoardQueryDAO.class);

    @Autowired
    private FountainNeo fountainNeo;

    @Autowired
    private FountainPoolDAO poolDAO;


    @Autowired
    private BoardDAO boardDAO;


    @Nonnull
    @Override
    public LSDEntity getMyBoards(int start, int end, @Nonnull LiquidSessionIdentifier session) throws InterruptedException {
        List<BoardIndexEntity> boards = boardDAO.getMyBoards(start, end, session.getAliasURL().toString());
        return convertToEntityResult(session, boards);

    }

    @Nonnull
    private LSDSimpleEntity convertToEntityResult(LiquidSessionIdentifier session, @Nonnull List<BoardIndexEntity> boards) throws InterruptedException {
        List<LSDEntity> subEntities = new ArrayList<LSDEntity>();
        LSDSimpleEntity result = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.BOARD_LIST);
        for (BoardIndexEntity board : boards) {
            final LSDEntity poolObjectTx = poolDAO.getPoolObjectTx(session, new LiquidURI(board.getUri()), false, false, LiquidRequestDetailLevel.BOARD_LIST);
            if (poolObjectTx == null) {
                log.warn("Board " + board.getUri() + " was null from getPoolObjectTx, skipping.");
            } else {
                subEntities.add(poolObjectTx);
            }
        }
        result.addSubEntities(LSDAttribute.CHILD, subEntities);
        return result;
    }

    @Nonnull
    @Override
    public LSDEntity getUserPublicBoards(int start, int end, LiquidSessionIdentifier session, @Nonnull LiquidURI alias) throws InterruptedException {
        List<BoardIndexEntity> boards = boardDAO.getUserBoards(start, end, alias.toString());
        return convertToEntityResult(session, boards);
    }

    @Nonnull
    @Override
    public LSDEntity getRecentPublicBoards(int start, int end, LiquidSessionIdentifier session) throws InterruptedException {
        List<BoardIndexEntity> boards = boardDAO.getRecentBoards(start, end);
        return convertToEntityResult(session, boards);
    }

    @Nonnull
    @Override
    public LSDEntity getMyVisitedBoards(int start, int end, @Nonnull LiquidSessionIdentifier session) throws InterruptedException {
        List<BoardIndexEntity> boards = boardDAO.getVisitedBoards(start, end, session.getAliasURL().toString());
        return convertToEntityResult(session, boards);
    }

    @Nonnull
    @Override
    public LSDEntity getPopularBoards(int start, int end, LiquidSessionIdentifier session) throws InterruptedException {
        List<BoardIndexEntity> boards = boardDAO.getPopularBoards(start, end);
        return convertToEntityResult(session, boards);
    }
}
