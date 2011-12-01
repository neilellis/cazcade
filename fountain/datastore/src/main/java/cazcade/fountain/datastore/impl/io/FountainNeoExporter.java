package cazcade.fountain.datastore.impl.io;

import cazcade.liquid.api.lsd.LSDAttribute;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;

/**
 * @author neilellis@cazcade.com
 */
public class FountainNeoExporter {
    public static final String ID = LSDAttribute.ID.getKeyName();
    private final GraphDatabaseService graphDatabase;

    public FountainNeoExporter(final GraphDatabaseService graphDatabase) {
        this.graphDatabase = graphDatabase;
    }


    public void export(final String dir) throws IOException {
        final JsonFactory f = new JsonFactory();
        final JsonGenerator g = f.createJsonGenerator(new File(dir, "nodes.json"), JsonEncoding.UTF8);
        Iterable<Node> allNodes = graphDatabase.getAllNodes();
        for (final Node node : allNodes) {
            g.writeStartObject();
            writeProperties(g, node);
            g.writeEndObject();
        }
        g.close();
        final JsonGenerator relGen = f.createJsonGenerator(new File(dir, "rels.json"), JsonEncoding.UTF8);
        allNodes = graphDatabase.getAllNodes();
        for (final Node node : allNodes) {
            if (node.hasProperty(ID)) {
                relGen.writeStartObject();
                relGen.writeStringField("id", node.getProperty(ID).toString());
                relGen.writeArrayFieldStart("rels");
                final Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING);
                for (final Relationship relationship : relationships) {
                    relGen.writeStartObject();
                    relGen.writeStringField("t", relationship.getType().name());
                    relGen.writeStringField("d", relationship.getEndNode().getProperty(ID).toString());
                    relGen.writeObjectFieldStart("p");
                    writeProperties(relGen, relationship);
                    relGen.writeEndObject();
                    relGen.writeEndObject();
                }
                relGen.writeEndArray();
                relGen.writeEndObject();
            }
        }
        relGen.close();

    }

    private void writeProperties(@Nonnull final JsonGenerator g, @Nonnull final Node node) throws IOException {
        final Iterable<String> propertyKeys = node.getPropertyKeys();
        for (final String propertyKey : propertyKeys) {
            g.writeStringField(propertyKey, node.getProperty(propertyKey).toString());
        }
    }

    private void writeProperties(@Nonnull final JsonGenerator g, @Nonnull final Relationship rel) throws IOException {
        final Iterable<String> propertyKeys = rel.getPropertyKeys();
        for (final String propertyKey : propertyKeys) {
            g.writeStringField(propertyKey, rel.getProperty(propertyKey).toString());
        }
    }


}
