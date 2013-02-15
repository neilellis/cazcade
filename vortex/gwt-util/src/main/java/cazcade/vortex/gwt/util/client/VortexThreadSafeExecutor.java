/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client;


import com.google.gwt.user.client.Timer;

import javax.annotation.Nonnull;
import java.util.Stack;

/**
 * @author neilellis@cazcade.com
 */
public class VortexThreadSafeExecutor {

    public static final int             SCHEDULER_DELAY_MIILLIS = 50;
    @Nonnull
    private final       Stack<Runnable> stack                   = new Stack<Runnable>();

    public VortexThreadSafeExecutor() {
        new Timer() {
            @Override
            public void run() {
                while (!stack.isEmpty()) {
                    final Runnable runnable = stack.remove(0);
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        ClientLog.log(e);
                    }
                }
                schedule(SCHEDULER_DELAY_MIILLIS);
            }
        }.schedule(1);
    }

    public void execute(final Runnable run) {
        //        run.run();
        stack.push(run);
    }


}
