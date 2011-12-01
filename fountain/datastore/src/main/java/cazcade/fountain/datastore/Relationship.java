package cazcade.fountain.datastore;

import cazcade.fountain.datastore.impl.FountainRelationships;
import org.neo4j.graphdb.GraphDatabaseService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class Relationship {

    @Nullable
    private final org.neo4j.graphdb.Relationship relationship;

    public Relationship(@Nullable org.neo4j.graphdb.Relationship relationship) {
        if (relationship == null) {
            throw new NullPointerException("Tried to create a FountainRelationship from a null Neo Relationship.");
        }
        this.relationship = relationship;
    }


    public long getId() {
        return relationship.getId();
    }


    public void delete() {
        relationship.delete();
    }


    @Nonnull
    public Node getStartNode() {
        return new Node(relationship.getStartNode());
    }


    @Nonnull
    public Node getEndNode() {
        return new Node(relationship.getEndNode());
    }


    @Nonnull
    public Node getOtherNode(@Nonnull Node node) {
        return new Node(relationship.getOtherNode(node.getNeoNode()));
    }


    public Node[] getNodes() {
        List<Node> fountainNodes = new ArrayList<Node>();
        final org.neo4j.graphdb.Node[] nodes = relationship.getNodes();
        for (org.neo4j.graphdb.Node node : nodes) {
            fountainNodes.add(new Node(node));
        }
        return fountainNodes.toArray(new Node[fountainNodes.size()]);
    }


    public FountainRelationships getType() {
        return FountainRelationships.valueOf(relationship.getType().name());
    }


    public boolean isType(FountainRelationships type) {
        return relationship.isType(type);
    }


    public GraphDatabaseService getGraphDatabase() {
        return relationship.getGraphDatabase();
    }


    public boolean hasProperty(String key) {
        return relationship.hasProperty(key);
    }


    public Object getProperty(String key) {
        return relationship.getProperty(key);
    }


    public Object getProperty(String key, Object defaultValue) {
        return relationship.getProperty(key, defaultValue);
    }


    public void setProperty(String key, Object value) {
        relationship.setProperty(key, value);
    }


    public Object removeProperty(String key) {
        return relationship.removeProperty(key);
    }


    public Iterable<String> getPropertyKeys() {
        return relationship.getPropertyKeys();
    }


}
