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
     * All LSD Objects have an id attribute which confirms to Java's {@link java.util.UUID} format, but for
     * GWT related reasons we use instead {@link cazcade.liquid.api.LiquidUUID} class.
     *
     * @return a universally unique identifier for this object
     */
    LiquidUUID getUUID();

    /**
     * Returns a un-aliased sub object from all properties with the common parent path.
     *
     * @param path     all properties that start with this will be used.
     * @param readonly the newly created sub entity should be readonly.
     * @return a new object from the sub properties.
     */
    @Nonnull
    <T extends LSDBaseEntity> T getSubEntity(LSDAttribute path, boolean readonly);

    @Nonnull
    <T extends LSDBaseEntity> T removeSubEntity(LSDAttribute path);

    @Nullable
    LiquidURI getURI();

    /**
     * LSD Types are describe how an object can be represented and interacted with by clients.
     *
     * @return
     */
    LSDTypeDef getTypeDef();

    /**
     * Extracts a list of objects
     *
     * @param key
     * @return
     */
    @Nonnull
    <T extends LSDBaseEntity> List<T> getSubEntities(LSDAttribute key);

    String getAttribute(LSDAttribute attribute);

    @Nullable
    LiquidURI getAttributeAsURI(LSDAttribute attribute);

    /**
     * This returns a value without any validation.
     *
     * @param key
     * @return
     */
    String getRawValue(LSDAttribute key);

    boolean isError();

    boolean isEmptyValue(LSDAttribute key);

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

    String getValue(String key);

    /**
     * Set's an attribute value, the empty string "" will remove the property.
     * null is invalid, for potentially null values use
     * {@link #setAttributeConditonally(LSDAttribute, String)} instead.
     *
     * @param key   the attribute key.
     * @param value the value.
     */
    void setAttribute(LSDAttribute key, String value);

    boolean hasAttribute(LSDAttribute key);

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

    void addSubEntities(LSDAttribute stem, Collection<LSDBaseEntity> entity);

    /**
     * Anonymous sub entities have no ID or TYPE, they are just a collection of attributes really.
     *
     * @param stem   the stem property to which the sub entities properties
     *               are appended to.
     * @param entity the collection of attributes to add.
     */
    void addAnonymousSubEntity(LSDAttribute stem, LSDBaseEntity entity);

    boolean canBe(LSDDictionaryTypes type);

    boolean isA(LSDDictionaryTypes type);

    boolean isA(LSDTypeDef typeDef);

    void setValues(LSDAttribute key, List values);

    void setAttributeConditonally(LSDAttribute key, String value);

    boolean attributeIs(LSDAttribute attribute, String comparison);

    @Nonnull
    List<String> getAttributeAsList(LSDAttribute attribute);

    @Nullable
    Date getUpdated();

    @Nullable
    Date getPublished();

    void timestamp();

    void setTypeDef(LSDTypeDef typeDef);

    /**
     * Used for free text searching.
     *
     * @return the entity as a single piece of text.
     */
    String asFreeText();

    boolean isNewerThan(LSDBaseEntity entity);

    boolean wasPublishedAfter(LSDBaseEntity entity);

    void remove(LSDAttribute id);

    void removeValue(LSDAttribute id);

    boolean getBooleanAttribute(LSDAttribute editable);

    void setAttribute(LSDAttribute checked, boolean bool);

    boolean hasPermission(LiquidPermissionScope permissionScope, LiquidPermission permission);

    Object get(String key);

    void set(String key, String value);

    String getAttribute(LSDAttribute attribute, String defaultValue);

    void setId(String id);

    void setURI(LiquidURI uri);

    Long getLongAttribute(LSDAttribute attribute);

    void setAttribute(LSDAttribute attribute, long value);

    Integer getIntegerAttribute(LSDAttribute attribute);

    @Nullable
    LiquidUUID getUUIDAttribute(LSDAttribute attribute);

    void setAttribute(LSDAttribute attribute, LiquidUUID uuid);

    @Nullable
    LiquidURI getURIAttribute(LSDAttribute attribute);

    void setAttribute(LSDAttribute attribute, LiquidURI uri);

    Double getDoubleAttribute(LSDAttribute attribute);

    void setAttribute(LSDAttribute attribute, double value);

    int getIntegerAttribute(LSDAttribute attribute, int defaultValue);

    boolean hasSubEntity(LSDAttribute attribute);

    boolean getBooleanAttribute(LSDAttribute attribute, boolean defaultValue);

    void removeCompletely(LSDAttribute attribute);

    void setReadonly(boolean readonly);

    void setAttribute(LSDAttribute parent, LSDAttribute child, String value);

    boolean isReadonly();

    String getSubAttribute(LSDAttribute attribute, LSDAttribute subAttribute, String defaultValue);

    void setUpdated(Date updated);

    void setAttribute(LSDAttribute attribute, Date value);

    void setPublished(Date published);

    void copyAttribute(LSDBaseEntity entity, LSDAttribute attribute);

    void setType(@Nonnull LSDDictionaryTypes type);

    void setID(@Nonnull LiquidUUID id);

    boolean isSerializable();

    Map<String, String> getMap();

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
