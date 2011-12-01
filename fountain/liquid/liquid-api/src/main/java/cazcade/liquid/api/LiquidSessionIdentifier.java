package cazcade.liquid.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * @author neilellis@cazcade.com
 */
public class LiquidSessionIdentifier implements Serializable {

    private LiquidUUID session;

    @Nonnull
    private LiquidURI alias;

    @Nonnull
    public static final LiquidSessionIdentifier ANON = new LiquidSessionIdentifier("anon", null);

    public LiquidSessionIdentifier() {
    }

    public LiquidSessionIdentifier(@Nonnull String name) {
        this.alias = new LiquidURI(LiquidURIScheme.alias, "cazcade:" + name.toLowerCase());
    }

    public LiquidSessionIdentifier(@Nonnull String name, LiquidUUID session) {
        this(name);
        this.session = session;
    }

    public LiquidSessionIdentifier(@Nonnull LiquidURI aliasURI) {
        this.alias = aliasURI;
    }

    @Nonnull
    public String getName() {
        return alias.getSubURI().getSubURI().asString();
    }

    public LiquidUUID getSession() {
        return session;
    }


    public void setSession(String session) {
        this.session = LiquidUUID.fromString(session);
    }

    @Nonnull
    public LiquidURI getUserURL() {
        return new LiquidURI(LiquidURIScheme.user, getName());
    }

    @Nonnull
    public LiquidURI getAliasURL() {
        return alias;
    }


    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LiquidSessionIdentifier that = (LiquidSessionIdentifier) o;

        if (!session.equals(that.session)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return session.hashCode();
    }

    @Nonnull
    public LiquidURI getAlias() {
        return alias;

    }

    @Nullable
    public static LiquidSessionIdentifier fromString(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        final String[] strings = s.split(",");
        if (strings.length > 2 || strings.length == 0) {
            throw new IllegalArgumentException("Could not create a LiquidSessionIdentifier from " + s);
        }
        if (strings.length == 2) {
            return new LiquidSessionIdentifier(strings[0], new LiquidUUID(strings[1]));
        } else {
            return new LiquidSessionIdentifier(strings[0], null);
        }
    }

    @Nullable
    @Override
    public String toString() {
        if (session != null) {
            return getName() + "," + session.toString();
        } else {
            return getName();
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

    public boolean isAnon() {
        return alias.asString().equals("alias:cazcade:anon");
    }
}
