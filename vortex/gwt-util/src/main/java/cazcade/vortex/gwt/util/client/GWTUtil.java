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

    public static class AsyncTimer extends Timer {
        private final Runnable runnable;

        public AsyncTimer(Runnable runnable) {this.runnable = runnable;}

        @Override public void run() {
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
    }
}
