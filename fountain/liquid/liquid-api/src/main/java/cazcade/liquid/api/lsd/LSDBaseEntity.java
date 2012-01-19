package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LiquidPermission;
import cazcade.liquid.api.LiquidPermissionScope;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * The interface which all entities must extend.
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
    void addAnonymousSubEntity(LSDAttribute stem, LSDBaseEntity entity);

    <T extends LSDBaseEntity> void addSubEntities(LSDAttribute stem, Collection<T> entity);

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
    void addSubEntity(LSDAttribute stem, LSDBaseEntity entity, boolean requiresId);

    /**
     * Used for free text searching.
     *
     * @return the entity as a single piece of text.
     */
    String asFreeText();

    boolean attributeIs(LSDAttribute attribute, String comparison);

    boolean canBe(LSDDictionaryTypes type);

    void copyAttribute(LSDBaseEntity entity, LSDAttribute attribute);

    Object get(String key);

    String getAttribute(LSDAttribute attribute, String defaultValue);

    String getAttribute(LSDAttribute attribute);

    @Nonnull
    List<String> getAttributeAsList(LSDAttribute attribute);

    @Nullable
    LiquidURI getAttributeAsURI(LSDAttribute attribute);

    boolean getBooleanAttribute(LSDAttribute attribute, boolean defaultValue);

    boolean getBooleanAttribute(LSDAttribute editable);

    Double getDoubleAttribute(LSDAttribute attribute);

    int getIntegerAttribute(LSDAttribute attribute, int defaultValue);

    Integer getIntegerAttribute(LSDAttribute attribute);

    Long getLongAttribute(LSDAttribute attribute);

    Map<String, String> getMap();

    @Nullable
    Date getPublished();

    /**
     * This returns a value without any validation.
     *
     * @param key
     * @return
     */
    String getRawValue(LSDAttribute key);

    String getSubAttribute(LSDAttribute attribute, LSDAttribute subAttribute, String defaultValue);

    /**
     * Extracts a list of objects
     *
     * @param key
     * @return
     */
    @Nonnull
    <T extends LSDBaseEntity> List<T> getSubEntities(LSDAttribute key);

    /**
     * Returns a un-aliased sub object from all properties with the common parent path.
     *
     * @param path     all properties that start with this will be used.
     * @param readonly the newly created sub entity should be readonly.
     * @return a new object from the sub properties.
     */
    @Nonnull
    <T extends LSDBaseEntity> T getSubEntity(LSDAttribute path, boolean readonly);

    /**
     * LSD Types are describe how an object can be represented and interacted with by clients.
     *
     * @return
     */
    LSDTypeDef getTypeDef();

    @Nullable
    LiquidURI getURI();

    @Nullable
    LiquidURI getURIAttribute(LSDAttribute attribute);

    /**
     * All LSD Objects have an id attribute which confirms to Java's {@link java.util.UUID} format, but for
     * GWT related reasons we use instead {@link cazcade.liquid.api.LiquidUUID} class.
     *
     * @return a universally unique identifier for this object
     */
    LiquidUUID getUUID();

    @Nullable
    LiquidUUID getUUIDAttribute(LSDAttribute attribute);

    @Nullable
    Date getUpdated();

    String getValue(String key);

    boolean hasAttribute(LSDAttribute key);

    boolean hasPermission(LiquidPermissionScope permissionScope, LiquidPermission permission);

    boolean hasSubEntity(LSDAttribute attribute);

    boolean isA(LSDDictionaryTypes type);

    boolean isA(LSDTypeDef typeDef);

    boolean isEmptyValue(LSDAttribute key);

    boolean isError();

    boolean isNewerThan(LSDBaseEntity entity);

    boolean isReadonly();

    boolean isSerializable();

    void remove(LSDAttribute id);

    void removeCompletely(LSDAttribute attribute);

    @Nonnull
    <T extends LSDBaseEntity> T removeSubEntity(LSDAttribute path);

    void removeValue(LSDAttribute id);

    void set(String key, String value);

    void setAttribute(LSDAttribute parent, LSDAttribute child, String value);

    /**
     * Set's an attribute value, the empty string "" will remove the property.
     * null is invalid, for potentially null values use
     * {@link #setAttributeConditonally(LSDAttribute, String)} instead.
     *
     * @param key   the attribute key.
     * @param value the value.
     */
    void setAttribute(LSDAttribute key, String value);

    void setAttribute(LSDAttribute checked, boolean bool);

    void setAttribute(LSDAttribute attribute, long value);

    void setAttribute(LSDAttribute attribute, LiquidUUID uuid);

    void setAttribute(LSDAttribute attribute, LiquidURI uri);

    void setAttribute(LSDAttribute attribute, double value);

    void setAttribute(LSDAttribute attribute, Date value);

    void setAttributeConditonally(LSDAttribute key, String value);

    void setID(@Nonnull LiquidUUID id);

    void setId(String id);

    void setPublished(Date published);

    void setReadonly(boolean readonly);

    void setType(@Nonnull LSDDictionaryTypes type);

    void setTypeDef(LSDTypeDef typeDef);

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
    void setValue(String key, String value);

    void setValues(LSDAttribute key, List values);

    void timestamp();

    boolean wasPublishedAfter(LSDBaseEntity entity);

    class EntityUpdatedComparator implements Comparator<LSDBaseEntity> {
        @Override
        public int compare(@Nonnull final LSDBaseEntity entity, @Nonnull final LSDBaseEntity entity1) {
            return entity.getUpdated().compareTo(entity1.getUpdated());
        }
    }

    class EntityPublishedComparator implements Comparator<LSDBaseEntity> {
        @Override
        public int compare(@Nonnull final LSDBaseEntity entity, @Nonnull final LSDBaseEntity entity1) {
            return entity.getPublished().compareTo(entity1.getPublished());
        }
    }
}
