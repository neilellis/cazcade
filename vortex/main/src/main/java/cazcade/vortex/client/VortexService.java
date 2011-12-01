package cazcade.vortex.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import javax.annotation.Nonnull;

@RemoteServiceRelativePath("VortexService")
public interface VortexService extends RemoteService {
    // Sample interface method of remote interface

    @Nonnull
    String getMessage(String msg);

    /**
     * Utility/Convenience class.
     * Use VortexService.App.getInstance () to access static instance of VortexServiceAsync
     */
    public static class App {
        private static final VortexServiceAsync ourInstance = GWT.create(VortexService.class);

        public static synchronized VortexServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
