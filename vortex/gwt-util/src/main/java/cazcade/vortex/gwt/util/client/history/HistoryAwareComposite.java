/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client.history;

import com.google.gwt.user.client.ui.Composite;

/**
 * @author neilellis@cazcade.com
 */
public class HistoryAwareComposite extends Composite implements HistoryAware {

    private HistoryManager historyManager;
    private String         historyToken;

    @Override
    public void onLocalHistoryTokenChanged(final String token) {
    }


    @Override
    public void setHistoryManager(final HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void setHistoryToken(final String historyToken) {
        this.historyToken = historyToken;
    }

    @Override
    public String getHistoryToken() {
        return historyToken;
    }

    @Override
    public boolean addToRootPanel() {
        return true;
    }

    @Override public void beforeInactive() {

    }

    @Override public void onActive() {

    }
}
