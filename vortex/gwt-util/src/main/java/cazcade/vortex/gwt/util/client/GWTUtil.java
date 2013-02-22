/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Timer;

/**
 * @author neilellis@cazcade.com
 */
public class GWTUtil {

    public static void runAsync(final Runnable runnable) {
        GWT.runAsync(new RunAsyncCallback() {
            @Override public void onFailure(Throwable reason) {
                ClientLog.log(reason);
            }

            @Override public void onSuccess() {
                try {
                    runnable.run();
                } catch (Exception e) {
                    ClientLog.log(e);
                }
            }
        });


    }

    public static void runAsyncWithDelay(int delay, final Runnable runnable) {
        new Timer() {
            @Override public void run() {
                runAsync(runnable);
            }
        }.schedule(delay);

    }
}
