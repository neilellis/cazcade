package cazcade.vortex.gwt.util.client.history;

import com.google.gwt.user.client.ui.Composite;

/**
 * @author neilellis@cazcade.com
 */
public  class HistoryAwareComposite extends Composite {

    private HistoryManager historyManager;
    private String historyToken;

    public  void onLocalHistoryTokenChanged(String token) {}


    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public void setHistoryToken(String historyToken) {
        this.historyToken = historyToken;
    }

    public String getHistoryToken() {
        return historyToken;
    }
}
