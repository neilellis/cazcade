/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import cazcade.fountain.datastore.impl.compensators.AbstractRequestCompensator;
import cazcade.fountain.datastore.impl.handlers.AbstractDataStoreHandler;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class FountainRequestMap {
    private Map<String, FountainRequestConfiguration> map;

    public FountainRequestConfiguration getConfiguration(@Nonnull final Class clazz) {
        return map.get(clazz.getName());
    }

    public void injectNeo(final FountainNeo fountainNeo) {
        for (final FountainRequestConfiguration configuration : map.values()) {
            ((AbstractDataStoreHandler) configuration.getHandler()).setNeo(fountainNeo);
            final AbstractRequestCompensator compensator = (AbstractRequestCompensator) configuration.getCompensator();
            if (compensator != null) {
                compensator.setFountainNeo(fountainNeo);
            }
        }
    }

    public Map getMap() {
        return map;
    }

    public void setMap(final Map map) {
        this.map = map;
    }
}
