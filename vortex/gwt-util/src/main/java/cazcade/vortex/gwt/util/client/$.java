/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client;

import cazcade.liquid.api.lsd.Dictionary;
import cazcade.vortex.gwt.util.client.history.HistoryManager;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Timer;

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

    public static void defer(final Runnable runnable) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override public void execute() {
                runnable.run();
            }
        });
    }


    public static void delay(int delayInMilli, final Runnable runnable) {
        new Timer() {
            @Override public void run() {
                runnable.run();
            }
        }.schedule(delayInMilli);
    }
}
