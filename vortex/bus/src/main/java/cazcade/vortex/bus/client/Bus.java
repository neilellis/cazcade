/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.bus.client;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class Bus {

    @Nonnull
    private static final BusService instance = new BusServiceImpl();

    @Nonnull
    public static BusService get() {
        return instance;
    }
}
