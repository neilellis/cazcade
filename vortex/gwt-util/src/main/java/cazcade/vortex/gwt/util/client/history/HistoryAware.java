/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client.history;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * @author neilellis@cazcade.com
 */
public interface HistoryAware extends IsWidget {
    void onLocalHistoryTokenChanged(String token);

    void setHistoryManager(HistoryManager historyManager);

    HistoryManager getHistoryManager();

    void setHistoryToken(String historyToken);

    String getHistoryToken();

    boolean addToRootPanel();

    void beforeInactive();

    void onActive();
}
