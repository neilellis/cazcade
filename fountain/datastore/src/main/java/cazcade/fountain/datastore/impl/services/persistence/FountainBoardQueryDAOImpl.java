package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainBoardQueryDAO;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainPoolDAO;
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
    private static final Logger log = Logger.getLogger(FountainBoardQueryDAO.class);

    @Autowired
    private FountainNeo fountainNeo;

    @Autowired
    private FountainPoolDAO poolDAO;


    @Autowired
    private BoardDAO boardDAO;


    @Nonnull
    @Override
    public LSDEntity getMyBoards(final int start, final int end, @Nonnull final LiquidSessionIdentifier session) throws InterruptedException {
        final List<BoardIndexEntity> boards = boardDAO.getMyBoards(start, end, session.getAliasURL().toString());
        return convertToEntityResult(session, boards);

    }

    @Nonnull
    private LSDSimpleEntity convertToEntityResult(final LiquidSessionIdentifier session, @Nonnull final List<BoardIndexEntity> boards) throws InterruptedException {
        final List<LSDEntity> subEntities = new ArrayList<LSDEntity>();
        final LSDSimpleEntity result = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.BOARD_LIST);
        for (final BoardIndexEntity board : boards) {
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
    public LSDEntity getUserPublicBoards(final int start, final int end, final LiquidSessionIdentifier session, @Nonnull final LiquidURI alias) throws InterruptedException {
        final List<BoardIndexEntity> boards = boardDAO.getUserBoards(start, end, alias.toString());
        return convertToEntityResult(session, boards);
    }

    @Nonnull
    @Override
    public LSDEntity getRecentPublicBoards(final int start, final int end, final LiquidSessionIdentifier session) throws InterruptedException {
        final List<BoardIndexEntity> boards = boardDAO.getRecentBoards(start, end);
        return convertToEntityResult(session, boards);
    }

    @Nonnull
    @Override
    public LSDEntity getMyVisitedBoards(final int start, final int end, @Nonnull final LiquidSessionIdentifier session) throws InterruptedException {
        final List<BoardIndexEntity> boards = boardDAO.getVisitedBoards(start, end, session.getAliasURL().toString());
        return convertToEntityResult(session, boards);
    }

    @Nonnull
    @Override
    public LSDEntity getPopularBoards(final int start, final int end, final LiquidSessionIdentifier session) throws InterruptedException {
        final List<BoardIndexEntity> boards = boardDAO.getPopularBoards(start, end);
        return convertToEntityResult(session, boards);
    }
}
