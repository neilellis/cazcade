/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client.history;

import cazcade.vortex.gwt.util.client.$;

import javax.annotation.Nonnull;

/**
 * This is used to help with code splitting.
 *
 * @author neilellis@cazcade.com
 */
public abstract class AbstractLazyHistoryAwareFactory implements HistoryLocation {
    private HistoryManager historyManager;
    private String         token;
    private HistoryAware   instance;


    @Override
    public void handle(@Nonnull final HistoryLocationCallback callback) {
        $.async(new Runnable() {
            @Override public void run() {
                if (instance == null) {
                    instance = getInstanceInternal();
                    instance.setHistoryManager(historyManager);
                    instance.setHistoryToken(token);
                }
                callback.withInstance(instance);
            }
        });
    }

    @Nonnull
    protected abstract HistoryAware getInstanceInternal();

    @Override
    public void setHistoryManager(final HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void setHistoryToken(final String token) {
        this.token = token;
    }

    @Override public void beforeRemove() {
        instance.beforeInactive();
    }
}
