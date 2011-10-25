package cazcade.liquid.api;

import cazcade.liquid.api.lsd.LSDEntity;

import java.io.Serializable;
import java.security.Principal;

/**
* @author neilelliz@cazcade.com
*/
public class LiquidPrincipal implements Principal, Serializable {
    private String name;
    private LSDEntity aliasEntity;

    public LiquidPrincipal(String name) {
        this.name = name;
    }

    public LiquidPrincipal(String name, LSDEntity aliasEntity) {
        this.aliasEntity= aliasEntity;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Principal)) return false;

        Principal that = (Principal) o;

        if (!name.equals(that.getName())) return false;

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
