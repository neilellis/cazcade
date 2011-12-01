package cazcade.liquid.api;

import cazcade.liquid.api.lsd.LSDBaseEntity;

import java.io.Serializable;
import java.security.Principal;

/**
 * @author neilelliz@cazcade.com
 */
public class LiquidPrincipal implements Principal, Serializable {
    private String name;
    private LSDBaseEntity aliasEntity;

    public LiquidPrincipal(final String name) {
        this.name = name;
    }

    public LiquidPrincipal(final String name, final LSDBaseEntity aliasEntity) {
        this.aliasEntity = aliasEntity;
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

    public String toString() {
        return name;
    }


    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }
}
