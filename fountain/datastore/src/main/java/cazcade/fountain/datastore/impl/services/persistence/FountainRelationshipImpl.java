package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.fountain.datastore.impl.FountainEntity;
import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import org.neo4j.graphdb.GraphDatabaseService;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author neilellis@cazcade.com
 */
public class FountainRelationshipImpl implements FountainRelationship {

    @Nonnull
    private final org.neo4j.graphdb.Relationship relationship;

    public FountainRelationshipImpl(@Nonnull final org.neo4j.graphdb.Relationship relationship) {
        if (relationship == null) {
            throw new NullPointerException("Tried to create a FountainRelationship from a null neo relationship.");
        }
        this.relationship = relationship;
    }


    public long getId() {
        return relationship.getId();
    }


    @Override
    public void delete() {
        relationship.delete();
    }


    @Override
    @Nonnull
    public FountainEntity getStartNode() {
        return new FountainEntityImpl(relationship.getStartNode());
    }


    @Override
    @Nonnull
    public FountainEntity getEndNode() {
        return new FountainEntityImpl(relationship.getEndNode());
    }


    @Override
    @Nonnull
    public FountainEntity getOtherNode(@Nonnull final FountainEntity fountainEntity) {
        return new FountainEntityImpl(relationship.getOtherNode(fountainEntity.getNeoNode()));
    }


    public FountainEntity[] getNodes() {
        final List<FountainEntity> fountainFountainEntities = new ArrayList<FountainEntity>();
        final org.neo4j.graphdb.Node[] nodes = relationship.getNodes();
        for (final org.neo4j.graphdb.Node node : nodes) {
            fountainFountainEntities.add(new FountainEntityImpl(node));
        }
        return fountainFountainEntities.toArray(new FountainEntity[fountainFountainEntities.size()]);
    }


    @Override
    public FountainRelationships getType() {
        return FountainRelationships.valueOf(relationship.getType().name());
    }


    public boolean isType(final FountainRelationships type) {
        return relationship.isType(type);
    }


    public GraphDatabaseService getGraphDatabase() {
        return relationship.getGraphDatabase();
    }


    @Override
    public boolean hasProperty(@Nonnull final String key) {
        return relationship.hasProperty(key);
    }


    @Override
    public Object getProperty(@Nonnull final String key) {
        return relationship.getProperty(key);
    }


    public Object getProperty(@Nonnull final String key, @Nonnull final Object defaultValue) {
        return relationship.getProperty(key, defaultValue);
    }


    @Override
    public void setProperty(@Nonnull final String key, @Nonnull final Object value) {
        relationship.setProperty(key, value);
    }


    public Object removeProperty(@Nonnull final String key) {
        return relationship.removeProperty(key);
    }


    public Iterable<String> getPropertyKeys() {
        return relationship.getPropertyKeys();
    }


}
