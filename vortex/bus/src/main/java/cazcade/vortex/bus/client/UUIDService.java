package cazcade.vortex.bus.client;

import cazcade.liquid.api.LiquidUUID;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import javax.annotation.Nonnull;
import java.util.ArrayList;


/**
 * @author neilellis@cazcade.com
 */
@RemoteServiceRelativePath("UUIDService")
public interface UUIDService extends RemoteService {

    @Nonnull
    ArrayList<LiquidUUID> getRandomUUIDs(int count);

    /**
     * Utility/Convenience class.
     * Use UUIDService.App.getInstance() to access static instance of UUIDServiceAsync
     */
    class App {
        @Nonnull
        private static final UUIDServiceAsync ourInstance = (UUIDServiceAsync) GWT.create(UUIDService.class);

        @Nonnull
        public static UUIDServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
