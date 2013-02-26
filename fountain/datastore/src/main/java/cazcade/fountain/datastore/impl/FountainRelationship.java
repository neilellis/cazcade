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

    @Nonnull PersistedEntity end();

    @Nonnull PersistedEntity other(@Nonnull PersistedEntity persistedEntity);

    Object $(String key);

    @Nonnull PersistedEntity start();

    FountainRelationships type();

    boolean has(String key);

    void $(String key, Object value);
}
