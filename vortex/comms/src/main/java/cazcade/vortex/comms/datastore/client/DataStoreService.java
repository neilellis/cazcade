package cazcade.vortex.comms.datastore.client;


import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDTransferEntity;
import cazcade.liquid.api.request.SerializedRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

@RemoteServiceRelativePath("DataStoreService")
public interface DataStoreService extends RemoteService {
    @Nonnull
    String X_VORTEX_CACHE_SCOPE = "X-Vortex-Cache-Scope";
    @Nonnull
    String X_VORTEX_CACHE_EXPIRY = "X-Vortex-Cache-Expiry";
    @Nonnull
    String X_VORTEX_SINCE = "X-Vortex-Cache-Since";
    // Sample interface method of remote interface

    void logout(LiquidSessionIdentifier identity);

    /**
     * The application identifier should return the same value for the same release of the application.
     *
     * @return a string identifying the release of the application.
     */
    String getApplicationIdentifier();

    @Nullable
    LiquidSessionIdentifier login(String username, String password);

    @Nullable
    LiquidSessionIdentifier loginQuick(boolean anon);

    @Nullable
    LSDTransferEntity register(String fullname, String username, String password, String emailAddress);

    boolean checkUsernameAvailability(String username);

    @Nullable
    ArrayList<SerializedRequest> collect(LiquidSessionIdentifier identity, ArrayList<String> location) throws Exception;

    @Nullable
    SerializedRequest process(SerializedRequest request);

    boolean checkBoardAvailability(LiquidURI board);

    /**
     * Utility/Convenience class.
     * Use DataStoreService.App.getInstance () to access static instance of DataStoreServiceAsync
     */
    class App {
        private static final DataStoreServiceAsync ourInstance = GWT.create(DataStoreService.class);

        public static synchronized DataStoreServiceAsync getInstance() {

            return ourInstance;
        }
    }
}
