package cazcade.liquid.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * @author neilellis@cazcade.com
 */
public class LiquidURI implements Serializable {
    @Nonnull
    public static final String POOL_SCHEME_PREFIX = "pool://";
    @Nonnull
    protected String uri;

    public LiquidURI(@Nonnull final LiquidURIScheme scheme, @Nonnull final String path) {
        this(scheme.name() + ":" + lowerCaseIfRequired(scheme, path));
    }

    private static String lowerCaseIfRequired(final LiquidURIScheme scheme, @Nonnull final String path) {
        if (scheme == LiquidURIScheme.alias || scheme == LiquidURIScheme.user || scheme == LiquidURIScheme.session) {
            return path.toLowerCase();
        }
        else {
            return path;
        }
    }

    public LiquidURI(@Nonnull final LiquidURIScheme scheme, @Nonnull final LiquidURI url) {
        this(scheme.name() + ":" + url.asString());
    }

    @Nonnull
    public String asString() {
        return uri;
    }

    public LiquidURI(@Nonnull final LiquidURI url, @Nonnull final String subPath) {
        this(url.getPath().endsWith("/") ? url + lowerCaseIfRequired(url.getScheme(), subPath) : lowerCaseIfRequired(
                url.getScheme(), subPath
                                                                                                                    ) +
                                                                                                 "/" +
                                                                                                 subPath
            );
    }

    public LiquidURIScheme getScheme() {
        final String schemeStr = getSchemeAsString();
        if (schemeStr == null) {
            throw new InvalidURLException("No scheme.");
        }
        return LiquidURIScheme.valueOf(schemeStr);
    }

    @Nullable
    public String getSchemeAsString() {
        final int index = uri.indexOf(':');
        if (index <= 0) {
            return null;
        }
        return uri.substring(0, index);
    }

    public LiquidURI(@Nullable final String uri) {
        if (uri == null) {
            throw new NullPointerException("Attempted to create a LiquidURI with a null uri string parameter");
        }
        if (uri.isEmpty()) {
            throw new IllegalArgumentException("Attempted to create an empty URI.");
        }
        //Various Liquid URIs should be lowercase only
        if (uri.startsWith(POOL_SCHEME_PREFIX) || uri.startsWith("alias:") || uri.startsWith("user:") || uri.startsWith("session:"
                                                                                                                       )) {
            this.uri = uri.toLowerCase();
        }
        else {
            this.uri = uri;
        }
    }

    public LiquidURI() {
    }

    @Nonnull
    public String asReverseDNSString() {
        final String scheme = getSchemeAsString();
        String reverseDNS = "";
        if (scheme == null) {
            int count = 0;
            for (final String element : getPathElements()) {
                if (element.length() > 0) {
                    reverseDNS = (count == 0 ? "" : reverseDNS + ".") + element;
                    count++;
                }
            }
            if (getFragment() != null && getFragment().length() > 0) {
                reverseDNS = (count == 0 ? "" : reverseDNS + ".") + getFragment().substring(1);
            }
        }
        else {
            reverseDNS = scheme + "." + getSubURI().asReverseDNSString();
        }
        return reverseDNS;
    }

    public String getFragment() {
        final int index = uri.indexOf('#');
        if (index < 0) {
            return "";
        }
        else {
            return uri.substring(index);
        }
    }

    @Nonnull
    public LiquidBoardURL asShortUrl() {
        return new LiquidBoardURL(this);
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LiquidURI liquidURI = (LiquidURI) o;

        if (uri != null ? !uri.equals(liquidURI.uri) : liquidURI.uri != null) {
            return false;
        }

        return true;
    }

    @Nullable
    public String getLastPathElement() {
        final String[] pathElements = getPathElements();
        if (pathElements == null || pathElements.length == 0) {
            return null;
        }
        return pathElements[pathElements.length - 1];
    }

    @Nullable
    private String[] getPathElements() {
        final String path = getPath();
        if (path == null) {
            return null;
        }
        return path.split("/");
    }

    /**
     * getParentURI().equals(this) if  top level path element
     *
     * @return
     */
    @Nonnull
    public LiquidURI getParentURI() {
        final String scheme;
        if (uri.startsWith(POOL_SCHEME_PREFIX)) {
            scheme = POOL_SCHEME_PREFIX;
        }
        else {
            scheme = getSchemeAsString() + ":";
        }
        final String path = getPath();
        final int i = path.lastIndexOf('/');
        if (i <= 0) {
            return new LiquidURI(uri);
        }
        return new LiquidURI(scheme + path.substring(0, i));
    }

    @Nonnull
    public String getPath() {
        final String withoutFragment;
        final int i = uri.indexOf(':');
        withoutFragment = getWithoutFragment().asString();
        if (withoutFragment.startsWith(LiquidURI.POOL_SCHEME_PREFIX)) {
            final String schemeless = withoutFragment.substring(LiquidURI.POOL_SCHEME_PREFIX.length());
            final int firstSlash = schemeless.indexOf('/');
            if (firstSlash < 0) {
                throw new InvalidURLException("Invalid url %s", uri);
            }
            return schemeless.substring(firstSlash);
        }
        else if (i >= 0) {
            return withoutFragment.substring(i + 1);
        }
        else {
            return withoutFragment;
        }
    }

    @Nonnull
    public LiquidURI getSubURI() {
        final int i = getColonPos();
        return new LiquidURI(uri.substring(i + 1));
    }

    private int getColonPos() {
        final int i = uri.indexOf(':');
        if (i < 0) {
            throw new InvalidURLException("No scheme.");
        }
        return i;
    }

    @Nonnull
    public LiquidURI getWithoutFragment() {
        if (uri.contains("#")) {
            final int i = uri.indexOf('#');
            return new LiquidURI(uri.substring(0, i));
        }
        else {
            return new LiquidURI(uri);
        }
    }

    @Nonnull
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

    public boolean hasFragment() {
        return uri.contains("#");
    }

    @Override
    public int hashCode() {
        return uri != null ? uri.hashCode() : 0;
    }

    @Nullable
    public String toString() {
        return uri;
    }
}
