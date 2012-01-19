package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.fountain.datastore.api.FountainDataStore;
import cazcade.fountain.datastore.api.FountainRequestCompensator;
import cazcade.fountain.datastore.impl.FountainRequestMap;
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
    public LiquidRequest compensate(@Nonnull final LiquidRequest request) {
        final FountainRequestCompensator compensator = requestMap.getConfiguration(request.getClass()).getCompensator();
        if (compensator == null) {
            return null;
        }
        else {
            return compensator.compensate(request);
        }
    }

    public FountainRequestMap getRequestMap() {
        return requestMap;
    }

    public void setRequestMap(final FountainRequestMap requestMap) {
        this.requestMap = requestMap;
    }
}
