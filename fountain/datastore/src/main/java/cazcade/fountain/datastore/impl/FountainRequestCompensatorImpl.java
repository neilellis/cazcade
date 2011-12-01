package cazcade.fountain.datastore.impl;

import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.datastore.api.FountainRequestCompensator;
import cazcade.liquid.api.LiquidRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
public class FountainRequestCompensatorImpl implements FountainRequestCompensator<LiquidRequest> {

    private FountainDataStore store;

    private FountainRequestMap requestMap;

    @Nullable
    public LiquidRequest compensate(@Nonnull LiquidRequest request) {
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
