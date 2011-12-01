package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDEntity;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainBoardQueryDAO {


    @Nonnull
    LSDEntity getMyBoards(int start, int end, LiquidSessionIdentifier session) throws InterruptedException;

    @Nonnull
    LSDEntity getUserPublicBoards(int start, int end, LiquidSessionIdentifier session, LiquidURI alias) throws InterruptedException;

    @Nonnull
    LSDEntity getRecentPublicBoards(int start, int end, LiquidSessionIdentifier session) throws InterruptedException;

    @Nonnull
    LSDEntity getMyVisitedBoards(int start, int end, LiquidSessionIdentifier session) throws InterruptedException;

    @Nonnull
    LSDEntity getPopularBoards(int max, int end, LiquidSessionIdentifier session) throws InterruptedException;

}
