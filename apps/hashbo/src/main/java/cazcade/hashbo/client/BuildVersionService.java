package cazcade.hashbo.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;

import java.io.FileNotFoundException;

/**
 * @author neilellis@cazcade.com
 */
@RemoteServiceRelativePath("BuildVersionService")
public interface BuildVersionService extends RemoteService {
    String getBuildVersion();

    /**
     * Utility/Convenience class.
     * Use BuildVersionService.App.getInstance() to access static instance of BuildVersionServiceAsync
     */
    public static class App {
        private static final BuildVersionServiceAsync ourInstance = (BuildVersionServiceAsync) GWT.create(BuildVersionService.class);

        public static BuildVersionServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
