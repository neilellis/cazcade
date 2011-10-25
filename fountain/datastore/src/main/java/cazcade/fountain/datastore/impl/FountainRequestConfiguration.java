package cazcade.fountain.datastore.impl;

import cazcade.fountain.datastore.api.FountainRequestCompensator;
import cazcade.liquid.api.LiquidMessageHandler;
import cazcade.liquid.api.LiquidRequest;

/**
 * @author neilellis@cazcade.com
 */
public class FountainRequestConfiguration<T extends LiquidRequest> {

    private LiquidMessageHandler<T> handler;
    private FountainRequestCompensator<T> compensator;


    public void setHandler(LiquidMessageHandler<T> handler) {
        this.handler = handler;
    }

    public void setCompensator(FountainRequestCompensator<T> compensator) {
        this.compensator = compensator;
    }

    public FountainRequestCompensator<T> getCompensator() {
        return compensator;
    }

    public LiquidMessageHandler<T> getHandler() {
        return handler;
    }
}
