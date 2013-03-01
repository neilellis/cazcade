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
public class LURI implements Serializable {
    @Nonnull
    public static final String POOL_SCHEME_PREFIX = "pool://";
    @Nonnull
    protected String uri;



    public LURI(@Nonnull final LiquidURIScheme scheme, @Nonnull final String path) {
        this(scheme.name() + ":" + lowerCaseIfRequired(scheme, path));
    }

    private static String lowerCaseIfRequired(final LiquidURIScheme scheme, @Nonnull final String path) {
        if (scheme == LiquidURIScheme.alias || scheme == LiquidURIScheme.user || scheme == LiquidURIScheme.session) {
            return path.toLowerCase();
        } else {
            return path;
        }
    }

    public LURI(@Nonnull final LiquidURIScheme scheme, @Nonnull final LURI url) {
        this(scheme.name() + ":" + url.asString());
    }

    @Nonnull
    public String asString() {
        return uri;
    }

    public LURI(@Nonnull final LURI url, @Nonnull final String subPath) {
        this(url.path().endsWith("/")
             ? url + lowerCaseIfRequired(url.scheme(), subPath)
             : lowerCaseIfRequired(url.scheme(), subPath) +
               "/" +
               subPath);
    }

    public LiquidURIScheme scheme() {
        final String schemeStr = schemeString();
        if (schemeStr == null) {
            throw new InvalidURLException("No scheme.");
        }
        return LiquidURIScheme.valueOf(schemeStr);
    }

    @Nullable
    public String schemeString() {
        final int index = uri.indexOf(':');
        if (index <= 0) {
            return null;
        }
        return uri.substring(0, index);
    }

    public LURI(@Nullable final String uri) {
        if (uri == null) {
            throw new NullPointerException("Attempted to create a LURI with a null uri string parameter");
        }
        if (uri.isEmpty()) {
            throw new IllegalArgumentException("Attempted to create an empty URI.");
        }
        //Various Liquid URIs should be lowercase only
        if (uri.startsWith(POOL_SCHEME_PREFIX)
            || uri.startsWith("alias:")
            || uri.startsWith("user:")
            || uri.startsWith("session:")) {
            this.uri = uri.toLowerCase();
        } else {
            this.uri = uri;
        }
    }

    public LURI() {
    }

    @Nonnull
    public String asReverseDNSString() {
        final String scheme = schemeString();
        String reverseDNS = "";
        if (scheme == null) {
            int count = 0;
            for (final String element : pathElements()) {
                if (element.length() > 0) {
                    reverseDNS = (count == 0 ? "" : reverseDNS + ".") + element;
                    count++;
                }
            }
            if (getFragment() != null && getFragment().length() > 0) {
                reverseDNS = (count == 0 ? "" : reverseDNS + ".") + getFragment().substring(1);
            }
        } else {
            reverseDNS = scheme + "." + sub().asReverseDNSString();
        }
        return reverseDNS;
    }

    public String getFragment() {
        final int index = uri.indexOf('#');
        if (index < 0) {
            return "";
        } else {
            return uri.substring(index);
        }
    }

    @Nonnull
    public BoardURL board() {
        return new BoardURL(this);
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LURI liquidURI = (LURI) o;

        if (!uri.equals(liquidURI.uri)) {
            return false;
        }

        return true;
    }

    @Nullable
    public String lastPath() {
        final String[] pathElements = pathElements();
        if (pathElements.length == 0) {
            return null;
        }
        return pathElements[pathElements.length - 1];
    }

    @Nonnull
    private String[] pathElements() {
        final String path = path();
        return path.split("/");
    }

    /**
     * parent().equals(this) if  top level path element
     *
     * @return
     */
    @Nonnull
    public LURI parent() {
        final String scheme;
        if (uri.startsWith(POOL_SCHEME_PREFIX)) {
            scheme = POOL_SCHEME_PREFIX;
        } else {
            scheme = schemeString() + ":";
        }
        final String path = path();
        final int i = path.lastIndexOf('/');
        if (i <= 0) {
            return new LURI(uri);
        }
        return new LURI(scheme + path.substring(0, i));
    }

    @Nonnull
    public String path() {
        final String withoutFragment;
        final int i = uri.indexOf(':');
        withoutFragment = withoutFragment().asString();
        if (withoutFragment.startsWith(LURI.POOL_SCHEME_PREFIX)) {
            final String schemeless = withoutFragment.substring(LURI.POOL_SCHEME_PREFIX.length());
            final int firstSlash = schemeless.indexOf('/');
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

    @Nonnull
    public LURI sub() {
        final int i = getColonPos();
        return new LURI(uri.substring(i + 1));
    }

    private int getColonPos() {
        final int i = uri.indexOf(':');
        if (i < 0) {
            throw new InvalidURLException("No scheme.");
        }
        return i;
    }

    @Nonnull
    public LURI withoutFragment() {
        if (uri.contains("#")) {
            final int i = uri.indexOf('#');
            return new LURI(uri.substring(0, i));
        } else {
            return new LURI(uri);
        }
    }

    @Nonnull
    public LURI withoutFragmentOrComment() {
        String newStr = uri;
        if (uri.contains("#")) {
            final int i = uri.indexOf('#');
            newStr = uri.substring(0, i);
        }
        if (uri.contains("~")) {
            final int i = uri.indexOf('~');
            newStr = uri.substring(0, i);
        }
        return new LURI(newStr);
    }

    public boolean hasFragment() {
        return uri.contains("#");
    }

    @Override
    public int hashCode() {
        return uri.hashCode();
    }

    @Nonnull
    public String toString() {
        return uri;
    }

    public boolean anon() {
        return uri.equals("alias:cazcade:anon");
    }
}
