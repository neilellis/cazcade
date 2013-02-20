/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import javax.annotation.Nonnull;
import java.util.HashMap;

/**
 * @author neilellis@cazcade.com
 */
@RemoteServiceRelativePath("BuildVersionService")
public interface BuildVersionService extends RemoteService {
    HashMap<String, String> getBuildVersion();

    /**
     * Utility/Convenience class.
     * Use BuildVersionService.App.getInstance() to access static instance of BuildVersionServiceAsync
     */
    class App {
        @Nonnull
        private static final BuildVersionServiceAsync ourInstance = (BuildVersionServiceAsync) GWT.create(BuildVersionService.class);

        @Nonnull
        public static BuildVersionServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
