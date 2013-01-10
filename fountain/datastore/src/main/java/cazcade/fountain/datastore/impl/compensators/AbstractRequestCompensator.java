/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.compensators;

import cazcade.fountain.datastore.api.FountainRequestCompensator;
import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.liquid.api.LiquidRequest;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractRequestCompensator<T extends LiquidRequest> implements FountainRequestCompensator<T> {
    private FountainNeo fountainNeo;

    public void setFountainNeo(final FountainNeo fountainNeo) {
        this.fountainNeo = fountainNeo;
    }
}
