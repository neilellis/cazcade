/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.gwt.util.client;

import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Window;


/**
 * @author neilellis@cazcade.com
 */
public class Config {

    private static final boolean ALPHA_FEATURES_AVAILABLE = true;
    private static boolean debug;
    private static boolean preflight = true;
    private static boolean alphaFeatures;
    private static boolean loginRequired;
    private static boolean retrieveUpdates = true;
    private static boolean register;
    private static boolean snapshotMode = false;
    private static String  debugType;
    private static boolean dev;

    public static boolean debug() {
        return debug;
    }

    public static void setDebug(final boolean debug) {
        Config.debug = debug;
    }

    public static boolean isPreflight() {
        return preflight;
    }

    public static void setPreflight(final boolean preflight) {
        Config.preflight = preflight;
    }

    public static boolean alpha() {
        if (ALPHA_FEATURES_AVAILABLE) {
            return alphaFeatures;
        } else {
            return false;
        }
    }

    public static void setAlphaFeatures(final boolean alphaFeatures) {
        Config.alphaFeatures = alphaFeatures;
    }

    public static boolean isLoginRequired() {
        return loginRequired;
    }

    public static void setLoginRequired(final boolean loginRequired) {
        Config.loginRequired = loginRequired;
    }

    public static void init() {
        setSnapshotMode(Window.Location.getParameter("snapshot") != null);
        setPreflight(Window.Location.getParameter("skippreflight") == null);
        setLoginRequired(Window.Location.getParameter("forceLogin") != null);
        setDev(Window.Location.getParameter("dev") != null);
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
        Config.retrieveUpdates = retrieveUpdates;
    }

    public static void setRegister(final boolean register) {
        Config.register = register;
    }

    public static void setSnapshotMode(boolean snapshotMode) {
        Config.snapshotMode = snapshotMode;
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
        Config.debugType = debugType;
    }

    public static String getDebugType() {
        return debugType;
    }


    public static boolean dev() {
        return dev;
    }

    public static void setDev(boolean dev) {
        Config.dev = dev;
    }
}
