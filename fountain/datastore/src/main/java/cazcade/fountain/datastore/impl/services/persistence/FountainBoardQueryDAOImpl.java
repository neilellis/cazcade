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
import cazcade.liquid.api.lsd.*;
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
    public LSDTransferEntity getMyBoards(final int start, final int end, @Nonnull final LiquidSessionIdentifier session)
            throws InterruptedException {
        final List<BoardIndexEntity> boards = boardDAO.getMyBoards(start, end, session.getAliasURL().toString());
        return convertToEntityResult(session, boards);
    }

    @Nonnull
    private LSDTransferEntity convertToEntityResult(final LiquidSessionIdentifier session,
                                                    @Nonnull final List<BoardIndexEntity> boards) throws InterruptedException {
        final List<LSDBaseEntity> subEntities = new ArrayList<LSDBaseEntity>();
        final LSDTransferEntity result = LSDSimpleEntity.createNewEntity(LSDDictionaryTypes.BOARD_LIST);
        for (final BoardIndexEntity board : boards) {
            final LSDBaseEntity poolObjectTx = poolDAO.getPoolObjectTx(session, new LiquidURI(board.getUri()), false, false,
                                                                       LiquidRequestDetailLevel.BOARD_LIST
                                                                      );
            if (poolObjectTx == null) {
                log.warn("Board " + board.getUri() + " was null from getPoolObjectTx, skipping.");
            }
            else {
                subEntities.add(poolObjectTx);
            }
        }
        result.addSubEntities(LSDAttribute.CHILD, subEntities);
        return result;
    }

    @Nonnull
    @Override
    public LSDTransferEntity getMyVisitedBoards(final int start, final int end, @Nonnull final LiquidSessionIdentifier session)
            throws InterruptedException {
        final List<BoardIndexEntity> boards = boardDAO.getVisitedBoards(start, end, session.getAliasURL().toString());
        return convertToEntityResult(session, boards);
    }

    @Nonnull
    @Override
    public LSDTransferEntity getPopularBoards(final int start, final int end, final LiquidSessionIdentifier session)
            throws InterruptedException {
        final List<BoardIndexEntity> boards = boardDAO.getPopularBoards(start, end);
        return convertToEntityResult(session, boards);
    }

    @Nonnull
    @Override
    public LSDTransferEntity getRecentPublicBoards(final int start, final int end, final LiquidSessionIdentifier session)
            throws InterruptedException {
        final List<BoardIndexEntity> boards = boardDAO.getRecentBoards(start, end);
        return convertToEntityResult(session, boards);
    }

    @Nonnull
    @Override
    public LSDTransferEntity getUserPublicBoards(final int start, final int end, final LiquidSessionIdentifier session,
                                                 @Nonnull final LiquidURI alias) throws InterruptedException {
        final List<BoardIndexEntity> boards = boardDAO.getUserBoards(start, end, alias.toString());
        return convertToEntityResult(session, boards);
    }
}
