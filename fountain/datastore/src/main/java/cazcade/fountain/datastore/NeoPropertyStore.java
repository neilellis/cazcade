package cazcade.fountain.datastore;

import cazcade.liquid.api.lsd.LSDPropertyStore;
import org.neo4j.graphdb.Node;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author neilellis@cazcade.com
 */
public class NeoPropertyStore implements LSDPropertyStore {
    private final Node neoNode;

    public NeoPropertyStore(Node neoNode) {
        this.neoNode = neoNode;
    }

    @Override
    public void put(String property, String value) {
        neoNode.setProperty(property, value);
    }

    @Override
    public String get(String property) {
        return String.valueOf(neoNode.getProperty(property));
    }

    @Override
    public Iterable<? extends String> getProperties() {
        return neoNode.getPropertyKeys();
    }

    @Override
    public void remove(String property) {
        neoNode.removeProperty(property);
    }

    @Nonnull
    @Override
    public Map<String, String> asMap() {
        throw new UnsupportedOperationException("Cannot convert Neo nodes to maps.");
    }

    @Override
    public boolean containsProperty(String property) {
        return neoNode.hasProperty(property);
    }

    @Nonnull
    @Override
    public Iterable<? extends String> valueIterator() {
        throw new UnsupportedOperationException("Cannot iterate values.");
    }

    @Nonnull
    @Override
    public LSDPropertyStore copy() {
        return new NeoPropertyStore(neoNode);
    }
}
