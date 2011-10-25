package cazcade.vortex.gwt.util.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;

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
        private static final WebScrapeServiceAsync ourInstance = (WebScrapeServiceAsync) GWT.create(WebScrapeService.class);

        public static WebScrapeServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
