/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * @author neilellis@cazcade.com
 */
public class SessionIdentifier implements Serializable {
    @Nonnull
    public static final SessionIdentifier ANON = new SessionIdentifier("anon", null);

    @Nullable
    private LiquidUUID session;

    @Nonnull
    private LURI alias;

    @Nullable
    public static SessionIdentifier fromString(@Nullable final String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        final String[] strings = s.split(",");
        if (strings.length > 2 || strings.length == 0) {
            throw new IllegalArgumentException("Could not create a SessionIdentifier from " + s);
        }
        if (strings.length == 2) {
            return new SessionIdentifier(strings[0], new LiquidUUID(strings[1]));
        } else {
            return new SessionIdentifier(strings[0], null);
        }
    }

    public SessionIdentifier(@Nonnull final String name, @Nullable final LiquidUUID session) {
        this(name);
        this.session = session;
    }

    public SessionIdentifier(@Nonnull final String name) {
        alias = new LURI(LiquidURIScheme.alias, "cazcade:" + name.toLowerCase());
    }

    public SessionIdentifier(@Nonnull final LURI aliasURI) {
        //noinspection ConstantConditions
        if (aliasURI == null) {
            throw new IllegalArgumentException("Cannot create and SessionIdentifier with a null LURI");
        }
        alias = aliasURI;
    }

    public SessionIdentifier() {
    }


    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        SessionIdentifier that = (SessionIdentifier) o;

        if (!alias.equals(that.alias)) { return false; }
        if (session != null ? !session.equals(that.session) : that.session != null) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = session != null ? session.hashCode() : 0;
        result = 31 * result + alias.hashCode();
        return result;
    }

    @Nonnull
    public LURI aliasURI() {
        return alias;
    }

    @Nonnull
    public LURI userURL() {
        return new LURI(LiquidURIScheme.user, name());
    }

    @Nonnull
    public String name() {
        return alias.sub().sub().asString();
    }


    public boolean anon() {
        return "alias:cazcade:anon".equals(alias.asString());
    }

    public boolean isCazcadeAlias() {
        return alias.toString().startsWith("alias:cazcade");
    }


    public void session(final String session) {
        this.session = LiquidUUID.fromString(session);
    }

    @Nonnull @Override
    public String toString() {
        if (session != null) {
            return name() + ',' + session.toString();
        } else {
            return name();
        }
        //        if (name != null) {
        //            if (alias != null) {
        //                if (session != null) {
        //                    return name + ":" + alias + ":" + session;
        //                } else {
        //                    return name + ":" + alias;
        //
        //                }
        //            } else {
        //                if (session != null) {
        //                    return name + ":" + session;
        //                } else {
        //                    return name;
        //
        //                }
        //            }
        //        } else {
        //           if (alias != null) {
        //                if (session != null) {
        //                    return "<unknown>" + ":" + alias + ":" + session;
        //                } else {
        //                    return "<unknown>"  + ":" + alias;
        //
        //                }
        //            } else {
        //                if (session != null) {
        //                    return "<unknown>"  + ":" + session;
        //                } else {
        //                    return "<unknown>" ;
        //
        //                }
        //            }
        //        }
    }

    @Nonnull
    public LURI alias() {
        return alias;
    }

    @Nullable public LiquidUUID session() {
        return session;
    }


}
