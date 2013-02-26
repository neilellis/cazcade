/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author Neil Ellis
 */

public interface EntityFactory<T extends TransferEntity<T>> {
    /**
     * @param properties
     * @param dotPrefixed true if it should only be built from properties starting with "."
     * @return
     */
    @Nonnull T create(Map<String, String> properties, boolean dotPrefixed);

    @Nonnull T create(LiquidUUID uuid);

    @Nonnull T createFromServletProperties(Map<String, String[]> properties);
}
