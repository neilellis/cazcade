package cazcade.liquid.impl;

import cazcade.liquid.api.LiquidUUID;

/**
 * @author neilellis@cazcade.com
 */
public class UUIDFactory {
    public static LiquidUUID randomUUID() {
        return new LiquidUUID(java.util.UUID.randomUUID().toString());
    }
}
