package cazcade.boardcast.client;

import cazcade.liquid.api.LiquidSessionIdentifier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import javax.annotation.Nonnull;

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
        @Nonnull
        private static final ScriptServiceAsync ourInstance = (ScriptServiceAsync) GWT.create(ScriptService.class);

        @Nonnull
        public static ScriptServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
