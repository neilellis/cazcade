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
        new Timer() {
            @Override
            public void run() {
                BuildVersionService.App.getInstance().getBuildVersion(new AsyncCallback<String>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        ClientLog.log("Failed to obtain version number.");
                    }

                    @Override
                    public void onSuccess(String result) {
                        ClientLog.log("Build version is " + result);
                        if (version == null) {
                            version = result;
                            return;
                        }
                        if (!version.equals(result)) {
                            Window.Location.reload();
                        }
                    }
                });
            }
        }.scheduleRepeating(180 * 1000);
    }

    public static String getBuildNumber() {
        return version;
    }
}
