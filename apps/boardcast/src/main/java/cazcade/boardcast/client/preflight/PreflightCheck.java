package cazcade.boardcast.client.preflight;

import cazcade.vortex.gwt.util.client.ClientApplicationConfiguration;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

import static com.google.gwt.modernizr.client.Modernizr.*;

/**
 * @author neilellis@cazcade.com
 */
public class PreflightCheck {


    public static void check() {
        if (ClientApplicationConfiguration.isSnapshotMode()) {
            //snapshot mode is under our control so no preflight check.
            return;
        }
        check(borderRadius(), "Border Radius");
        check(boxShadow(), "Box Shadow");
        check(cssAnimations(), "CSS Animations");
        //todo - restore this when not buggy
//        check(cssTransforms3d(), "3D Transforms");
        check(cssTransitions(), "CSS Transitions");
        check(localStorage(), "Local Storage");
        check(opacity(), "Opacity");
        if (Window.Navigator.getUserAgent().contains("MSIE")) {
            Window.Location.assign("../unsupported_browser.html?message=IE+Not+Supported+Yet");
        }
        if (Window.Navigator.getUserAgent().contains("iPhone") || Window.Navigator.getUserAgent().contains("iPod")) {
            Window.Location.assign("../unsupported_browser.html?message=Mobile+devices+not+supported+yet+" + URL.encode(
                    Window.Navigator.getUserAgent()
                                                                                                                       )
                                  );
        }
    }


    public static void check(final boolean condition, final String message) {
        if (!condition) {
            Window.Location.assign("../preflight_fail.html?message=" + URL.encode(message + " is not supported by this browser."));
        }
    }
}
