package cazcade.fountain.datastore.impl;

import javax.annotation.Nonnull;

/**
 * Currently this class just maps to a Neo Relationship but it will differ soon enough.
 *
 * @author neilellis@cazcade.com
 */
public interface FountainRelationship {


    void delete();

    @Nonnull
    LSDPersistedEntity getStartNode();

    @Nonnull
    LSDPersistedEntity getEndNode();

    @Nonnull
    LSDPersistedEntity getOtherNode(@Nonnull LSDPersistedEntity persistedEntity);

    FountainRelationships getType();

    boolean hasProperty(String key);

    Object getProperty(String key);

    void setProperty(String key, Object value);
}
