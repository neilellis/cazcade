package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author neilellis@cazcade.com
 */
public class LSDMapPropertyStore implements LSDPropertyStore {
    private static final long serialVersionUID = 478281508458564018L;
    @Nonnull
    private final TreeMap<String, String> map = new TreeMap<String, String>();

    public LSDMapPropertyStore(final Map<String, String> lsdProperties) {
        map.putAll(lsdProperties);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public LSDMapPropertyStore() {
    }

    @Nonnull
    @Override
    public Map<String, String> asMap() {
        return new HashMap<String, String>(map);
    }

    @Override
    public boolean containsProperty(final String property) {
        return map.containsKey(property);
    }

    @Nonnull
    @Override
    public LSDPropertyStore copy() {
        return new LSDMapPropertyStore(map);
    }

    @Override
    public String get(final String property) {
        return map.get(property);
    }

    @Override
    public Iterable<? extends String> getKeys() {
        return map.keySet();
    }

    @Override
    public boolean isSerializable() {
        return true;
    }

    @Override
    public void put(@Nonnull final String property, @Nonnull final String value) {
        //noinspection ConstantConditions
        if(property == null) {
            throw new IllegalArgumentException("Key placed into LSDMapPropertyStore was null.");
        }
        //noinspection ConstantConditions
        if(value == null) {
            throw new IllegalArgumentException("Key placed into LSDMapPropertyStore was null.");
        }
        map.put(property, value);
    }

    @Override
    public void remove(final String property) {
        map.remove(property);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("LSDMapPropertyStore");
        sb.append("{map=").append(map);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public Iterable<? extends String> valueIterator() {
        return map.values();
    }
}
