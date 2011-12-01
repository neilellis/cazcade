package cazcade.vortex.gwt.util.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
@RemoteServiceRelativePath("WebScrapeService")
public interface WebScrapeService extends RemoteService {
    /**
     * Utility/Convenience class.
     * Use WebScrapeService.App.getInstance() to access static instance of WebScrapeServiceAsync
     */
    public static class App {
        @Nonnull
        private static final WebScrapeServiceAsync ourInstance = (WebScrapeServiceAsync) GWT.create(WebScrapeService.class);

        @Nonnull
        public static WebScrapeServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
