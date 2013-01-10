/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.datastore.impl;

import javax.annotation.Nonnull;

/**
 * Currently this class just maps to a Neo Relationship but it will differ soon enough.
 *
 * @author neilellis@cazcade.com
 */
public interface FountainRelationship {
    void delete();

    @Nonnull LSDPersistedEntity getEndNode();

    @Nonnull LSDPersistedEntity getOtherNode(@Nonnull LSDPersistedEntity persistedEntity);

    Object getProperty(String key);

    @Nonnull LSDPersistedEntity getStartNode();

    FountainRelationships getType();

    boolean hasProperty(String key);

    void setProperty(String key, Object value);
}
