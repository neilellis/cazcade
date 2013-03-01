/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.comms.datastore.client;


import cazcade.liquid.api.LURI;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.TransferEntity;
import cazcade.liquid.api.request.SerializedRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

@RemoteServiceRelativePath("DataStoreService")
public interface DataStoreService extends RemoteService {
    @Nonnull String X_VORTEX_CACHE_SCOPE  = "X-Vortex-Cache-Scope";
    @Nonnull String X_VORTEX_CACHE_EXPIRY = "X-Vortex-Cache-Expiry";
    @Nonnull String X_VORTEX_SINCE        = "X-Vortex-Cache-Since";
    // Sample interface method of remote interface

    void logout(SessionIdentifier identity);

    @Nullable SessionIdentifier login(String username, String password);

    @Nullable SessionIdentifier loginQuick(boolean anon);

    @Nullable TransferEntity register(String fullname, String username, String password, String emailAddress);

    boolean checkUsernameAvailability(String username);

    @Nullable ArrayList<SerializedRequest> collect(SessionIdentifier identity, ArrayList<String> location) throws Exception;

    @Nullable SerializedRequest process(SerializedRequest request) throws Exception;

    boolean checkBoardAvailability(LURI board);

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
