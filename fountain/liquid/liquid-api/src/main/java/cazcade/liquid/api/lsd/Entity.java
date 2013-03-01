/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LURI;
import cazcade.liquid.api.LiquidUUID;
import cazcade.liquid.api.Permission;
import cazcade.liquid.api.PermissionScope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;

/**
 * The interface which all entities must extend. LSD entities are key/value stores with a
 * hierarchical key format using . notation. They can be represented easily in XML and JSON formats
 * as hierarchical data structures or as flat key/value maps.
 * <p/>
 * NB: T - is the final type and NEW is the type of newly created entities
 *
 * @author neilellis@cazcade.com
 */
public interface Entity<NEW extends Entity<NEW, T>, T extends Entity<NEW, T>> extends Serializable {
    /**
     * Anonymous sub entities have no ID or TYPE, they are just a collection of attributes really.
     *
     * @param stem   the stem property to which the sub entities properties
     *               are appended to.
     * @param entity the collection of attributes to add.
     */
    void addAnonymousSubEntity(@Nonnull Attribute stem, @Nonnull Entity entity);

    void children(@Nonnull Attribute stem, @Nonnull Collection<? extends NEW> entity);

    void children( @Nonnull Collection<? extends NEW> entity);

    /**
     * You can only add one object at a given root. If you
     * want to add an array of objects at a root use the
     * {@link #children(Attribute, Collection < TransferEntity >)}
     * method.
     *
     * @param stem       the stem property to which the sub entities properties
     *                   are appended to.
     * @param requiresId
     */
    void child(@Nonnull Attribute stem, @Nonnull NEW entity, boolean requiresId);

    /**
     * Used for free text searching.
     *
     * @return the entity as a single piece of text.
     */
    @Nonnull String asFreeText();

    @Nonnull String asDebugText();

    boolean attributeIs(@Nonnull Attribute attribute, @Nonnull String comparison);

    boolean canBe(@Nonnull Types type);

    T $(@Nonnull Entity entity, @Nonnull Attribute attribute);

    @Nullable Object get(@Nonnull String key);

    @Nonnull String default$(@Nonnull Attribute attribute, @Nonnull String defaultValue);

    @Nonnull String $(@Nonnull Attribute attribute);

    @Nonnull List<String> $list(@Nonnull Attribute attribute);

    @Nonnull LURI $uri(@Nonnull Attribute attribute);

    boolean default$bool(@Nonnull Attribute attribute, boolean defaultValue);

    boolean $bool(@Nonnull Attribute attribute);

    @Nonnull Double $d(@Nonnull Attribute attribute) throws NumberFormatException;

    int default$i(@Nonnull Attribute attribute, int defaultValue) throws NumberFormatException;

    @Nonnull Integer $i(@Nonnull Attribute attribute) throws NumberFormatException;

    @Nonnull Long $l(@Nonnull Attribute attribute) throws NumberFormatException;


    @Nonnull Date published();

    void published(Date published);

    /**
     * This returns a value without any validation.
     *
     * @param key
     * @return
     */
    String $raw(@Nonnull Attribute key);

    @Nullable String default$sub(@Nonnull Attribute attribute, @Nonnull Attribute subAttribute, String defaultValue);

    /**
     * Returns a un-aliased sub object from all properties with the common parent path.
     *
     * @param path     all properties that start with this will be used.
     * @param readonly the newly created sub entity should be readonly.
     * @return a new object from the sub properties.
     */
    @Nonnull NEW child(@Nonnull Attribute path, boolean readonly);

    /**
     * LSD Types are describe how an object can be represented and interacted with by clients.
     *
     * @return
     */
    @Nonnull TypeDef type();

    @Nonnull LURI uri();

    T uri(LURI uri);

    boolean hasURI();

    @Nonnull LURI getURIAttribute(@Nonnull Attribute attribute);

    /**
     * All LSD Objects have an id attribute which confirms to Java's {@link java.util.UUID} format, but for
     * GWT related reasons we use instead {@link cazcade.liquid.api.LiquidUUID} class.
     *
     * @return a universally unique identifier for this object
     */
    @Nonnull LiquidUUID id();

    @Nonnull LiquidUUID $uuid(@Nonnull Attribute attribute);

    @Nonnull Date updated();

    void updated(Date updated);

    @Nullable String getValue(@Nonnull String key);

    boolean has(@Nonnull Attribute key);

    boolean allowed(@Nonnull PermissionScope permissionScope, @Nonnull Permission permission);

    boolean hasChild(@Nonnull Attribute attribute);

    boolean is(@Nonnull Types type);

    boolean is(@Nonnull TypeDef typeDef);

    boolean isEmptyValue(@Nonnull Attribute key);

    boolean error();

    boolean isNewerThan(Entity entity);

    boolean readonly();

    void readonly(boolean readonly);

    boolean serializable();

    void remove(@Nonnull Attribute id);

    void removeCompletely(@Nonnull Attribute attribute);

    @Nonnull NEW removeChild(@Nonnull Attribute path);

    void remove$(@Nonnull Attribute id);

    void set(@Nonnull String key, String value);

    T $(@Nonnull Attribute parent, @Nonnull Attribute child, String value);

    /**
     * Set's an attribute value, the empty string "" will remove the property.
     * null is invalid, for potentially null values use
     * {@link #$notnull(Attribute, String)} instead.
     *
     * @param key   the attribute key.
     * @param value the value.
     */
    T $(@Nonnull Attribute key, String value);

    T $(@Nonnull Attribute checked, boolean bool);

    T $(@Nonnull Attribute attribute, long value);

    T $(@Nonnull Attribute attribute, LiquidUUID uuid);

    T $(@Nonnull Attribute attribute, LURI uri);

    T $(@Nonnull Attribute attribute, double value);

    T $(@Nonnull Attribute attribute, Date value);

    T $notnull(@Nonnull Attribute key, String value);

    T id(@Nonnull LiquidUUID id);

    T id(String id);

    /**
     * <b>Avoid using this method, it is primarily for internal use.</b>
     * It set's an attribute value, the empty string "" will remove the property.
     * null is invalid, for potentially null values
     * use {@link #$notnull(Attribute, String)} instead.
     *
     * @param key   the attribute key.
     * @param value the value.
     */
    T setValue(@Nonnull String key, String value);

    T $(@Nonnull Attribute key, List values);

    T timestamp();

    boolean wasPublishedAfter(@Nonnull Entity entity);

    boolean hasUpdated();

    boolean hasId();

    String nameOrId();

    /**
     * Returns a new entity which is the merge result of this entity and the supplied otherEntity.
     *
     * @param otherEntity the entity to merge with this entity.
     * @param destructive if true then overwrite values from this entity during the merge.
     * @return a new entity which is the result of a merge between this entity and the otherEntity.
     */
    NEW merge(Entity otherEntity, boolean destructive);

    String $sub(@Nonnull Attribute attribute, @Nonnull Attribute subAttribute);

    @Nonnull Node asFormatIndependentTree();

    @Nonnull Map<String, String> asMapForPersistence(boolean ignoreType, boolean update);



    @Nonnull  NEW $();

    /**
     * @deprecated use toString() instead.
     */
    String dump();

    /**
     * Use this for JSPs i.e. JSTL EL
     *
     * @return
     */
    @Nonnull Map<String, String> getCamelCaseMap();

    /**
     * The canonical format.
     *
     * @return a map of name/value pairs.
     */
    @Nonnull Map<String, String> map();



    class EntityUpdatedComparator implements Comparator<Entity> {
        @Override
        public int compare(@Nonnull final Entity entity, @Nonnull final Entity entity1) {
            final Date updated = entity.updated();
            return updated.compareTo(entity1.updated());
        }
    }

    class EntityPublishedComparator implements Comparator<Entity> {
        @Override
        public int compare(@Nonnull final Entity entity, @Nonnull final Entity entity1) {
            final Date published = entity.published();
            return published.compareTo(entity1.published());
        }
    }
}
