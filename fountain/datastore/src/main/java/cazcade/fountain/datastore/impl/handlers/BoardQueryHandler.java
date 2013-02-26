/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.handlers;

import cazcade.common.Logger;
import cazcade.fountain.datastore.api.DataStoreException;
import cazcade.fountain.datastore.impl.FountainBoardQueryDAO;
import cazcade.fountain.datastore.impl.LiquidResponseHelper;
import cazcade.liquid.api.handler.BoardQueryRequestHandler;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.BoardQueryRequest;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;


/**
 * @author neilelliz@cazcade.com
 */
public class BoardQueryHandler extends AbstractDataStoreHandler<BoardQueryRequest> implements BoardQueryRequestHandler {
    @Nonnull
    private static final Logger log = Logger.getLogger(BoardQueryHandler.class);

    @Autowired FountainBoardQueryDAO queryDAO;

    @Nonnull
    public BoardQueryRequest handle(@Nonnull final BoardQueryRequest request) throws Exception {
        final Transaction transaction = neo.beginTx();
        try {
            log.debug("Processing Pool Query of type {0}", request.getQueryType());

            final TransferEntity searchResultEntity;
            if (request.getQueryType() == BoardQueryRequest.QueryType.POPULAR) {
                searchResultEntity = queryDAO.getPopularBoards(request.getStart(), request.getMax(), request.session());
            } else if (request.getQueryType() == BoardQueryRequest.QueryType.HISTORY) {
                searchResultEntity = queryDAO.getMyVisitedBoards(request.getStart(), request.getMax(), request.session());
            } else if (request.getQueryType() == BoardQueryRequest.QueryType.MY) {
                searchResultEntity = queryDAO.getMyBoards(request.getStart(), request.getMax(), request.session());
            } else if (request.getQueryType() == BoardQueryRequest.QueryType.RECENT) {
                searchResultEntity = queryDAO.getRecentPublicBoards(request.getStart(), request.getMax(), request.session());
            } else if (request.getQueryType() == BoardQueryRequest.QueryType.PROFILE) {
                searchResultEntity = queryDAO.getUserPublicBoards(request.getStart(), request.getMax(), request.session(), request.alias());
            } else {
                throw new DataStoreException("Unsupported query type " + request.type());
            }
            return LiquidResponseHelper.forServerSuccess(request, searchResultEntity);
        } catch (Exception e) {
            transaction.failure();
            throw e;
        } finally {
            transaction.finish();
        }
    }
}