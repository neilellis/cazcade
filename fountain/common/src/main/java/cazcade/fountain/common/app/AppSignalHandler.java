package cazcade.fountain.common.app;

import cazcade.common.Logger;
import cazcade.fountain.common.error.ErrorHandler;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class AppSignalHandler implements SignalHandler {
    private int shutdownCount = 0;
    private boolean running = true;
    @Nonnull
    private final static Logger log = Logger.getLogger(AppSignalHandler.class);
    private final Runnable onFirst;
    private final Runnable onSecond;
    private final Runnable onThird;

    public AppSignalHandler(Runnable onFirst, Runnable onSecond, Runnable onThird) {
        this.onFirst = onFirst;
        this.onSecond = onSecond;
        this.onThird = onThird;
    }

    public void handle(Signal sig) {
        shutdownCount++;
        if (running) {
            running = false;
            log.info("Signal " + sig);
            try {
                onFirst.run();
                System.exit(0);
            } catch (Exception e) {
                ErrorHandler.handle(e);
            }
        } else {
            if (shutdownCount == 2) {
                onSecond.run();
                return;
            }
            if (shutdownCount > 2) {
                // only on the second attempt do we exit
                onThird.run();
                System.exit(-3);
            }
        }
    }
}
