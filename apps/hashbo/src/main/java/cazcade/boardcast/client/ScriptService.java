package cazcade.boardcast.client;

import cazcade.liquid.api.LiquidSessionIdentifier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author neilellis@cazcade.com
 */
@RemoteServiceRelativePath("ScriptService")
public interface ScriptService extends RemoteService {

    void execute(LiquidSessionIdentifier session, String script);

    /**
     * Utility/Convenience class.
     * Use ScriptService.App.getInstance() to access static instance of ScriptServiceAsync
     */
    public static class App {
        private static final ScriptServiceAsync ourInstance = (ScriptServiceAsync) GWT.create(ScriptService.class);

        public static ScriptServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
