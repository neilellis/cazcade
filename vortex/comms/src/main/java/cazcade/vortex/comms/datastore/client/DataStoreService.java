package cazcade.vortex.comms.datastore.client;


import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDEntity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import java.util.ArrayList;

@RemoteServiceRelativePath("DataStoreService")
public interface DataStoreService extends RemoteService {
    String X_VORTEX_CACHE_SCOPE = "X-Vortex-Cache-Scope";
    String X_VORTEX_CACHE_EXPIRY = "X-Vortex-Cache-Expiry";
    String X_VORTEX_SINCE = "X-Vortex-Cache-Since";
    // Sample interface method of remote interface

    void logout(LiquidSessionIdentifier identity);

    /**
     * The application identifier should return the same value for the same release of the application.
     *
     * @return a string identifying the release of the application.
     */
    String getApplicationIdentifier();

    LiquidSessionIdentifier login(String username, String password);

    LiquidSessionIdentifier loginQuick(boolean anon);

    LSDEntity register(String fullname, String username, String password, String emailAddress);

    boolean checkUsernameAvailability(String username);

    ArrayList<LiquidMessage> collect(LiquidSessionIdentifier identity, ArrayList<String> location) throws Exception;

    LiquidMessage process(LiquidRequest request);

    boolean checkBoardAvailability(LiquidURI board);

    /**
     * Utility/Convenience class.
     * Use DataStoreService.App.getInstance () to access static instance of DataStoreServiceAsync
     */
    public static class App {
        private static DataStoreServiceAsync ourInstance = GWT.create(DataStoreService.class);

        public static synchronized DataStoreServiceAsync getInstance() {

            return ourInstance;
        }
    }
}
