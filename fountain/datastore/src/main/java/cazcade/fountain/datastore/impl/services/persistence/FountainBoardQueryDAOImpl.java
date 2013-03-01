/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.common.Logger;
import cazcade.fountain.datastore.impl.FountainBoardQueryDAO;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainPoolDAO;
import cazcade.fountain.index.persistence.dao.BoardDAO;
import cazcade.fountain.index.persistence.entities.BoardIndexEntity;
import cazcade.liquid.api.LURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.RequestDetailLevel;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author neilellis@cazcade.com
 */
public class FountainBoardQueryDAOImpl implements FountainBoardQueryDAO {
    @Nonnull
    private static final Logger log = Logger.getLogger(FountainBoardQueryDAO.class);
    @Autowired
    private FountainNeo     fountainNeo;
    @Autowired
    private FountainPoolDAO poolDAO;
    @Autowired
    private BoardDAO        boardDAO;

    @Nonnull @Override
    public TransferEntity getMyBoards(final int start, final int end, @Nonnull final SessionIdentifier session) throws Exception {
        final List<BoardIndexEntity> boards = boardDAO.getMyBoards(start, end, session.aliasURI().toString());
        return convertToEntityResult(session, boards);
    }

    @Nonnull @Override
    public TransferEntity getMyVisitedBoards(final int start, final int end, @Nonnull final SessionIdentifier session) throws Exception {
        final List<BoardIndexEntity> boards = boardDAO.getVisitedBoards(start, end, session.aliasURI().toString());
        return convertToEntityResult(session, boards);
    }

    @Nonnull @Override
    public TransferEntity getPopularBoards(final int start, final int end, final SessionIdentifier session) throws Exception {
        final List<BoardIndexEntity> boards = boardDAO.getPopularBoards(start, end);
        return convertToEntityResult(session, boards);
    }

    @Nonnull @Override
    public TransferEntity getRecentPublicBoards(final int start, final int end, final SessionIdentifier session) throws Exception {
        final List<BoardIndexEntity> boards = boardDAO.getRecentBoards(start, end);
        return convertToEntityResult(session, boards);
    }

    @Nonnull @Override
    public TransferEntity getUserPublicBoards(final int start, final int end, final SessionIdentifier session, @Nonnull final LURI alias) throws Exception {
        final List<BoardIndexEntity> boards = boardDAO.getUserBoards(start, end, alias.toString());
        return convertToEntityResult(session, boards);
    }

    @Nonnull
    private TransferEntity convertToEntityResult(final SessionIdentifier session, @Nonnull final List<BoardIndexEntity> boards) throws Exception {
        final List<Entity> subEntities = new ArrayList<Entity>();
        final TransferEntity result = SimpleEntity.createNewTransferEntity(Types.T_BOARD_LIST, LiquidUUID.fromString(UUID.randomUUID()
                                                                                                                         .toString()));
        for (final BoardIndexEntity board : boards) {
            final Entity poolObjectTx = poolDAO.getPoolObjectTx(session, new LURI(board.getUri()), false, false, RequestDetailLevel.BOARD_LIST);
            if (poolObjectTx == null) {
                log.warn("Board " + board.getUri() + " was null from getPoolObjectTx, skipping.");
            } else {
                subEntities.add(poolObjectTx);
            }
        }
        result.children(Dictionary.CHILD_A, subEntities);
        return result;
    }
}
