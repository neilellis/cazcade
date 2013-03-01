package cazcade.vortex.gwt.util.client;

import com.google.gwt.storage.client.Storage;

import javax.annotation.Nonnull;


public class ClientPreferences {

    public enum Preference {
        RHS_HIDE
    }

    public static boolean booleanPreference(@Nonnull final Preference pref) {
        if (!Config.isLocalStorageSupported()) {
            return false;
        }
        final String value = Storage.getLocalStorageIfSupported().getItem(prefToKey(pref));
        return "true".equals(value);
    }

    @Nonnull
    private static String prefToKey(@Nonnull final Preference pref) {
        return "cazcade.pref." + pref.name().toLowerCase();
    }

    public static void setBooleanPreference(@Nonnull final Preference pref, final boolean value) {
        if (Config.isLocalStorageSupported()) {
            Storage.getLocalStorageIfSupported().setItem(prefToKey(pref), value ? "true" : "false");
        }
    }

}
