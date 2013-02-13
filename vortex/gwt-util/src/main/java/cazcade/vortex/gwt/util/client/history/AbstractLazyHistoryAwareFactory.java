/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client.history;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

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
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(final Throwable reason) {
                //TODO
            }

            @Override
            public void onSuccess() {
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
