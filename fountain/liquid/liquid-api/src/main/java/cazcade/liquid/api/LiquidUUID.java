package cazcade.liquid.api;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * @author neilellis@cazcade.com
 */
public class LiquidUUID implements Serializable {

    private String s;

    public LiquidUUID() {
    }

    public LiquidUUID(final String s) {
        this.s = s;
    }

    @Nonnull
    public static LiquidUUID fromString(final String s) {
        return new LiquidUUID(s);
    }

    @Override
    public String toString() {
        return s;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LiquidUUID that = (LiquidUUID) o;

        if (s != null ? !s.equals(that.s) : that.s != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return s != null ? s.hashCode() : 0;
    }
}
