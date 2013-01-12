/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.common.app;


import cazcade.common.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class ApplicationLifecycleManager implements ApplicationLifecycle {
    @Nonnull
    private static final Logger log = Logger.getLogger(ApplicationLifecycleManager.class);
    public boolean shutdown;

    @Nonnull
    private final List<ApplicationLifecycleListener> listeners = new ArrayList<ApplicationLifecycleListener>();

    public void register(final ApplicationLifecycleListener listener) {
        listeners.add(listener);
    }

    public void shutdown() {
        if (shutdown) {
            log.warn("Application is already shutdown.");
            return;
        }
        shutdown = true;
        for (final ApplicationLifecycleListener listener : listeners) {
            try {
                listener.shutdown();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    public boolean isShutdown() {
        return shutdown;
    }
}
