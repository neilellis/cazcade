/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.common.client;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.Entity;
import cazcade.vortex.gwt.util.client.Config;
import cazcade.vortex.gwt.util.client.ClientLog;
import com.google.gwt.storage.client.Storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cazcade.liquid.api.lsd.Dictionary.*;

/**
 * @author neilellis@cazcade.com
 */
@SuppressWarnings({"StaticNonFinalField"})
public class User {

    private static final String dummy= "";

    @Nullable
    private static SessionIdentifier identity;
    @Nullable
    private static Entity            currentAlias;
    @Nonnull
    public static final String ANON            = "anon";
    @Nonnull
    public static final String ANON_ALIAS      = "alias:cazcade:anon";
    @Nonnull
    public static final String VORTEX_IDENTITY = "boardcast.identity";

    @Nullable
    public static SessionIdentifier getIdentity() {
        return identity;
    }

    public static void setIdentity(@Nullable final SessionIdentifier identity) {
        User.identity = identity;
    }

    public static boolean anon() {
        return identity == null || "anon".equals(identity.name());
    }

    @Nonnull
    public static Entity currentAlias() {
        if (currentAlias == null) {
            throw new IllegalStateException("Attempted to get the current alias when it has not been set, check if logged in first.");
        }
        return currentAlias;
    }

    public static void setCurrentAlias(@Nullable final Entity currentAlias) {
        User.currentAlias = currentAlias;
    }

    @SuppressWarnings({"MethodParameterOfConcreteClass"})
    public static void storeIdentity(@Nullable final SessionIdentifier storeIdentity) {
        if (storeIdentity != null
            && storeIdentity.session() != null
            && Config.isSessionStorageSupported()) {
            Storage.getSessionStorageIfSupported().setItem(VORTEX_IDENTITY, storeIdentity.toString());
        }
    }

    @Nullable
    public static SessionIdentifier retrieveUser() {
        if (Config.isSessionStorageSupported()) {
            final Storage storage = Storage.getSessionStorageIfSupported();
            return SessionIdentifier.fromString(storage.getItem(VORTEX_IDENTITY));
        } else {
            //fallback to anonymous usage
            ClientLog.log("Returning anonymous identifier as session storage not available or in snapshot mode.");
            return new SessionIdentifier(new LURI(ANON_ALIAS));

        }
    }

    public static void removeIdentity() {
        if (Config.isSessionStorageSupported()) {
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
        final LURI uri = currentAlias.uri();
        return uri != null && "alias:cazcade:admin".equals(uri.toString());
    }

    public static boolean isAlias(final LURI uri) {
        return identity != null && identity.aliasURI().equals(uri);
    }

    @Nonnull
    public static LURI getInboxURI() {
        if (currentAlias == null) {
            throw new NullPointerException("Attempted to use a null currentAlias to 'getInboxURI' in User.");
        }
        return new LURI("pool:///people/" + currentAlias.$(NAME) + "/.inbox");
    }

    public static String getCurrentAliasName() {
        if (currentAlias != null) {
            return currentAlias.$(NAME);
        } else {
            return "";
        }

    }
}
