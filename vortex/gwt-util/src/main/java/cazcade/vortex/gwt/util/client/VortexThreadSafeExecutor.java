package cazcade.vortex.gwt.util.client;


import com.google.gwt.user.client.Timer;

import java.util.Stack;

/**
 * @author neilellis@cazcade.com
 */
public class VortexThreadSafeExecutor {

    public static final int SCHEDULER_DELAY_MIILLIS = 50;
    private Stack<Runnable> stack = new Stack<Runnable>();

    public VortexThreadSafeExecutor() {
        new Timer() {
            @Override
            public void run() {
                while (!stack.isEmpty()) {
                    final Runnable runnable = stack.pop();
                    try {
                        runnable.run();
                    } catch (Exception e) {
                        ClientLog.log(e);
                    }
                }
                this.schedule(SCHEDULER_DELAY_MIILLIS);
            }
        }.schedule(1);
    }

    public void execute(Runnable run) {
        stack.push(run);
    }
}
