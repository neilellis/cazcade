/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidMessage;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public interface Callback<T extends LiquidMessage> {
    void handle(T message) throws Exception;
}
