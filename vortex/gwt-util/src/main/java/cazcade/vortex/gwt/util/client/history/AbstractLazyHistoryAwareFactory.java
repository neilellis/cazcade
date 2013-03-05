/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client.history;

import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.AsyncProxy;
import com.google.gwt.user.client.Timer;

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

            @Override public void onFailure(Throwable reason) {
                ClientLog.log(reason);
            }

            @Override public void onSuccess() {
                doSuccess(callback);
            }
        });


    }

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

    public void doSuccess(final HistoryLocationCallback callback) {
        try {
            if (instance == null) {
                instance = getInstanceInternal();

                if (instance instanceof AsyncProxy) {
                    ((AsyncProxy<HistoryAware>) instance).setProxyCallback(new AsyncProxy.ProxyCallback<HistoryAware>() {
                        public void onComplete(HistoryAware real) {
                            instance = real;
                            callback.withInstance(instance);
                        }
                    });
                    instance.setHistoryManager(historyManager);
                    instance.setHistoryToken(token);
                } else {
                    instance.setHistoryManager(historyManager);
                    instance.setHistoryToken(token);
                    callback.withInstance(instance);
                }
            }

            if (instance instanceof AsyncProxy) {
                //Don't do anything until we have the real version
                new Timer() {
                    @Override public void run() {
                        if (instance instanceof AsyncProxy) {
                            schedule(500);
                        } else {
                            callback.withInstance(instance);
                        }
                    }
                }.schedule(500);
            } else {
                callback.withInstance(instance);
            }
        } catch (Exception e) {
            ClientLog.log(e);
        }
    }

    @Nonnull
    protected abstract HistoryAware getInstanceInternal();
}
