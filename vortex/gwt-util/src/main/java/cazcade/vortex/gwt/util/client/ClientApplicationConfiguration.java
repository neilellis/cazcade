package cazcade.vortex.gwt.util.client;

import com.google.gwt.user.client.Window;


/**
 * @author neilellis@cazcade.com
 */
public class ClientApplicationConfiguration {

    private static final boolean ALPHA_FEATURES_AVAILABLE = false;
    private static boolean debug;
    private static boolean preflight = true;
    private static boolean alphaFeatures;
    private static boolean loginRequired;
    private static boolean retrieveUpdates = true;
    private static boolean register;

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        ClientApplicationConfiguration.debug = debug;
    }

    public static boolean isPreflight() {
        return preflight;
    }

    public static void setPreflight(boolean preflight) {
        ClientApplicationConfiguration.preflight = preflight;
    }

    public static boolean isAlphaFeatures() {
        if (ALPHA_FEATURES_AVAILABLE) {
            return alphaFeatures;
        } else {
            return false;
        }
    }

    public static void setAlphaFeatures(boolean alphaFeatures) {
        ClientApplicationConfiguration.alphaFeatures = alphaFeatures;
    }

    public static boolean isLoginRequired() {
        return loginRequired;
    }

    public static void setLoginRequired(boolean loginRequired) {
        ClientApplicationConfiguration.loginRequired = loginRequired;
    }

    public static void init() {
        setPreflight(Window.Location.getParameter("skippreflight") == null);
        setLoginRequired(Window.Location.getParameter("forceLogin") != null);
        setDebug(Window.Location.getParameter("debug") != null);
        setAlphaFeatures(Window.Location.getParameter("alpha") != null);
        setRetrieveUpdates(Window.Location.getParameter("noupdates") == null);
        setRegister(Window.Location.getParameter("register") != null);
    }

    public static boolean isRetrieveUpdates() {
        return retrieveUpdates;
    }

    public static void setRetrieveUpdates(boolean retrieveUpdates) {
        ClientApplicationConfiguration.retrieveUpdates = retrieveUpdates;
    }

    public static void setRegister(boolean register) {
        ClientApplicationConfiguration.register = register;
    }

    public static boolean xxxisRegister() {
        return register;
    }
}
