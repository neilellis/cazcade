package cazcade.boardcast.client.main.version;

import cazcade.boardcast.client.BuildVersionService;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author neilellis@cazcade.com
 */
public class VersionNumberChecker {
    private static String version;

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
        BuildVersionService.App.getInstance().getBuildVersion(new AsyncCallback<String>() {
            @Override
            public void onFailure(final Throwable caught) {
                ClientLog.log("Failed to obtain version number.");
            }

            @Override
            public void onSuccess(final String result) {
                ClientLog.log("Build version is " + result);
                if (version == null) {
                    version = result;
                    return;
                }
                if (!version.equals(result)) {
                    Window.alert("A new version of this application is available, please refresh this page.");
                }
            }
        });
    }

    public static String getBuildNumber() {
        return version;
    }
}