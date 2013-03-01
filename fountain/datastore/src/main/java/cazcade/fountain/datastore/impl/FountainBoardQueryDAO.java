/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.TransferEntity;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainBoardQueryDAO {
    @Nonnull TransferEntity getMyBoards(int start, int end, SessionIdentifier session) throws Exception;

    @Nonnull TransferEntity getMyVisitedBoards(int start, int end, SessionIdentifier session) throws Exception;

    @Nonnull TransferEntity getPopularBoards(int max, int end, SessionIdentifier session) throws Exception;

    @Nonnull TransferEntity getRecentPublicBoards(int start, int end, SessionIdentifier session) throws Exception;

    @Nonnull TransferEntity getUserPublicBoards(int start, int end, SessionIdentifier session, LURI alias) throws Exception;
}
