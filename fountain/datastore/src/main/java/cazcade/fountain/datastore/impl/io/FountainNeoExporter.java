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

    public FountainNeoExporter(GraphDatabaseService graphDatabase) {
        this.graphDatabase = graphDatabase;
    }


    public void export(String dir) throws IOException {
        JsonFactory f = new JsonFactory();
        JsonGenerator g = f.createJsonGenerator(new File(dir, "nodes.json"), JsonEncoding.UTF8);
        Iterable<Node> allNodes = graphDatabase.getAllNodes();
        for (Node node : allNodes) {
            g.writeStartObject();
            writeProperties(g, node);
            g.writeEndObject();
        }
        g.close();
        JsonGenerator relGen = f.createJsonGenerator(new File(dir, "rels.json"), JsonEncoding.UTF8);
        allNodes = graphDatabase.getAllNodes();
        for (Node node : allNodes) {
            if (node.hasProperty(ID)) {
                relGen.writeStartObject();
                relGen.writeStringField("id", node.getProperty(ID).toString());
                relGen.writeArrayFieldStart("rels");
                Iterable<Relationship> relationships = node.getRelationships(Direction.OUTGOING);
                for (Relationship relationship : relationships) {
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

    private void writeProperties(@Nonnull JsonGenerator g, @Nonnull Node node) throws IOException {
        Iterable<String> propertyKeys = node.getPropertyKeys();
        for (String propertyKey : propertyKeys) {
            g.writeStringField(propertyKey, node.getProperty(propertyKey).toString());
        }
    }

    private void writeProperties(@Nonnull JsonGenerator g, @Nonnull Relationship rel) throws IOException {
        Iterable<String> propertyKeys = rel.getPropertyKeys();
        for (String propertyKey : propertyKeys) {
            g.writeStringField(propertyKey, rel.getProperty(propertyKey).toString());
        }
    }


}
