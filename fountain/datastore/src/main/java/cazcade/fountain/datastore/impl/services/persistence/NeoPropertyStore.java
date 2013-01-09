package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.liquid.api.lsd.LSDPropertyStore;
import org.neo4j.graphdb.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class NeoPropertyStore implements LSDPropertyStore {
    private final Node neoNode;

    public NeoPropertyStore(final Node neoNode) {
        this.neoNode = neoNode;
    }

    @Nonnull
    @Override
    public Map<String, String> asMap() {
        throw new UnsupportedOperationException("Cannot convert Neo nodes to maps.");
    }

    @Override
    public boolean containsProperty(final String property) {
        return neoNode.hasProperty(property);
    }

    @Nonnull
    @Override
    public LSDPropertyStore copy() {
        return new NeoPropertyStore(neoNode);
    }

    @Override
    @Nullable
    public String get(final String property) {
        final Object neoNodeProperty = neoNode.getProperty(property, null);
        return neoNodeProperty != null ? neoNodeProperty.toString() : null;
    }

    @Override
    public Iterable<? extends String> getKeys() {
        return neoNode.getPropertyKeys();
    }

    @Override
    public boolean isSerializable() {
        return false;
    }

    @Override
    public void put(@Nonnull final String property, @Nonnull final String value) {
        neoNode.setProperty(property, value);
    }

    @Override
    public void remove(final String property) {
        neoNode.removeProperty(property);
    }

    @Nonnull
    @Override
    public Iterable<? extends String> valueIterator() {
        throw new UnsupportedOperationException("Cannot iterate values.");
    }
}
