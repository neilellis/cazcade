package cazcade.fountain.datastore.impl;

import cazcade.fountain.datastore.impl.compensators.AbstractRequestCompensator;
import cazcade.fountain.datastore.impl.handlers.AbstractDataStoreHandler;

import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class FountainRequestMap {
    private Map<String, FountainRequestConfiguration> map;

    public void setMap(Map map) {
        this.map = map;
    }

    public Map getMap() {
        return map;
    }

    public void injectNeo(FountainNeo fountainNeo) {
        for (FountainRequestConfiguration configuration : map.values()) {
            ((AbstractDataStoreHandler) configuration.getHandler()).setFountainNeo(fountainNeo);
            AbstractRequestCompensator compensator = (AbstractRequestCompensator) configuration.getCompensator();
            if (compensator != null) {
                compensator.setFountainNeo(fountainNeo);
            }
        }
    }

    public FountainRequestConfiguration getConfiguration(Class clazz) {
        return map.get(clazz.getName());
    }
}
