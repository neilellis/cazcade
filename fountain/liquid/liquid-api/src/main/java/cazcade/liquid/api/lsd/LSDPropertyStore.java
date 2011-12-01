package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public interface LSDPropertyStore extends Serializable {
    void put(String property, String value);

    String get(String property);

    Iterable<? extends String> getProperties();

    void remove(String property);

    @Nonnull
    Map<String, String> asMap();

    boolean containsProperty(String property);

    Iterable<? extends String> valueIterator();

    @Nonnull
    LSDPropertyStore copy();

    boolean isSerializable();
}
