package cazcade.fountain.datastore.impl.io;

import cazcade.fountain.datastore.impl.FountainNeo;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.liquid.api.lsd.LSDAttribute;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.BatchInserterIndex;
import org.neo4j.graphdb.index.BatchInserterIndexProvider;
import org.neo4j.graphdb.index.Index;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.impl.lucene.LuceneBatchInserterIndexProvider;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.impl.batchinsert.BatchInserter;
import org.neo4j.kernel.impl.batchinsert.BatchInserterImpl;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author neilellis@cazcade.com
 */
public class FountainNeoImporter {
    public static final String ID = LSDAttribute.ID.getKeyName();
    public static final String URI = LSDAttribute.URI.getKeyName();

    @Nonnull
    private final BatchInserter batchInserter;

    public FountainNeoImporter(String dir) {
        this.batchInserter = new BatchInserterImpl(dir);
    }


    public void importJson(String dir) throws IOException {
        BatchInserterIndexProvider indexProvider = new LuceneBatchInserterIndexProvider(batchInserter);
        BatchInserterIndex uuidIndex = indexProvider.nodeIndex(FountainNeo.NODE_INDEX_NAME, MapUtil.stringMap("type", "exact"));
        JsonFactory f = new JsonFactory();
        JsonParser jp = f.createJsonParser(new File(dir, "nodes.json"));
        while (jp.nextToken() != null) {
            HashMap<String, Object> properties = new HashMap<String, Object>();
            String id = "";
            while (jp.nextToken() != JsonToken.END_OBJECT) {

                String fieldname = jp.getCurrentName();
                jp.nextToken();
                String fieldValue = jp.getText();
                properties.put(fieldname, fieldValue);
                if (fieldname.equals(ID)) {
                    id = fieldValue;
                }
            }
            long node = batchInserter.createNode(properties);
            uuidIndex.add(node, MapUtil.map(ID, id));
            System.out.println("Created node " + properties);
        }
        jp.close();

        uuidIndex.flush();

        jp = f.createJsonParser(new File(dir, "rels.json"));
        jp.nextToken();
        while (jp.nextToken() != null) {
            String fieldname = jp.getCurrentName();
            if (!fieldname.equals("id")) {
                throw new RuntimeException("Expected id attribute.");
            }
            jp.nextToken();
            long startNode = uuidIndex.get(ID, jp.getText()).getSingle();
            long endNode = 0;
            jp.nextToken();
            String relsName = jp.getCurrentName();
            if (!relsName.equals("rels")) {
                throw new RuntimeException("Expected rels attribute, got " + relsName);
            }
            jp.nextToken();
            while (jp.nextToken() != JsonToken.END_ARRAY) {
                jp.nextToken();

                String typeName = jp.getCurrentName();
                jp.nextToken();
                if (!typeName.equals("t")) {
                    throw new RuntimeException("Expected t attribute, got " + typeName);
                }
                String relType = jp.getText();
                jp.nextToken();
                String destinationName = jp.getCurrentName();
                jp.nextToken();
                if (!destinationName.equals("d")) {
                    throw new RuntimeException("Expected d attribute.");
                }
                endNode = uuidIndex.get(ID, jp.getText()).getSingle();
                jp.nextToken();

                String propertiesFieldName = jp.getCurrentName();
                jp.nextToken();
                if (!propertiesFieldName.equals("p")) {
                    throw new RuntimeException("Expected p attribute.");
                }
                HashMap<String, Object> properties = new HashMap<String, Object>();

                while (jp.nextToken() != JsonToken.END_OBJECT) {

                    String propertyName = jp.getCurrentName();
                    jp.nextToken();
                    properties.put(propertyName, jp.getText());

                }
                batchInserter.createRelationship(startNode, endNode, FountainRelationships.valueOf(relType), properties);
                System.out.println("Created relationship from " + startNode + " to " + endNode + " of type " + relType + " with " + properties);
                jp.nextToken();

            }
            jp.nextToken();
            jp.nextToken();
        }
        jp.close();
        indexProvider.shutdown();
        batchInserter.shutdown();
        System.out.println("Adding URI index.");
        EmbeddedGraphDatabase embeddedGraphDatabase = new EmbeddedGraphDatabase(batchInserter.getStore());
        Transaction transaction = embeddedGraphDatabase.beginTx();
        Index<Node> uriIndex = embeddedGraphDatabase.index().forNodes(FountainNeo.NODE_INDEX_NAME, MapUtil.stringMap("type", "exact"));
        Iterable<Node> allNodes = embeddedGraphDatabase.getAllNodes();
        for (Node node : allNodes) {
            if (node.hasProperty(URI) && !node.hasRelationship(FountainRelationships.VERSION_PARENT, Direction.INCOMING)) {
                uriIndex.add(node, URI, node.getProperty(URI));
            }
        }
        transaction.success();
        transaction.finish();
        embeddedGraphDatabase.shutdown();
        System.out.println("Finished.");
    }


}
