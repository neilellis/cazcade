/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;


/**
 * @author Neil Ellis
 */

public class SimpleEntity<T extends TransferEntity<T>> implements TransferEntity<T> {
    public static final  SimpleEntity EMPTY_ENTITY     = new SimpleEntity(true);
    private static final String       ID_KEY           = Dictionary.ID.getKeyName();
    private static final String       TYPE_KEY         = Dictionary.TYPE.getKeyName();
    private static final long         serialVersionUID = 1697435148665350511L;
    @SuppressWarnings("FieldAccessedSynchronizedAndUnsynchronized") @Nonnull
    //Cannot be final if serialized by GWT
    protected         PropertyStore lsdProperties;
    @Nullable
    // This is just a cache of the type stored as a string in the properties
    private transient TypeDef       typeDef;
    @Nullable
    // This is just a cache of the id stored as a string in the properties
    private transient LiquidUUID    uuid;
    private           boolean       readonly;

    protected SimpleEntity(@Nonnull final PropertyStore store) {
        lsdProperties = store.copy();
    }

    private SimpleEntity(@Nonnull final Map<String, String> lsdProperties) {
        for (final Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            if (entry.getValue() == null) {
                throw new NullPointerException("Tried to set the value of " + entry.getKey() + " to null.");
            }
        }
        this.lsdProperties = new MapPropertyStore(lsdProperties);
        initTypeDef();
        initUUID();
    }

    private SimpleEntity(@Nonnull final Node node) {
        this();
        final String path = "";
        final List<Node> children = node.getChildren();
        for (final Node child : children) {
            parse("", child, false);
        }
    }

    public SimpleEntity() {
        lsdProperties = new MapPropertyStore(new HashMap<String, String>());
    }

    SimpleEntity(boolean readonly) {
        lsdProperties = new MapPropertyStore(new HashMap<String, String>());
        this.readonly = readonly;
    }

    //    public boolean isValidOrEmptyValue(LSDDictionary key) {
    //        String value = lsdProperties.get(key.getKeyName());
    ////        return value == null || key.isValidFormat(FORMAT_VALIDATOR, value);
    //    }

    @Nonnull
    public static final SimpleEntity<? extends TransferEntity> createNewTransferEntity(@Nonnull final Types type, @Nonnull final LiquidUUID uuid) {
        final SimpleEntity entity = new SimpleEntity();
        entity.setType(type);
        entity.$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.$(Dictionary.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        entity.$(Dictionary.ID, uuid.toString());
        return entity;
    }

    @Nonnull
    public static final SimpleEntity<? extends TransferEntity> createFromNode(@Nonnull final Node node) {
        return new SimpleEntity(node);
    }

    @Nonnull
    public static final SimpleEntity<? extends TransferEntity> create(@Nonnull final Types type) {
        final SimpleEntity entity = new SimpleEntity();
        entity.setType(type);
        entity.$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.$(Dictionary.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        return entity;
    }

    @Nonnull
    public static final SimpleEntity<? extends TransferEntity> createNewEntity(@Nonnull final TypeDef type) {
        final SimpleEntity entity = new SimpleEntity();
        entity.setTypeDef(type);
        entity.$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.$(Dictionary.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        return entity;
    }

    //I can never figure our how this works, truly it drives me potty :-)
    @SuppressWarnings({"unchecked"})
    private static void addToStructuredMap(final List list, @Nonnull final String key, final String value) {
        final String[] props = key.split("\\.");
        List currentList = list;
        for (int i = 0; i < props.length; i++) {
            final String prop = props[i];
            if (prop.matches("[0-9]+")) {
                //Numeric so we're creating an array.
                if (i == props.length - 1) {
                    //                    final ArrayList arrayList = new ArrayList();
                    //                    final Map<String, ArrayList> map = Collections.singletonMap(key, arrayList);
                    //                    currentList.add(map);
                    //                    currentList = arrayList;
                    addToArray(Integer.parseInt(prop), currentList, props[i - 1], value);
                    return;
                } else {
                    //noinspection AssignmentToForLoopParameter
                    ++i;
                    currentList = addToArray(Integer.parseInt(prop) - 1, currentList, props[i], null);
                }
            } else {
                currentList = addToArray(0, currentList, prop, null);
            }
        }
        currentList.add(value);
    }

    @SuppressWarnings({"unchecked"}) @Nonnull
    private static List addToArray(final int arrayPos, @Nonnull final List currentList, final String prop, @Nullable final String value) {
        List result = currentList;
        //        if (currentList.size() < arrayPos) {
        //            throw new IllegalStateException("Missing array position for " + prop + " have you missed an entry or more before " + (arrayPos + 1) + " current size is " + currentList.size());
        //        }
        final int oldSize = result.size();
        if (oldSize <= arrayPos) {
            for (int i = 0; i <= arrayPos - oldSize; i++) {
                result.add(new HashMap<String, List>());
            }
        }
        if (result.get(arrayPos) instanceof String && value != null) {
            final List newList = new ArrayList();
            final HashMap<String, List> newMap = new HashMap<String, List>();
            newMap.put(prop, newList);
            newList.add(result.get(arrayPos));
            newList.add(value);
            result.set(arrayPos, newMap);
            return newList;
        }
        if (value == null) {
            final Map map = (Map) result.get(arrayPos);
            if (map.containsKey(prop)) {
                if (map.get(prop) instanceof List) {
                } else {
                    throw new IllegalArgumentException(
                            "Can't mix content and nodes, did you set an x.y='a' value then x.y.z='b'? The property was "
                            + prop);
                }
                result = (List) map.get(prop);
            } else {
                result = new ArrayList();
                map.put(prop, result);
            }
        } else {
            result.set(arrayPos, value);
        }
        return result;
    }

    @Nonnull
    public static final SimpleEntity<? extends TransferEntity> createNewEntity(@Nonnull final TypeDef type, @Nonnull final LiquidUUID uuid) {
        final SimpleEntity entity = new SimpleEntity();
        entity.setTypeDef(type);
        entity.$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.$(Dictionary.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        entity.$(Dictionary.ID, uuid.toString());
        return entity;
    }

    @Nonnull
    public static SimpleEntity<? extends TransferEntity> createFromProperties(@Nonnull final Map<String, String> lsdProperties) {
        return new SimpleEntity(lsdProperties);
    }

    private static String convertFromCamel(@Nonnull final CharSequence key) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            final char c = key.charAt(i);
            if (Character.isLowerCase(c)) {
                builder.append(c);
            } else {
                builder.append('.').append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

    @Nonnull
    public static SimpleEntity<?> createEmpty() {
        final SimpleEntity entity = new SimpleEntity();
        entity.$(Dictionary.UPDATED, String.valueOf(System.currentTimeMillis()));
        return entity;
    }

    @Nonnull
    public static SimpleEntity<?> emptyUnmodifiable() {
        return EMPTY_ENTITY;
    }

    private static String convertToCamel(@Nonnull final CharSequence key) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            final char c = key.charAt(i);
            if (c != '.') {
                builder.append(c);
            } else {
                //noinspection AssignmentToForLoopParameter
                ++i;
                builder.append(Character.toUpperCase(key.charAt(i)));
            }
        }
        return builder.toString();
    }

    public final void setType(@Nonnull final Types type) {
        assertNotReadonly();
        //noinspection ConstantConditions
        if (type.getValue() == null) {
            throw new NullPointerException("Cannot set type to a null value.");
        }

        lsdProperties.put(TYPE_KEY, type.getValue());
    }

    private void assertNotReadonly() {
        if (readonly) {
            throw new IllegalStateException("Entity is readonly, cannot be mutated.");
        }
    }

    private void initTypeDef() {
        synchronized (lsdProperties) {
            if (typeDef == null) {
                final String typeValue = lsdProperties.get(TYPE_KEY);
                if (typeValue != null) {
                    typeDef = new TypeDefImpl(typeValue);
                }
            }
        }
    }

    private void initUUID() {
        if (uuid == null) {
            final String id = lsdProperties.get(ID_KEY);
            if (id != null) {
                uuid = LiquidUUID.fromString(id);
            }
        }
    }

    private void parse(@Nonnull final String path, @Nonnull final Node node, final boolean array) {
        final String newPath;
        if (array) {
            newPath = path;
        } else {
            newPath = path.isEmpty() ? node.getName() : path + '.' + node.getName();
        }
        if (node.isLeaf()) {
            lsdProperties.put(newPath, node.getLeafValue());
        } else {
            if (node.isArray()) {
                final List<Node> children = node.getChildren();
                int count = 0;
                for (final Node child : children) {
                    if (count == 0 && child.isLeaf()) {
                        parse(newPath, child, true);
                    } else {
                        parse(newPath + '.' + (child.isLeaf() ? count : count + 1), child, true);
                    }
                    count++;
                }
            } else {
                final List<Node> nodeList = node.getChildren();
                for (final Node child : nodeList) {
                    parse(newPath, child, false);
                }
            }
        }
    }

    @SuppressWarnings("DesignForExtension") @Override
    public int hashCode() {
        return uri().hashCode();
    }

    @SuppressWarnings("DesignForExtension") @Override
    public boolean equals(@Nullable final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof SimpleEntity)) {
            return false;
        }
        return get(Dictionary.ID.getKeyName()).equals(((SimpleEntity) o).get(Dictionary.ID.getKeyName()));
    }

    @SuppressWarnings("DesignForExtension") @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder("{\n");
        for (final String property : lsdProperties.getKeys()) {
            buffer.append(property)
                  .append('=')
                  .append('\'')
                  .append(lsdProperties.get(property))
                  .append('\'')
                  .append(',')
                  .append('\n');
        }
        buffer.append("}\n");
        return buffer.toString();
    }

    @Override
    public final void addAnonymousSubEntity(@Nonnull final Attribute stem, @Nonnull final Entity entity) {
        assertNotReadonly();
        final Map<String, String> map = entity.map();
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            setValue(stem.getKeyName() + '.' + entry.getKey(), entry.getValue());
        }
    }

    @Override
    public final void children(@Nonnull final Attribute stem, @Nonnull final Collection<? extends TransferEntity<T>> entities) {
        assertNotReadonly();
        if (!stem.isSubEntity()) {
            throw new IllegalArgumentException("Cannot add a sub entity to a non sub entity property '" + stem.getKeyName() + "'.");
        }
        int count = 1;
        for (final Entity entity : entities) {
            final String stemKey = stem.getKeyName();
            final String existingId = lsdProperties.get(stemKey + '.' + count + ".id");
            if (existingId != null && !existingId.equals(entity.id().toString())) {
                throw new IllegalArgumentException("Attempted to add a sub entity to an entity which already has a different sub object.");
            }
            final Map<String, String> map = entity.map();
            for (final Map.Entry<String, String> entry : map.entrySet()) {
                setValue(stemKey + '.' + count + '.' + entry.getKey(), entry.getValue());
            }
            count++;
        }
    }

    @Override
    public final void child(@Nonnull final Attribute stem, @Nonnull final TransferEntity<T> entity, final boolean requiresId) {
        assertNotReadonly();
        //noinspection ConstantConditions
        if (entity == null) {
            throw new NullPointerException("Attempted to add a null sub entity using stem " + stem);
        }
        if (!stem.isSubEntity()) {
            throw new IllegalArgumentException("Cannot add a sub entity to a non sub entity property '" + stem.getKeyName() + "'.");
        }
        //noinspection ConstantConditions
        if (requiresId && entity.id() == null) {
            throw new IllegalArgumentException("Attempted to add a sub entity which had no id.");
        }
        final String stemKey = stem.getKeyName();
        final String existingId = lsdProperties.get(stemKey + ".id");
        if (existingId != null && !existingId.equals(entity.id().toString())) {
            throw new IllegalArgumentException("Attempted to add a sub entity to an entity which already has a different sub object.");
        }
        addAnonymousSubEntity(stem, entity);
    }

    @Nonnull @Override
    public final String asFreeText() {
        final StringBuilder s = new StringBuilder();
        for (final String value : lsdProperties.valueIterator()) {
            s.append(value);
            s.append(' ');
        }
        return s.toString();
    }

    @Nonnull @Override public String asDebugText() {
        final StringBuilder buffer = new StringBuilder("{");
        for (final String property : lsdProperties.getKeys()) {
            buffer.append(property).append('=').append('\'').append(lsdProperties.get(property)).append('\'').append(',');
        }
        buffer.append("}");
        return buffer.toString();
    }

    @Override
    public final boolean attributeIs(@Nonnull final Attribute attribute, @Nonnull final String comparison) {
        return has$(attribute) && $(attribute).equals(comparison);
    }

    @Override
    public final boolean canBe(@Nonnull final Types type) {
        return type().canBe(type);
    }

    @Override
    public final T $(@Nonnull final Entity entity, @Nonnull final Attribute attribute) {
        if (entity.has$(attribute)) {
            $(attribute, entity.$(attribute));
        }
        return (T) this;
    }

    @Override
    public final Object get(@Nonnull final String key) {
        final String dotStyleKey = convertFromCamel(key);
        if (hasSubEntity(dotStyleKey)) {
            return getSubEntity(dotStyleKey, true);
        } else {
            return lsdProperties.get(dotStyleKey);
        }
    }

    @Nonnull @Override
    public final String default$(@Nonnull final Attribute attribute, @Nonnull final String defaultValue) {
        final String result = lsdProperties.get(attribute.getKeyName());
        if (result == null || "null".equals(result)) {
            return defaultValue;
        }
        return result;
    }

    @Nonnull @Override
    public final String $(@Nonnull final Attribute attribute) {
        final String value = lsdProperties.get(attribute.getKeyName());
        //        if (value != null && !key.isValidFormat(FORMAT_VALIDATOR, (value))) {
        //            throw new IllegalArgumentException("The value in this T for " + key.name() + " was " + value + " which is invalid according to the dictionary.");
        //noinspection VariableNotUsedInsideIf
        if (value == null) {
            throw new IllegalArgumentException("There is no value for key "
                                               + attribute
                                               + " use hasAttribute('"
                                               + attribute
                                               + "') prior to getAttributeXXX('"
                                               + attribute
                                               + "') or use getAttributeXXX('"
                                               + attribute
                                               + "', <default>) to avoid this problem.");
        }
        //        }
        return value;
    }

    @Nonnull @Override
    public final List<String> $list(@Nonnull final Attribute attribute) {
        final List<String> values = new ArrayList<String>();
        if (has$(attribute)) {
            values.add($(attribute));
        }
        int count = 1;
        while (lsdProperties.containsProperty(attribute.getKeyName() + '.' + count)) {
            values.add(lsdProperties.get(attribute.getKeyName() + '.' + count));
            count++;
        }
        return values;
    }

    @Nonnull @Override
    public final LiquidURI $uri(@Nonnull final Attribute attribute) {
        if (has$(attribute)) {
            return new LiquidURI($(attribute));
        } else {
            throw new IllegalArgumentException("There is no value for key "
                                               + attribute
                                               + " use hasAttribute prior to getAttributeXXX() to avoid this problem");
        }
    }

    @Override
    public final boolean default$bool(@Nonnull final Attribute attribute, final boolean defaultValue) {
        if (!has$(attribute)) {
            return defaultValue;
        }
        final String value = $(attribute);
        if (!value.isEmpty()) {
            return "true".equals(value);
        } else {
            return defaultValue;
        }
    }

    @Override
    public final boolean $bool(@Nonnull final Attribute attribute) {
        final String value = $(attribute);
        return "true".equals(value);
    }

    @Nonnull @Override
    public final Double $d(@Nonnull final Attribute attribute) throws NumberFormatException {
        return Double.valueOf($(attribute));
    }

    @Override
    public final int default$i(@Nonnull final Attribute attribute, final int defaultValue) throws NumberFormatException {
        return Integer.parseInt(default$(attribute, String.valueOf(defaultValue)));
    }

    @Nonnull @Override
    public final Integer $i(@Nonnull final Attribute attribute) throws NumberFormatException {
        return Integer.valueOf($(attribute));
    }

    @Nonnull @Override
    public final Long $l(@Nonnull final Attribute attribute) throws NumberFormatException {
        return Long.valueOf($(attribute));
    }

    @Nonnull @Override
    public Date published() {
        if (has$(Dictionary.PUBLISHED)) {
            return new Date(Long.parseLong(lsdProperties.get(Dictionary.PUBLISHED.getKeyName())));
        } else {
            throw new IllegalStateException("Attempted to get the published property of an entity before it had been set.");
        }
    }

    @Override
    public void published(@Nonnull final Date published) {
        $(Dictionary.PUBLISHED, published);
    }

    @Override
    public final String $raw(@Nonnull final Attribute key) {
        return lsdProperties.get(key.getKeyName());
    }

    @Override
    public final String default$sub(@Nonnull final Attribute attribute, @Nonnull final Attribute subAttribute, final String defaultValue) {
        return getValue(attribute.getKeyName() + '.' + subAttribute.getKeyName(), defaultValue);
    }

    @Override @Nonnull
    public final TransferEntityCollection children(@Nonnull final Attribute key) {
        final String keyString = key.getKeyName();
        final TreeMap<Integer, SimpleEntity<T>> entities = new TreeMap<Integer, SimpleEntity<T>>();
        for (final String property : lsdProperties.getKeys()) {
            if (property.startsWith(keyString + '.')) {
                final String subEntityKeyFull = property.substring(keyString.length() + 1);
                final int firstDot = subEntityKeyFull.indexOf('.');
                final Integer subEntityNumber;
                final String subEntityKey;
                if (firstDot < 0) {
                    subEntityNumber = 0;
                    subEntityKey = subEntityKeyFull;
                } else {
                    final String subEntityString = subEntityKeyFull.substring(0, firstDot);
                    if (subEntityString.matches("[0-9]+")) {
                        subEntityNumber = Integer.valueOf(subEntityString);
                        subEntityKey = subEntityKeyFull.substring(firstDot + 1);
                    } else {
                        subEntityNumber = 0;
                        subEntityKey = subEntityKeyFull;
                    }
                }
                SimpleEntity subEntity = entities.get(subEntityNumber);
                if (subEntity == null) {
                    subEntity = createEmpty();
                    entities.put(subEntityNumber, subEntity);
                }
                subEntity.setValue(subEntityKey, lsdProperties.get(property));
            }
        }
        return new TransferEntityCollection(entities.values());
    }

    @Override @Nonnull
    public final TransferEntityCollection children() {
        return children(Dictionary.CHILD_A);
    }

    @Override @Nonnull
    public final SimpleEntity<T> child(@Nonnull final Attribute path, final boolean readonlyEntity) {
        final String keyString = path.getKeyName();
        return getSubEntity(keyString, readonlyEntity);
    }

    @Nonnull @Override
    public final TypeDef type() {
        initTypeDef();
        synchronized (lsdProperties) {
            if (typeDef == null) {
                throw new IllegalStateException("typeDef was null");
            }
            return typeDef;
        }
    }

    @Override @Nonnull
    public final LiquidURI uri() {
        final String uri = lsdProperties.get(Dictionary.URI.getKeyName());
        if (uri == null) {
            throw new ValidationException("No URI for this entity: " + toString());
        }
        return new LiquidURI(uri);
    }

    @Override
    public final void uri(@Nonnull final LiquidURI uri) {
        assertNotReadonly();
        $(Dictionary.URI, uri.asString());
    }

    @Override
    public final boolean hasURI() {
        return getValue(Dictionary.URI.getKeyName()) != null;
    }

    @Nonnull @Override
    public final LiquidURI getURIAttribute(@Nonnull final Attribute attribute) {
        if (!$(attribute).isEmpty()) {
            return new LiquidURI($(attribute));
        } else {
            throw new IllegalArgumentException("There is no value for key "
                                               + attribute
                                               + " use hasAttribute prior to getAttributeXXX() to avoid this problem");
        }
    }

    @Nonnull @Override
    public final LiquidUUID id() {
        initUUID();
        if (uuid == null) {
            throw new IllegalStateException("Attempted to access the UUID of an entity before it has been set.");
        }
        return uuid;
    }

    @Nonnull @Override
    public final LiquidUUID $uuid(@Nonnull final Attribute attribute) {
        final String result = $(attribute);
        if (!result.isEmpty()) {
            return LiquidUUID.fromString(result);
        } else {
            throw new IllegalArgumentException("There was an emptyy string for key "
                                               + attribute
                                               + " use hasAttribute prior to getAttributeXXX() to avoid this problem");
        }
        //        }        }
    }

    @Nonnull @Override
    public Date updated() {
        if (has$(Dictionary.UPDATED)) {
            return new Date(Long.parseLong(lsdProperties.get(Dictionary.UPDATED.getKeyName())));
        } else {
            throw new IllegalStateException("Attempted to get the updated property of an entity before it had been set.");
        }
    }

    @Override
    public void updated(@Nonnull final Date updated) {
        $(Dictionary.UPDATED, updated);
    }

    @Override
    public final String getValue(@Nonnull final String key) {
        return lsdProperties.get(key);
    }

    @Override
    public final boolean has$(@Nonnull final Attribute key) {
        return lsdProperties.containsProperty(key.getKeyName());
    }

    @Override
    public final boolean allowed(@Nonnull final PermissionScope permissionScope, @Nonnull final Permission permission) {
        return PermissionSet.createPermissionSet($(Dictionary.PERMISSIONS)).hasPermission(permissionScope, permission);
    }

    @Override
    public final boolean hasChild(@Nonnull final Attribute attribute) {
        final String keyString = attribute.getKeyName();
        return hasSubEntity(keyString);
    }

    @Override
    public final boolean is(@Nonnull final Types type) {
        return type().getPrimaryType().isA(type);
    }

    @Override
    public boolean is(@Nonnull final TypeDef typeDef) {
        return type().equals(typeDef);
    }

    @Override
    public boolean isEmptyValue(@Nonnull final Attribute key) {
        final String value = lsdProperties.get(key.getKeyName());
        return value == null;
    }

    @Override
    public boolean error() {
        return lsdProperties.get(TYPE_KEY).startsWith("System.Error");
    }

    @Override
    public boolean isNewerThan(@Nonnull final Entity entity) {
        final Date updated = updated();
        return updated.after(entity.updated());
    }

    @Override
    public boolean readonly() {
        return readonly;
    }

    @Override
    public void readonly(final boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public boolean serializable() {
        return lsdProperties.isSerializable();
    }

    @Override
    public void remove(@Nonnull final Attribute key) {
        assertNotReadonly();
        lsdProperties.put(key.getKeyName(), "");
    }

    @Override
    public void removeCompletely(@Nonnull final Attribute attribute) {
        assertNotReadonly();
        lsdProperties.remove(attribute.getKeyName());
    }

    @Override @Nonnull
    public SimpleEntity<T> removeChild(@Nonnull final Attribute path) {
        final String keyString = path.getKeyName();
        final SimpleEntity subEntity = createEmpty();
        final Collection<String> toDelete = new ArrayList<String>();
        for (final String key : lsdProperties.getKeys()) {
            if (key.startsWith(keyString + '.')) {
                final String subEntityKey = key.substring(keyString.length() + 1);
                subEntity.setValue(subEntityKey, lsdProperties.get(key));
                toDelete.add(key);
            }
        }
        for (final String key : toDelete) {
            lsdProperties.remove(key);
        }
        return subEntity;
    }

    @Override
    public void remove$(@Nonnull final Attribute id) {
        assertNotReadonly();
        lsdProperties.remove(id.toString());
    }

    @Override
    public void set(@Nonnull final String key, @Nullable final String value) {
        assertNotReadonly();
        if (value == null) {
            throw new NullPointerException("Cannot set " + key + " to a null value.");
        }
        lsdProperties.put(convertFromCamel(key), value);
    }

    @Override
    public T $(@Nonnull final Attribute parent, @Nonnull final Attribute child, final String value) {
        assertNotReadonly();
        setValue(parent.getKeyName() + '.' + child.getKeyName(), value);
        return (T) this;
    }

    @Override
    public T $(@Nonnull final Attribute key, @Nonnull final String value) {
        assertNotReadonly();
        //noinspection ConstantConditions
        if (key == null) {
            throw new IllegalArgumentException("Cannot set an attribute to a *null key*.");
        }
        //noinspection ConstantConditions
        if (value == null) {
            throw new NullPointerException("Cannot set an attribute to a *null value*, only an empty string.");
        }
        setValue(key.getKeyName(), value);
        return (T) this;
    }

    @Override
    public T $(@Nonnull final Attribute checked, final boolean bool) {
        assertNotReadonly();
        $(checked, bool ? "true" : "false");
        return (T) this;
    }

    @Override
    public T $(@Nonnull final Attribute attribute, final long value) {
        assertNotReadonly();
        $(attribute, String.valueOf(value));
        return (T) this;
    }

    @Override
    public T $(@Nonnull final Attribute attribute, @Nonnull final LiquidUUID uuid) {
        assertNotReadonly();
        $(attribute, uuid.toString());
        return (T) this;
    }

    @Override
    public T $(@Nonnull final Attribute attribute, @Nonnull final LiquidURI uri) {
        assertNotReadonly();
        $(attribute, uri.asString());
        return (T) this;
    }

    @Override
    public T $(@Nonnull final Attribute attribute, final double value) {
        assertNotReadonly();
        $(attribute, String.valueOf(value));
        return (T) this;
    }

    @Override
    public T $(@Nonnull final Attribute attribute, @Nonnull final Date value) {
        $(attribute, value.getTime());
        return (T) this;
    }

    @Override
    public T $notnull(@Nonnull final Attribute key, @Nullable final String value) {
        assertNotReadonly();
        if (value != null) {
            setValue(key.getKeyName(), value);
        }
        return (T) this;
    }

    @Override
    public T id(@Nonnull final LiquidUUID id) {
        assertNotReadonly();
        lsdProperties.put(ID_KEY, id.toString().toLowerCase());
        return (T) this;
    }

    @Override
    public T id(final String id) {
        assertNotReadonly();
        set(ID_KEY, id);
        return (T) this;
    }

    @Override
    public T setValue(@Nonnull final String key, @Nullable final String value) {
        assertNotReadonly();
        if (value == null) {
            throw new NullPointerException("The value for key '" + key + "' was null.");
        }
        if (!key.matches("[a-zA-Z0-9]+[a-zA-Z0-9\\._]*[a-zA-Z0-9_]*?")) {
            throw new IllegalArgumentException("Invalid key name " + key);
        }
        //        System.err.println("Setting " + key + "=" + value);
        lsdProperties.put(key, value);
        return (T) this;
    }

    @Override
    public T $(@Nonnull final Attribute key, @Nonnull final List values) {
        assertNotReadonly();
        for (final String property : lsdProperties.getKeys()) {
            if (property.startsWith(key.getKeyName() + '.')) {
                lsdProperties.put(property, "");
            }
        }
        int count = 0;
        for (final Object value : values) {
            //            int lastDot = key.getKeyName().lastIndexOf('.');
            //            String subPath;
            //            String finalElement;
            //            if (lastDot < 0) {
            //                subPath = "";
            //                finalElement = key.getKeyName();
            //            } else {
            //                subPath = key.getKeyName().substring(0, lastDot + 1);
            //                finalElement = key.getKeyName().substring(lastDot + 1);
            //            }
            if (count > 0) {
                setValue(key.getKeyName() + '.' + count, value.toString());
            } else {
                $(key, value.toString());
            }
            count++;
        }
        return (T) this;
    }

    @Override
    public T timestamp() {
        lsdProperties.put(Dictionary.UPDATED.getKeyName(), String.valueOf(System.currentTimeMillis()));
        return (T) this;
    }

    @Override
    public boolean wasPublishedAfter(@Nonnull final Entity entity) {
        final Date published = published();
        return published.after(entity.published());
    }

    @Override
    public boolean hasUpdated() {
        return has$(Dictionary.UPDATED);
    }

    @Override public boolean hasId() {
        return has$(Dictionary.ID);
    }

    @Override public String nameOrId() {
        if (has$(Dictionary.NAME)) {
            return $(Dictionary.NAME);
        } else if (hasId()) {
            return id().toString();
        } else {
            throw new IllegalStateException("Entity has neither name or id, so cannot retrieve getNameOrId()");
        }
    }

    @Override public SimpleEntity merge(final Entity newEntity, final boolean destructive) {
        final SimpleEntity result = $();
        final Map<String, String> map = newEntity.map();
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            if (!destructive && result.getValue(entry.getKey()) == null || destructive) {
                result.setValue(entry.getKey(), entry.getValue());
            }
        }
        return result;

    }

    @Override
    public final String $sub(@Nonnull final Attribute attribute, @Nonnull final Attribute subAttribute) {
        return getValue(attribute.getKeyName() + '.' + subAttribute.getKeyName());
    }

    public void setTypeDef(@Nonnull final TypeDef type) {
        typeDef = type;
        if (type.asString() == null) {
            throw new NullPointerException("Cannot set type def to a null value.");
        }
        lsdProperties.put(TYPE_KEY, type.asString());
    }

    @Override @Nonnull
    public final Node asFormatIndependentTree() {
        final List root = new ArrayList();
        final Map<String, List> values = new HashMap<String, List>();
        for (final String key : lsdProperties.getKeys()) {
            addToStructuredMap(root, key, lsdProperties.get(key));
        }
        return new SimpleNode("root", root);
    }

    @Override @Nonnull
    public final Map<String, String> asMapForPersistence(final boolean ignoreType, final boolean update) {
        final Map<String, String> typedMap = new HashMap<String, String>();
        for (final String key : lsdProperties.getKeys()) {
            final String[] strings = key.split("\\.");
            if (strings.length > 0) {
                final Attribute prefixAttribute = Attribute.valueOf(strings[0]);
                //todo: this should check for all sub-entity prefixes and not assume they are a single word long.
                if (prefixAttribute == null
                    || !prefixAttribute.isSubEntity()
                    || prefixAttribute.includeAttributeInPersistence(ignoreType, update)) {
                    if (prefixAttribute != null && prefixAttribute.isSubEntity()) {
                        typedMap.put(key, lsdProperties.get(key));
                    } else {
                        final Attribute dictionaryKeyName = Attribute.valueOf(key);
                        if (dictionaryKeyName == null) {
                            throw new UnknownAttributeException("Unknown attribute %s", key);
                        }

                        if (dictionaryKeyName.includeAttributeInPersistence(ignoreType, update)) {
                            typedMap.put(key, lsdProperties.get(key));
                        }
                    }
                } else {
                    //skip
                    //                    System.err.println("Skipped persisting " + entry.getKey());
                }
            }
        }
        return typedMap;
    }

    @Nonnull @Override
    public final SimpleEntity asUpdateEntity() {
        final SimpleEntity newEntity = createNewEntity(type());
        newEntity.uri(uri());
        return newEntity;
    }

    @Override @Nonnull
    public final SimpleEntity $() {
        return createFromProperties(lsdProperties.asMap());
    }

    @Override
    public final String dump() {
        final StringBuilder buffer = new StringBuilder();
        for (final String property : lsdProperties.getKeys()) {
            buffer.append(property).append('=').append(lsdProperties.get(property)).append("\n");
        }
        return buffer.toString();
    }

    @Nonnull @Override
    public final Map<String, String> getCamelCaseMap() {
        final Map<String, String> result = new HashMap<String, String>();
        for (final String key : lsdProperties.getKeys()) {
            result.put(convertToCamel(key), lsdProperties.get(key));
        }
        if (hasURI()) {
            final LiquidURI uri = uri();
            if (BoardURL.isConvertable(uri)) {
                result.put("shortUrl", uri.board().safe());
            }
        }
        if (has$(Dictionary.SOURCE) && BoardURL.isConvertable(getURIAttribute(Dictionary.SOURCE))) {
            //noinspection ConstantConditions
            result.put("sourceShortUrl", getURIAttribute(Dictionary.SOURCE).board().safe());
        }
        return result;
    }

    @Nonnull @Override
    public final Map<String, String> map() {
        return lsdProperties.asMap();
    }

    private boolean hasSubEntity(final String keyString) {
        for (final String key : lsdProperties.getKeys()) {
            if (key.startsWith(keyString + '.')) {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    private SimpleEntity getSubEntity(@Nonnull final String keyString, final boolean readonlyEntity) {
        final SimpleEntity subEntity = createEmpty();
        for (final String key : lsdProperties.getKeys()) {
            if (key.startsWith(keyString + '.')) {
                final String subEntityKey = key.substring(keyString.length() + 1);
                subEntity.setValue(subEntityKey, lsdProperties.get(key));
            }
        }
        subEntity.readonly(readonlyEntity);
        return subEntity;
    }

    private String getValue(final String key, final String defaultValue) {
        final String value = lsdProperties.get(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }
}
