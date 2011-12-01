package cazcade.liquid.impl;

import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class UUIDFactory {
    @Nonnull
    public static LiquidUUID randomUUID() {
        return new LiquidUUID(java.util.UUID.randomUUID().toString());
    }
}
