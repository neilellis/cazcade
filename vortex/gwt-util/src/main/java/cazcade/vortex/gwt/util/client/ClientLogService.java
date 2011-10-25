package cazcade.vortex.gwt.util.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;

/**
 * @author neilellis@cazcade.com
 */
@RemoteServiceRelativePath("ClientLogService")
public interface ClientLogService extends RemoteService {
    void log(Throwable t, String log);

    /**
     * Utility/Convenience class.
     * Use ClientLogService.App.getInstance() to access static instance of ClientLogServiceAsync
     */
    public static class App {
        private static final ClientLogServiceAsync ourInstance = (ClientLogServiceAsync) GWT.create(ClientLogService.class);

        public static ClientLogServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
