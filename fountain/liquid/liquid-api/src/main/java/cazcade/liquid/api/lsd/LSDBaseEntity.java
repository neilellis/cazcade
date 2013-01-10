/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.LiquidPermissionScope;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * The interface which all entities must extend. LSD entities are key/value stores with a
 * hierarchical key format using . notation. They can be represented easily in XML and JSON formats
 * as hierarchical data structures or as flat key/value maps.
 *
 * @author neilellis@cazcade.com
 */
public interface LSDBaseEntity {
    /**
     * Anonymous sub entities have no ID or TYPE, they are just a collection of attributes really.
     *
     * @param stem   the stem property to which the sub entities properties
     *               are appended to.
     * @param entity the collection of attributes to add.
     */
    void addAnonymousSubEntity(@Nonnull LSDAttribute stem, @Nonnull LSDBaseEntity entity);

    <T extends LSDBaseEntity> void addSubEntities(@Nonnull LSDAttribute stem, @Nonnull Collection<T> entity);

    /**
     * You can only add one object at a given root. If you
     * want to add an array of objects at a root use the
     * {@link #addSubEntities(LSDAttribute, Collection < LSDTransferEntity >)}
     * method.
     *
     * @param stem       the stem property to which the sub entities properties
     *                   are appended to.
     * @param requiresId
     */
    void addSubEntity(@Nonnull LSDAttribute stem, @Nonnull LSDBaseEntity entity, boolean requiresId);

    /**
     * Used for free text searching.
     *
     * @return the entity as a single piece of text.
     */
    @Nonnull String asFreeText();

    @Nonnull String asDebugText();


    boolean attributeIs(@Nonnull LSDAttribute attribute, @Nonnull String comparison);

    boolean canBe(@Nonnull LSDDictionaryTypes type);

    void copyAttribute(@Nonnull LSDBaseEntity entity, @Nonnull LSDAttribute attribute);

    @Nullable Object get(@Nonnull String key);

    @Nonnull String getAttribute(@Nonnull LSDAttribute attribute, @Nonnull String defaultValue);

    @Nonnull String getAttribute(@Nonnull LSDAttribute attribute);

    @Nonnull List<String> getAttributeAsList(@Nonnull LSDAttribute attribute);

    @Nonnull LiquidURI getAttributeAsURI(@Nonnull LSDAttribute attribute);

    boolean getBooleanAttribute(@Nonnull LSDAttribute attribute, boolean defaultValue);

    boolean getBooleanAttribute(@Nonnull LSDAttribute editable);

    @Nonnull Double getDoubleAttribute(@Nonnull LSDAttribute attribute) throws NumberFormatException;

    int getIntegerAttribute(@Nonnull LSDAttribute attribute, int defaultValue) throws NumberFormatException;

    @Nonnull Integer getIntegerAttribute(@Nonnull LSDAttribute attribute) throws NumberFormatException;

    @Nonnull Long getLongAttribute(@Nonnull LSDAttribute attribute) throws NumberFormatException;

    @Nonnull Map<String, String> getMap();

    @Nonnull Date getPublished();

    /**
     * This returns a value without any validation.
     *
     * @param key
     * @return
     */
    String getRawValue(@Nonnull LSDAttribute key);

    @Nullable String getSubAttribute(@Nonnull LSDAttribute attribute, @Nonnull LSDAttribute subAttribute, String defaultValue);

    /**
     * Extracts a list of objects
     *
     * @param key
     * @return
     */
    @Nonnull <T extends LSDBaseEntity> List<T> getSubEntities(@Nonnull LSDAttribute key);

    /**
     * Returns a un-aliased sub object from all properties with the common parent path.
     *
     * @param path     all properties that start with this will be used.
     * @param readonly the newly created sub entity should be readonly.
     * @return a new object from the sub properties.
     */
    @Nonnull <T extends LSDBaseEntity> T getSubEntity(@Nonnull LSDAttribute path, boolean readonly);

    /**
     * LSD Types are describe how an object can be represented and interacted with by clients.
     *
     * @return
     */
    @Nonnull LSDTypeDef getTypeDef();

    @Nonnull LiquidURI getURI();

    boolean hasURI();


    @Nonnull LiquidURI getURIAttribute(@Nonnull LSDAttribute attribute);

    /**
     * All LSD Objects have an id attribute which confirms to Java's {@link java.util.UUID} format, but for
     * GWT related reasons we use instead {@link cazcade.liquid.api.LiquidUUID} class.
     *
     * @return a universally unique identifier for this object
     */
    @Nonnull LiquidUUID getUUID();

    @Nonnull LiquidUUID getUUIDAttribute(@Nonnull LSDAttribute attribute);

    @Nonnull Date getUpdated();

    @Nullable String getValue(@Nonnull String key);

    boolean hasAttribute(@Nonnull LSDAttribute key);

    boolean hasPermission(@Nonnull LiquidPermissionScope permissionScope, @Nonnull LiquidPermission permission);

    boolean hasSubEntity(@Nonnull LSDAttribute attribute);

    boolean isA(@Nonnull LSDDictionaryTypes type);

    boolean isA(@Nonnull LSDTypeDef typeDef);

    boolean isEmptyValue(@Nonnull LSDAttribute key);

    boolean isError();

    boolean isNewerThan(LSDBaseEntity entity);

    boolean isReadonly();

    boolean isSerializable();

    void remove(@Nonnull LSDAttribute id);

    void removeCompletely(@Nonnull LSDAttribute attribute);

    @Nonnull <T extends LSDBaseEntity> T removeSubEntity(@Nonnull LSDAttribute path);

    void removeValue(@Nonnull LSDAttribute id);

    void set(@Nonnull String key, String value);

    void setAttribute(@Nonnull LSDAttribute parent, @Nonnull LSDAttribute child, String value);

    /**
     * Set's an attribute value, the empty string "" will remove the property.
     * null is invalid, for potentially null values use
     * {@link #setAttributeConditonally(LSDAttribute, String)} instead.
     *
     * @param key   the attribute key.
     * @param value the value.
     */
    void setAttribute(@Nonnull LSDAttribute key, String value);

    void setAttribute(@Nonnull LSDAttribute checked, boolean bool);

    void setAttribute(@Nonnull LSDAttribute attribute, long value);

    void setAttribute(@Nonnull LSDAttribute attribute, LiquidUUID uuid);

    void setAttribute(@Nonnull LSDAttribute attribute, LiquidURI uri);

    void setAttribute(@Nonnull LSDAttribute attribute, double value);

    void setAttribute(@Nonnull LSDAttribute attribute, Date value);

    void setAttributeConditonally(@Nonnull LSDAttribute key, String value);

    void setID(@Nonnull LiquidUUID id);

    void setId(String id);

    void setPublished(Date published);

    void setReadonly(boolean readonly);

    void setURI(LiquidURI uri);

    void setUpdated(Date updated);

    /**
     * <b>Avoid using this method, it is primarily for internal use.</b>
     * It set's an attribute value, the empty string "" will remove the property.
     * null is invalid, for potentially null values
     * use {@link #setAttributeConditonally(LSDAttribute, String)} instead.
     *
     * @param key   the attribute key.
     * @param value the value.
     */
    void setValue(@Nonnull String key, String value);

    void setValues(@Nonnull LSDAttribute key, List values);

    void timestamp();

    boolean wasPublishedAfter(@Nonnull LSDBaseEntity entity);

    boolean hasUpdated();

    boolean hasId();

    class EntityUpdatedComparator implements Comparator<LSDBaseEntity> {
        @Override
        public int compare(@Nonnull final LSDBaseEntity entity, @Nonnull final LSDBaseEntity entity1) {
            final Date updated = entity.getUpdated();
            return updated.compareTo(entity1.getUpdated());
        }
    }

    class EntityPublishedComparator implements Comparator<LSDBaseEntity> {
        @Override
        public int compare(@Nonnull final LSDBaseEntity entity, @Nonnull final LSDBaseEntity entity1) {
            final Date published = entity.getPublished();
            return published.compareTo(entity1.getPublished());
        }
    }
}
