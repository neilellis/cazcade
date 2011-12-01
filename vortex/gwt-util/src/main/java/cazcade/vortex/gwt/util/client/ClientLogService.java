package cazcade.vortex.gwt.util.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import javax.annotation.Nonnull;

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
    class App {
        @Nonnull
        private static final ClientLogServiceAsync ourInstance = (ClientLogServiceAsync) GWT.create(ClientLogService.class);

        @Nonnull
        public static ClientLogServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
