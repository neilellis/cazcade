/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client.history;

/**
 * @author neilellis@cazcade.com
 */
public interface HistoryLocation {

    void handle(HistoryLocationCallback callback);

    void setHistoryManager(HistoryManager historyManager);

    void setHistoryToken(String token);

    void beforeRemove();
}
