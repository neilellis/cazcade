package cazcade.fountain.datastore.impl;

import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.datastore.api.FountainRequestCompensator;
import cazcade.liquid.api.LiquidRequest;

/**
 * @author neilellis@cazcade.com
 */
public class FountainRequestCompensatorImpl implements FountainRequestCompensator<LiquidRequest> {

    private FountainDataStore store;

    private FountainRequestMap requestMap;

    public LiquidRequest compensate(LiquidRequest request) {
        FountainRequestCompensator compensator = requestMap.getConfiguration(request.getClass()).getCompensator();
        if (compensator == null) {
            return null;
        } else {
            return compensator.compensate(request);
        }
    }

    public void setRequestMap(FountainRequestMap requestMap) {
        this.requestMap = requestMap;
    }

    public FountainRequestMap getRequestMap() {
        return requestMap;
    }
}
