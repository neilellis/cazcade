/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.vortex.gwt.util.client.history.HistoryManager;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class $ implements Dictionary {

    public static void async(Runnable runnable) {
        GWTUtil.async(runnable);
    }

    public static void delayAsync(int delay, final Runnable runnable) {
        GWTUtil.delayAsync(delay, runnable);
    }

    public static void navigate(String location) {
        HistoryManager.get().navigate(location);
    }

}
