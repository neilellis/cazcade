/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl.services.persistence;

import cazcade.fountain.datastore.impl.FountainRelationship;
import cazcade.fountain.datastore.impl.PersistedEntity;
import cazcade.liquid.api.lsd.ObjectCollection;
import com.google.common.collect.FluentIterable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="http://uk.linkedin.com/in/neilellis">Neil Ellis</a>
 * @todo document.
 */
public class FountainRelationshipCollection extends FluentIterable<FountainRelationship> {


    Iterable<FountainRelationship> relationships;

    public FountainRelationshipCollection(Iterable<FountainRelationship> relationships) {
        this.relationships = relationships;
    }

    public void delete() {
        for (final FountainRelationship relationship : relationships) {
            relationship.delete();
        }
    }

    @Nonnull public FountainEntityCollection end() {
        List<PersistedEntity> result = new ArrayList<PersistedEntity>();
        for (FountainRelationship relationship : relationships) {
            result.add(relationship.end());
        }
        return new FountainEntityCollection(result);
    }

    @Nonnull public FountainEntityCollection other(@Nonnull PersistedEntity persistedEntity) {
        List<PersistedEntity> result = new ArrayList<PersistedEntity>();
        for (FountainRelationship relationship : relationships) {
            result.add(relationship.other(persistedEntity));
        }
        return new FountainEntityCollection(result);
    }

    public ObjectCollection<Object> $(String key) {
        List<Object> result = new ArrayList<Object>();
        for (FountainRelationship relationship : relationships) {
            result.add(relationship.end());
        }
        return new ObjectCollection<Object>(result);
    }

    @Nonnull public FountainEntityCollection start() {
        List<PersistedEntity> result = new ArrayList<PersistedEntity>();
        for (FountainRelationship relationship : relationships) {
            result.add(relationship.end());
        }
        return new FountainEntityCollection(result);
    }


    public void $(String key, Object value) {
        for (FountainRelationship relationship : relationships) {
            relationship.$(key, value);
        }
    }

    @Override public Iterator<FountainRelationship> iterator() {
        return relationships.iterator();
    }
}
