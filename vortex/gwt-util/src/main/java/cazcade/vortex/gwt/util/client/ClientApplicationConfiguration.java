/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;


/**
 * @author neilellis@cazcade.com
 */
public class ClientApplicationConfiguration {

    private static final boolean ALPHA_FEATURES_AVAILABLE = true;
    private static boolean debug;
    private static boolean preflight = true;
    private static boolean alphaFeatures;
    private static boolean loginRequired;
    private static boolean retrieveUpdates = true;
    private static boolean register;
    private static boolean snapshotMode = false;
    private static String debugType;

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(final boolean debug) {
        ClientApplicationConfiguration.debug = debug;
    }

    public static boolean isPreflight() {
        return preflight;
    }

    public static void setPreflight(final boolean preflight) {
        ClientApplicationConfiguration.preflight = preflight;
    }

    public static boolean isAlphaFeatures() {
        if (ALPHA_FEATURES_AVAILABLE) {
            return alphaFeatures;
        }
        else {
            return false;
        }
    }

    public static void setAlphaFeatures(final boolean alphaFeatures) {
        ClientApplicationConfiguration.alphaFeatures = alphaFeatures;
    }

    public static boolean isLoginRequired() {
        return loginRequired;
    }

    public static void setLoginRequired(final boolean loginRequired) {
        ClientApplicationConfiguration.loginRequired = loginRequired;
    }

    public static void init() {
        setSnapshotMode(Window.Location.getParameter("snapshot") == null);
        setPreflight(Window.Location.getParameter("skippreflight") == null);
        setLoginRequired(Window.Location.getParameter("forceLogin") != null);
        setDebug(Window.Location.getParameter("debug") != null);
        setDebugType(Window.Location.getParameter("debug"));
        setAlphaFeatures(Window.Location.getParameter("alpha") != null);
        setRetrieveUpdates(Window.Location.getParameter("noupdates") == null);
        setRegister(Window.Location.getParameter("register") != null);
    }

    public static boolean isRetrieveUpdates() {
        return retrieveUpdates;
    }

    public static void setRetrieveUpdates(final boolean retrieveUpdates) {
        ClientApplicationConfiguration.retrieveUpdates = retrieveUpdates;
    }

    public static void setRegister(final boolean register) {
        ClientApplicationConfiguration.register = register;
    }

    public static void setSnapshotMode(boolean snapshotMode) {
        ClientApplicationConfiguration.snapshotMode = snapshotMode;
    }

    public static boolean isSnapshotMode() {
        return snapshotMode;
    }

    public static boolean isSessionStorageSupported() {
        return !isSnapshotMode() && Storage.isSessionStorageSupported();
    }

    public static boolean isLocalStorageSupported() {
        return !isSnapshotMode() && Storage.isLocalStorageSupported();
    }


    public static void setDebugType(String debugType) {
        ClientApplicationConfiguration.debugType = debugType;
    }

    public static String getDebugType() {
        return debugType;
    }
}
