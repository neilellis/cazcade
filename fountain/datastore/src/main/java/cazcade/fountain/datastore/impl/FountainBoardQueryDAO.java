package cazcade.fountain.datastore.impl;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.lsd.LSDSimpleEntity;
import org.neo4j.graphdb.Node;

import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainBoardQueryDAO {


    LSDEntity getMyBoards(int start, int end, LiquidSessionIdentifier session) throws InterruptedException;

    LSDEntity getUserPublicBoards(int start, int end, LiquidSessionIdentifier session, LiquidURI alias) throws InterruptedException;

    LSDEntity getRecentPublicBoards(int start, int end, LiquidSessionIdentifier session) throws InterruptedException;

    LSDEntity getMyVisitedBoards(int start, int end, LiquidSessionIdentifier session) throws InterruptedException;

    LSDEntity getPopularBoards(int max, int end, LiquidSessionIdentifier session) throws InterruptedException;

}
