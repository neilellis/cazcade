package cazcade.liquid.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * @author neilellis@cazcade.com
 */
public class LiquidSessionIdentifier implements Serializable {
    @Nonnull
    public static final LiquidSessionIdentifier ANON = new LiquidSessionIdentifier("anon", null);

    private LiquidUUID session;

    @Nonnull
    private LiquidURI alias;

    @Nullable
    public static LiquidSessionIdentifier fromString(@Nullable final String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        final String[] strings = s.split(",");
        if (strings.length > 2 || strings.length == 0) {
            throw new IllegalArgumentException("Could not create a LiquidSessionIdentifier from " + s);
        }
        if (strings.length == 2) {
            return new LiquidSessionIdentifier(strings[0], new LiquidUUID(strings[1]));
        }
        else {
            return new LiquidSessionIdentifier(strings[0], null);
        }
    }

    public LiquidSessionIdentifier(@Nonnull final String name, @Nullable final LiquidUUID session) {
        this(name);
        this.session = session;
    }

    public LiquidSessionIdentifier(@Nonnull final String name) {
        alias = new LiquidURI(LiquidURIScheme.alias, "cazcade:" + name.toLowerCase());
    }

    public LiquidSessionIdentifier(@Nonnull final LiquidURI aliasURI) {
        //noinspection ConstantConditions
        if(aliasURI == null) {
            throw new IllegalArgumentException("Cannot create and LiquidSessionIdentifier with a null LiquidURI");
        }
        alias = aliasURI;
    }

    public LiquidSessionIdentifier() {
    }


    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LiquidSessionIdentifier that = (LiquidSessionIdentifier) o;

        if (!alias.equals(that.alias)) return false;
        if (session != null ? !session.equals(that.session) : that.session != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = session != null ? session.hashCode() : 0;
        result = 31 * result + alias.hashCode();
        return result;
    }

    @Nonnull
    public LiquidURI getAliasURL() {
        return alias;
    }

    @Nonnull
    public LiquidURI getUserURL() {
        return new LiquidURI(LiquidURIScheme.user, getName());
    }

    @Nonnull
    public String getName() {
        return alias.getSubURI().getSubURI().asString();
    }



    public boolean isAnon() {
        return "alias:cazcade:anon".equals(alias.asString());
    }

    public void setSession(final String session) {
        this.session = LiquidUUID.fromString(session);
    }

    @Nonnull
    @Override
    public String toString() {
        if (session != null) {
            return getName() + "," + session.toString();
        }
        else {
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

    @Nonnull
    public LiquidURI getAlias() {
        return alias;
    }

    public LiquidUUID getSession() {
        return session;
    }

    public void setSession(LiquidUUID session) {
        this.session = session;
    }

    public void setAlias(@Nonnull LiquidURI alias) {
        this.alias = alias;
    }
}
