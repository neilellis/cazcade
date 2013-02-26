/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.vortex.common.client;

import cazcade.common.CommonConstants;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.SessionIdentifier;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Entity;
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
    private static SessionIdentifier identity;
    @Nullable
    private static Entity            currentAlias;
    @Nonnull
    public static final String ANON            = "anon";
    @Nonnull
    public static final String ANON_ALIAS      = CommonConstants.ANONYMOUS_ALIAS;
    @Nonnull
    public static final String VORTEX_IDENTITY = "boardcast.identity";

    @Nullable
    public static SessionIdentifier getIdentity() {
        return identity;
    }

    public static void setIdentity(@Nullable final SessionIdentifier identity) {
        UserUtil.identity = identity;
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
        UserUtil.currentAlias = currentAlias;
    }

    @SuppressWarnings({"MethodParameterOfConcreteClass"})
    public static void storeIdentity(@Nullable final SessionIdentifier storeIdentity) {
        if (storeIdentity != null
            && storeIdentity.session() != null
            && ClientApplicationConfiguration.isSessionStorageSupported()) {
            Storage.getSessionStorageIfSupported().setItem(VORTEX_IDENTITY, storeIdentity.toString());
        }
    }

    @Nullable
    public static SessionIdentifier retrieveUser() {
        if (ClientApplicationConfiguration.isSessionStorageSupported()) {
            final Storage storage = Storage.getSessionStorageIfSupported();
            return SessionIdentifier.fromString(storage.getItem(VORTEX_IDENTITY));
        } else {
            //fallback to anonymous usage
            ClientLog.log("Returning anonymous identifier as session storage not available or in snapshot mode.");
            return new SessionIdentifier(new LiquidURI(ANON_ALIAS));

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
        final LiquidURI uri = currentAlias.uri();
        return uri != null && "alias:cazcade:admin".equals(uri.toString());
    }

    public static boolean isAlias(final LiquidURI uri) {
        return identity != null && identity.aliasURI().equals(uri);
    }

    @Nonnull
    public static LiquidURI getInboxURI() {
        if (currentAlias == null) {
            throw new NullPointerException("Attempted to use a null currentAlias to 'getInboxURI' in UserUtil.");
        }
        return new LiquidURI("pool:///people/" + currentAlias.$(Dictionary.NAME) + "/.inbox");
    }

    public static String getCurrentAliasName() {
        if (currentAlias != null) {
            return currentAlias.$(Dictionary.NAME);
        } else {
            return "";
        }

    }
}
