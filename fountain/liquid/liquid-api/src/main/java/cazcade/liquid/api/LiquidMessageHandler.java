/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface LiquidMessageHandler<T extends LiquidMessage> {
    @Nonnull T handle(T message) throws Exception;
}
