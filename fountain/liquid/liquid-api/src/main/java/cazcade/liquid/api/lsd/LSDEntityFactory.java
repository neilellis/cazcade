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

public interface LSDEntityFactory {
    /**
     * @param properties
     * @param dotPrefixed true if it should only be built from properties starting with "."
     * @return
     */
    @Nonnull LSDBaseEntity create(Map<String, String> properties, boolean dotPrefixed);

    @Nonnull LSDBaseEntity create(LiquidUUID uuid);

    @Nonnull LSDTransferEntity createFromServletProperties(Map<String, String[]> properties);
}
