/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.bus.client;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class BusFactory {

    @Nonnull
    private static final Bus instance = new BusImpl();

    @Nonnull
    public static Bus get() {
        return instance;
    }
}
