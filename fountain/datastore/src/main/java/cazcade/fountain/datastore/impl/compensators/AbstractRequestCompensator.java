package cazcade.fountain.datastore.impl.compensators;

import cazcade.fountain.datastore.api.FountainRequestCompensator;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.liquid.api.LiquidRequest;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractRequestCompensator<T extends LiquidRequest> implements FountainRequestCompensator<T> {
    private FountainNeo fountainNeo;

    public void setFountainNeo(FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }
}
