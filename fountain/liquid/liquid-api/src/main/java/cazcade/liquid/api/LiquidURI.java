package cazcade.liquid.api;

import java.io.Serializable;

/**
 * @author neilellis@cazcade.com
 */
public class LiquidURI implements Serializable {
    protected String uri;
    public static final String POOL_SCHEME_PREFIX = "pool://";

    public LiquidURI(String uri) {
        if (uri == null) {
            throw new NullPointerException("Attempted to create a LiquidURI with a null uri string parameter");
        }
        if (uri.isEmpty()) {
            throw new IllegalArgumentException("Attempted to create an empty URI.");
        }
        //Various Liquid URIs should be lowercase only
        if (uri.startsWith(POOL_SCHEME_PREFIX) || uri.startsWith("alias:") || uri.startsWith("user:") || uri.startsWith("session:")) {
            this.uri = uri.toLowerCase();
        } else {
            this.uri = uri;
        }
    }

    public LiquidURI() {
    }

    public LiquidURI(LiquidURIScheme scheme, String path) {
        this((scheme.name() + ":" + lowerCaseIfRequired(scheme, path)));
    }

    private static String lowerCaseIfRequired(LiquidURIScheme scheme, String path) {
        if (scheme == LiquidURIScheme.alias || scheme == LiquidURIScheme.user || scheme == LiquidURIScheme.session) {
            return path.toLowerCase();
        } else {
            return path;
        }
    }

    public LiquidURI(LiquidURIScheme scheme, LiquidURI url) {
        this((scheme.name() + ":" +  url.asString()));
    }

    public LiquidURI(LiquidURI url, String subPath) {
        this(url.getPath().endsWith("/") ? (url + lowerCaseIfRequired(url.getScheme(), subPath)) : lowerCaseIfRequired(url.getScheme(), subPath) + "/" + subPath);
    }

    public String getSchemeAsString() {
        int index = uri.indexOf(":");
        if (index <= 0) {
            return null;
        }
        return uri.substring(0, index);
    }

    public LiquidURIScheme getScheme() {
        String schemeStr = getSchemeAsString();
        if (schemeStr == null) {
            throw new InvalidURLException("No scheme.");
        }
        return LiquidURIScheme.valueOf(schemeStr);
    }

    public String getFragment() {
        int index = uri.indexOf('#');
        if (index < 0) {
            return "";
        } else {
            return uri.substring(index);
        }
    }

    public String getPath() {
        String withoutFragment;
        int i = uri.indexOf(":");
        withoutFragment = getWithoutFragment().asString();
        if (withoutFragment.startsWith(LiquidURI.POOL_SCHEME_PREFIX)) {
            String schemeless = withoutFragment.substring(LiquidURI.POOL_SCHEME_PREFIX.length());
            int firstSlash = schemeless.indexOf('/');
            if (firstSlash < 0) {
                throw new InvalidURLException("Invalid url %s", uri);
            }
            return schemeless.substring(firstSlash);
        } else if (i >= 0) {
            return withoutFragment.substring(i + 1);
        } else {
            return withoutFragment;
        }
    }

    public String toString() {
        return uri;
    }

    public LiquidBoardURL toShortUrl() {
        return new LiquidBoardURL(this);
    }


    public String asString() {
        return uri;
    }

    public LiquidURI getSubURI() {
        int i = getColonPos();
        return new LiquidURI(uri.substring(i + 1));
    }

    private int getColonPos() {
        int i = uri.indexOf(":");
        if (i < 0) {
            throw new InvalidURLException("No scheme.");
        }
        return i;
    }

    /**
     * getParentURI().equals(this) if  top level path element
     *
     * @return
     */
    public LiquidURI getParentURI() {
        String scheme;
        if (uri.startsWith(POOL_SCHEME_PREFIX)) {
            scheme = POOL_SCHEME_PREFIX;
        } else {
            scheme = getSchemeAsString() + ":";
        }
        String path = getPath();
        final int i = path.lastIndexOf("/");
        if (i <= 0) {
            return new LiquidURI(uri);
        }
        return new LiquidURI(scheme + path.substring(0, i));
    }

    public LiquidURI getWithoutFragment() {
        if (uri.contains("#")) {
            final int i = uri.indexOf('#');
            return new LiquidURI(uri.substring(0, i));
        } else {
            return new LiquidURI(uri);
        }

    }

    public String getLastPathElement() {
        final String[] pathElements = getPathElements();
        if (pathElements == null || pathElements.length == 0) {
            return null;
        }
        return pathElements[pathElements.length - 1];
    }

    private String[] getPathElements() {
        final String path = getPath();
        if (path == null) {
            return null;
        }
        return path.split("/");
    }

    public String asReverseDNSString() {
        String scheme = getSchemeAsString();
        String reverseDNS = "";
        if (scheme == null) {
            int count = 0;
            for (String element : getPathElements()) {
                if (element.length() > 0) {
                    reverseDNS = (count == 0 ? "" : (reverseDNS + ".")) + element;
                    count++;
                }
            }
            if (getFragment() != null && getFragment().length() > 0) {
                reverseDNS = (count == 0 ? "" : (reverseDNS + ".")) + getFragment().substring(1);
            }
        } else {
            reverseDNS = scheme + "." + getSubURI().asReverseDNSString();
        }
        return reverseDNS;

    }

    public boolean hasFragment() {
        return uri.contains("#");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LiquidURI liquidURI = (LiquidURI) o;

        if (uri != null ? !uri.equals(liquidURI.uri) : liquidURI.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uri != null ? uri.hashCode() : 0;
    }

    public LiquidURI getWithoutFragmentOrComment() {
        String newStr = uri;
        if (uri.contains("#")) {
            final int i = uri.indexOf('#');
            newStr = uri.substring(0, i);
        }
        if (uri.contains("~")) {
            final int i = uri.indexOf('~');
            newStr = uri.substring(0, i);
        }
        return new LiquidURI(newStr);

    }
}
