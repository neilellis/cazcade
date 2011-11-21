package cazcade.vortex.gwt.util.client.history;

/**
 * @author neilellis@cazcade.com
 */
public interface HistoryAwareFactory {
    void withInstance(HistoryAwareFactoryCallback callback);

    void setHistoryManager(HistoryManager historyManager);

    void setHistoryToken(String token);
}
