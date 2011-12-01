package cazcade.fountain.datastore.impl;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public interface FountainRelationship {


    void delete();

    @Nonnull
    FountainEntity getStartNode();

    @Nonnull
    FountainEntity getEndNode();

    @Nonnull
    FountainEntity getOtherNode(@Nonnull FountainEntity fountainEntity);

    FountainRelationships getType();

    boolean hasProperty(String key);

    Object getProperty(String key);

    void setProperty(String key, Object value);
}
