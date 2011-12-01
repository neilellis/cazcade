package cazcade.liquid.impl;

import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * @author neilellis@cazcade.com
 */
public class UUIDFactory {
    @Nonnull
    public static LiquidUUID randomUUID() {
        return new LiquidUUID(UUID.randomUUID().toString());
    }
}
