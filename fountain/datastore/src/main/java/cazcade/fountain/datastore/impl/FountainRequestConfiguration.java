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

    public FountainRequestCompensator<T> getCompensator() {
        return compensator;
    }

    public void setCompensator(final FountainRequestCompensator<T> compensator) {
        this.compensator = compensator;
    }

    public LiquidMessageHandler<T> getHandler() {
        return handler;
    }

    public void setHandler(final LiquidMessageHandler<T> handler) {
        this.handler = handler;
    }
}
