package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDTransferEntity;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainBoardQueryDAO {


    @Nonnull
    LSDTransferEntity getMyBoards(int start, int end, LiquidSessionIdentifier session) throws InterruptedException;

    @Nonnull
    LSDTransferEntity getUserPublicBoards(int start, int end, LiquidSessionIdentifier session, LiquidURI alias) throws InterruptedException;

    @Nonnull
    LSDTransferEntity getRecentPublicBoards(int start, int end, LiquidSessionIdentifier session) throws InterruptedException;

    @Nonnull
    LSDTransferEntity getMyVisitedBoards(int start, int end, LiquidSessionIdentifier session) throws InterruptedException;

    @Nonnull
    LSDTransferEntity getPopularBoards(int max, int end, LiquidSessionIdentifier session) throws InterruptedException;

}
