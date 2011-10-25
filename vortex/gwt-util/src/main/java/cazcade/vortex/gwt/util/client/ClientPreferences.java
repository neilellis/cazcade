package cazcade.vortex.gwt.util.client;

import com.google.gwt.storage.client.Storage;

/**
 * Created by IntelliJ IDEA.
 * User: neilellis
 * Date: 22/08/2011
 * Time: 19:07
 * To change this template use File | Settings | File Templates.
 */
public class ClientPreferences {

    public static enum Preference {
        RHS_HIDE
    }

    public static boolean booleanPreference(Preference pref) {
        String value = Storage.getLocalStorageIfSupported().getItem(prefToKey(pref));
        return "true".equals(value);
    }

    private static String prefToKey(Preference pref) {
        return "cazcade.pref." + pref.name().toLowerCase();
    }

    public static void setBooleanPreference(Preference pref, boolean value) {
        Storage.getLocalStorageIfSupported().setItem(prefToKey(pref), value ? "true" : "false");
    }

}
