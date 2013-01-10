/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api;

import cazcade.liquid.api.lsd.LSDBaseEntity;

import java.io.Serializable;
import java.security.Principal;

/**
 * @author neilelliz@cazcade.com
 */
public class LiquidPrincipal implements Principal, Serializable {
    private String        name;
    private LSDBaseEntity aliasEntity;

    public LiquidPrincipal(final String name, final LSDBaseEntity aliasEntity) {
        this.aliasEntity = aliasEntity;
    }

    public LiquidPrincipal(final String name) {
        this.name = name;
    }

    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Principal)) {
            return false;
        }

        final Principal that = (Principal) o;

        if (!name.equals(that.getName())) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        return name.hashCode();
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }
}
