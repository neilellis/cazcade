package cazcade.vortex.common.client;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import com.google.gwt.storage.client.Storage;
import com.sun.org.apache.bcel.internal.generic.RETURN;

/**
 * @author neilellis@cazcade.com
 */
public class UserUtil {
    private static LiquidSessionIdentifier identity;
    private static LSDEntity currentAlias;
    public static final String ANON = "anon";
    public static final String ANON_ALIAS = "alias:cazcade:anon";
    public static final String VORTEX_IDENTITY = "boardcast.identity";

    public static LiquidSessionIdentifier getIdentity() {
        return identity;
    }

    public static void setIdentity(LiquidSessionIdentifier identity) {
        UserUtil.identity = identity;
    }

    public static boolean isAnonymousOrLoggedOut() {
        return identity == null || identity.getName().equals("anon");
    }

    public static LSDEntity getCurrentAlias() {
        return currentAlias;
    }

    public static void setCurrentAlias(LSDEntity currentAlias) {
        UserUtil.currentAlias = currentAlias;
    }

    public static void storeIdentity(LiquidSessionIdentifier identity) {
        if (identity != null && identity.getSession() != null) {
            Storage.getSessionStorageIfSupported().setItem(VORTEX_IDENTITY, identity.toString());
        }
    }

    public static LiquidSessionIdentifier retrieveUser() {
        final Storage storage = Storage.getSessionStorageIfSupported();
        if (storage == null) {
            throw new RuntimeException("No session storage available.");
        }
        return LiquidSessionIdentifier.fromString(storage.getItem(VORTEX_IDENTITY));
    }

    public static void removeIdentity() {
        Storage.getSessionStorageIfSupported().removeItem(VORTEX_IDENTITY);
        identity = null;
        currentAlias = null;

    }

    public static boolean isAnonymousAliasURI(String value) {
        return ANON_ALIAS.equalsIgnoreCase(value);
    }

    public static boolean isAdmin() {
        return currentAlias.getURI().toString().equals("alias:cazcade:admin");
    }

    public static boolean isAlias(LiquidURI uri) {
        return identity != null && identity.getAliasURL().equals(uri);
    }

    public static LiquidURI getInboxURI() {
        return new LiquidURI("pool:///people/"+currentAlias.getAttribute(LSDAttribute.NAME)+"/.inbox");
    }
}
