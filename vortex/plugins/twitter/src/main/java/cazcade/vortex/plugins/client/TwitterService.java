package cazcade.vortex.plugins.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author neilellis@cazcade.com
 */
@RemoteServiceRelativePath("TwitterService")
public interface TwitterService extends RemoteService {
    /**
     * Utility/Convenience class.
     * Use TwitterService.App.getInstance() to access static instance of TwitterServiceAsync
     */
    public static class App {
        private static final TwitterServiceAsync ourInstance = (TwitterServiceAsync) GWT.create(TwitterService.class);

        public static TwitterServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
