package cazcade.vortex.gwt.util.client.history;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;

/**
 * @author neilellis@cazcade.com
 */
public abstract class AbstractLazyHistoryAwareFactory implements HistoryAwareFactory {
    private HistoryManager historyManager;
    private String token;
    private HistoryAware instance;


    @Override
    public void withInstance(final HistoryAwareFactoryCallback callback) {
        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable reason) {
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

    protected abstract HistoryAware getInstanceInternal();

    @Override
    public void setHistoryManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public void setHistoryToken(String token) {
        this.token = token;
    }
}
