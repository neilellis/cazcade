package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.FountainRelationships;
import cazcade.fountain.datastore.impl.LSDPersistedEntity;
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
    public LSDPersistedEntity getStartNode() {
        return new FountainEntityImpl(relationship.getStartNode());
    }


    @Override
    @Nonnull
    public LSDPersistedEntity getEndNode() {
        return new FountainEntityImpl(relationship.getEndNode());
    }


    @Override
    @Nonnull
    public LSDPersistedEntity getOtherNode(@Nonnull final LSDPersistedEntity persistedEntity) {
        return new FountainEntityImpl(relationship.getOtherNode(persistedEntity.getNeoNode()));
    }


    public LSDPersistedEntity[] getNodes() {
        final List<LSDPersistedEntity> fountainPersistedEntities = new ArrayList<LSDPersistedEntity>();
        final org.neo4j.graphdb.Node[] nodes = relationship.getNodes();
        for (final org.neo4j.graphdb.Node node : nodes) {
            fountainPersistedEntities.add(new FountainEntityImpl(node));
        }
        return fountainPersistedEntities.toArray(new LSDPersistedEntity[fountainPersistedEntities.size()]);
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
