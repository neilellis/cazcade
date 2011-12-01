package cazcade.vortex.plugins.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
@RemoteServiceRelativePath("TwitterService")
public interface TwitterService extends RemoteService {
    /**
     * Utility/Convenience class.
     * Use TwitterService.App.getInstance() to access static instance of TwitterServiceAsync
     */
    class App {
        @Nonnull
        private static final TwitterServiceAsync ourInstance = (TwitterServiceAsync) GWT.create(TwitterService.class);

        @Nonnull
        public static TwitterServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
