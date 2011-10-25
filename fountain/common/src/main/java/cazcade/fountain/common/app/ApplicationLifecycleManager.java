package cazcade.fountain.common.app;


import cazcade.common.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class ApplicationLifecycleManager implements ApplicationLifecycle {
    private final static Logger log = Logger.getLogger(ApplicationLifecycleManager.class);
    public boolean shutdown = false;

    private List<ApplicationLifecycleListener> listeners = new ArrayList<ApplicationLifecycleListener>();

    public void register(ApplicationLifecycleListener listener) {
        listeners.add(listener);
    }

    public void shutdown() {
        if (shutdown) {
            log.warn("Application is already shutdown.");
            return;
        }
        shutdown = true;
        for (ApplicationLifecycleListener listener : listeners) {
            try {
                listener.shutdown();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public boolean isShutdown() {
        return shutdown;
    }
}
