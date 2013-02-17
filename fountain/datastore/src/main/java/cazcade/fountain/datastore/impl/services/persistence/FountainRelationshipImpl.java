/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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
    @Nonnull private final org.neo4j.graphdb.Relationship relationship;

    public FountainRelationshipImpl(@Nonnull final org.neo4j.graphdb.Relationship relationship) {
        //noinspection ConstantConditions
        if (relationship == null) {
            throw new NullPointerException("Tried to create a FountainRelationship from a null neo relationship.");
        }
        this.relationship = relationship;
    }

    @Override
    public void delete() {
        assert relationship != null;
        relationship.delete();
    }

    @Override @Nonnull
    public LSDPersistedEntity getEndNode() {
        return new FountainEntityImpl(relationship.getEndNode());
    }

    public GraphDatabaseService getGraphDatabase() {
        assert relationship != null;
        return relationship.getGraphDatabase();
    }

    public long getId() {
        assert relationship != null;
        return relationship.getId();
    }

    public LSDPersistedEntity[] getNodes() {
        final List<LSDPersistedEntity> fountainPersistedEntities = new ArrayList<LSDPersistedEntity>();
        assert relationship != null;
        final org.neo4j.graphdb.Node[] nodes = relationship.getNodes();
        for (final org.neo4j.graphdb.Node node : nodes) {
            fountainPersistedEntities.add(new FountainEntityImpl(node));
        }
        return fountainPersistedEntities.toArray(new LSDPersistedEntity[fountainPersistedEntities.size()]);
    }

    @Override @Nonnull
    public LSDPersistedEntity getOtherNode(@Nonnull final LSDPersistedEntity persistedEntity) {
        assert relationship != null;
        return new FountainEntityImpl(relationship.getOtherNode(persistedEntity.getNeoNode()));
    }

    public Object getProperty(@Nonnull final String key, @Nonnull final Object defaultValue) {
        assert relationship != null;
        return relationship.getProperty(key, defaultValue);
    }

    @Override
    public Object getProperty(@Nonnull final String key) {
        assert relationship != null;
        return relationship.getProperty(key);
    }

    public Iterable<String> getPropertyKeys() {
        assert relationship != null;
        return relationship.getPropertyKeys();
    }

    @Override @Nonnull
    public LSDPersistedEntity getStartNode() {
        assert relationship != null;
        return new FountainEntityImpl(relationship.getStartNode());
    }

    @Override
    public FountainRelationships getType() {
        assert relationship != null;
        return FountainRelationships.valueOf(relationship.getType().name());
    }

    @Override
    public boolean hasProperty(@Nonnull final String key) {
        assert relationship != null;
        return relationship.hasProperty(key);
    }

    public boolean isType(final FountainRelationships type) {
        assert relationship != null;
        return relationship.isType(type);
    }

    public Object removeProperty(@Nonnull final String key) {
        assert relationship != null;
        return relationship.removeProperty(key);
    }

    @Override
    public void setProperty(@Nonnull final String key, @Nonnull final Object value) {
        assert relationship != null;
        relationship.setProperty(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        FountainRelationshipImpl that = (FountainRelationshipImpl) o;

        if (relationship.getId() != that.relationship.getId()) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (relationship.getId() % Integer.MAX_VALUE);
    }

    @Override public String toString() {
        return relationship.toString();
    }
}

