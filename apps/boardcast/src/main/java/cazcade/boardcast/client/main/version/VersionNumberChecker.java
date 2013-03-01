/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.boardcast.client.main.version;

import cazcade.boardcast.client.BuildVersionService;
import cazcade.vortex.gwt.util.client.Config;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * @author neilellis@cazcade.com
 */
public class VersionNumberChecker {
    @Nullable private static String version;
    private static HashMap<String, String> properties = new HashMap<String, String>();

    public static void start() {
        getVersionFromServer();
        new Timer() {
            @Override
            public void run() {
                getVersionFromServer();
            }
        }.scheduleRepeating(180 * 1000);
    }

    private static void getVersionFromServer() {
        BuildVersionService.App.getInstance().getBuildVersion(new AsyncCallback<HashMap<String, String>>() {
            @Override public void onFailure(Throwable caught) {
                ClientLog.log(caught);
            }

            @Override public void onSuccess(HashMap<String, String> result) {
                if (Config.debug()) {
                    ClientLog.logImportant("Build version details " + result);
                }
                String newVersion = result.get("build.timestamp");
                if (version != null && !newVersion.equals(version)) {
                    if(Config.dev()) {
                        ClientLog.logVeryImportant("VERSION CHANGE: " + version + " -> " + newVersion);
                        Window.Location.reload();
                    } else {
                        Window.alert("New version released, please refresh your browser.");
                    }
                }
                version = newVersion;
                properties = result;

            }
        });
    }

    public static String getBuildNumber() {
        return version;
    }
}
