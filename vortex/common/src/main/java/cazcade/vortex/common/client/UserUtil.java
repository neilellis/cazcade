package cazcade.vortex.common.client;

import cazcade.liquid.api.LiquidSessionIdentifier;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.vortex.gwt.util.client.ClientApplicationConfiguration;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.storage.client.Storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author neilellis@cazcade.com
 */
@SuppressWarnings({"StaticNonFinalField"})
public class UserUtil {
    @Nullable
    private static LiquidSessionIdentifier identity;
    @Nullable
    private static LSDBaseEntity currentAlias;
    @Nonnull
    public static final String ANON = "anon";
    @Nonnull
    public static final String ANON_ALIAS = "alias:cazcade:anon";
    @Nonnull
    public static final String VORTEX_IDENTITY = "boardcast.identity";

    @Nullable
    public static LiquidSessionIdentifier getIdentity() {
        return identity;
    }

    public static void setIdentity(@Nullable final LiquidSessionIdentifier identity) {
        UserUtil.identity = identity;
    }

    public static boolean isAnonymousOrLoggedOut() {
        return identity == null || "anon".equals(identity.getName());
    }

    @Nullable
    public static LSDBaseEntity getCurrentAlias() {
        return currentAlias;
    }

    public static void setCurrentAlias(@Nullable final LSDBaseEntity currentAlias) {
        UserUtil.currentAlias = currentAlias;
    }

    @SuppressWarnings({"MethodParameterOfConcreteClass"})
    public static void storeIdentity(@Nullable final LiquidSessionIdentifier storeIdentity) {
        if (storeIdentity != null && storeIdentity.getSession() != null
            && ClientApplicationConfiguration
                .isSessionStorageSupported()) {
            Storage.getSessionStorageIfSupported().setItem(VORTEX_IDENTITY, storeIdentity.toString());
        }
    }

    @Nullable
    public static LiquidSessionIdentifier retrieveUser() {
        if (ClientApplicationConfiguration.isSessionStorageSupported()) {
            final Storage storage = Storage.getSessionStorageIfSupported();
            return LiquidSessionIdentifier.fromString(storage.getItem(VORTEX_IDENTITY));
        }
        else {
            //fallback to anonymous usage
            ClientLog.log("Returning anonymous identifier as session storage not available or in snapshot mode.");
            return new LiquidSessionIdentifier(new LiquidURI(ANON_ALIAS));

        }
    }

    public static void removeIdentity() {
        if (ClientApplicationConfiguration.isSessionStorageSupported()) {
            Storage.getSessionStorageIfSupported().removeItem(VORTEX_IDENTITY);
        }
        identity = null;
        currentAlias = null;

    }

    public static boolean isAnonymousAliasURI(final String value) {
        return ANON_ALIAS.equalsIgnoreCase(value);
    }

    public static boolean isAdmin() {
        if (currentAlias == null) {
            return false;
        }
        final LiquidURI uri = currentAlias.getURI();
        return uri != null && "alias:cazcade:admin".equals(uri.toString());
    }

    public static boolean isAlias(final LiquidURI uri) {
        return identity != null && identity.getAliasURL().equals(uri);
    }

    @Nonnull
    public static LiquidURI getInboxURI() {
        if (currentAlias == null) {
            throw new NullPointerException("Attempted to use a null currentAlias to 'getInboxURI' in UserUtil.");
        }
        return new LiquidURI("pool:///people/" + currentAlias.getAttribute(LSDAttribute.NAME) + "/.inbox");
    }

    public static String getCurrentAliasName() {
        if (currentAlias != null) {
            return currentAlias.getAttribute(LSDAttribute.NAME);
        }
        else {
            return "";
        }

    }
}
