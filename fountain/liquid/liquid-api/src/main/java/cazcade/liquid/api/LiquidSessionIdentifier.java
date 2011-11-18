package cazcade.liquid.api;

import java.io.Serializable;

/**
 * @author neilelliz@cazcade.com
 *         <p/>
 *         TODO: This needs to become LiquidSessionIdentifier as it is not a user identity but a session identity.
 */
public class LiquidSessionIdentifier implements Serializable {

    private String name;
    private LiquidUUID session;
    private LiquidURI alias;
    public static final LiquidSessionIdentifier ANON = new LiquidSessionIdentifier("anon", null);

    public LiquidSessionIdentifier() {
    }

    @Deprecated
    public LiquidSessionIdentifier(String name) {
        this.name = name;
    }

    public LiquidSessionIdentifier(String name, LiquidUUID session) {
        this.name = name;
        this.session = session;
    }

    public String getName() {
        return name;
    }

    public LiquidUUID getSession() {
        return session;
    }


    public void setSession(String session) {
        this.session = LiquidUUID.fromString(session);
    }

    public LiquidURI getUserURL() {
        return new LiquidURI(LiquidURIScheme.user, name.toLowerCase());
    }

    public LiquidURI getAliasURL() {
        return new LiquidURI(LiquidURIScheme.alias, "cazcade:" + name.toLowerCase());
    }


    @Override
    public boolean equals(Object o) {
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

    public LiquidURI getAlias() {
        if (alias != null) {
            return alias;
        } else {
            return new LiquidURI(LiquidURIScheme.alias, "cazcade:" + getName().toLowerCase());
        }

    }

    public static LiquidSessionIdentifier fromString(String s) {
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

    @Override
    public String toString() {
        if (session != null) {
            return name + "," + session.toString();
        } else {
            return name;
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
        return name.equalsIgnoreCase("anon");
    }
}
