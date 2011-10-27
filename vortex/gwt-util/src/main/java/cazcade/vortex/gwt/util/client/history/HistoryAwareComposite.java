package cazcade.vortex.gwt.util.client.history;

import com.google.gwt.user.client.ui.Composite;

/**
 * @author neilellis@cazcade.com
 */
public  class HistoryAwareComposite extends Composite implements HistoryAware {

    private HistoryManager historyManager;
    private String historyToken;

    @Override
    public  void onLocalHistoryTokenChanged(String token) {}


    @Override
    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public void setHistoryToken(String historyToken) {
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
}
