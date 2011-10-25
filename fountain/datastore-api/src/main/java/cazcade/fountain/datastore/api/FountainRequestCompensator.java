package cazcade.fountain.datastore.api;

import cazcade.liquid.api.LiquidRequest;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainRequestCompensator<T extends LiquidRequest> {

    LiquidRequest compensate(T request);
}
