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

public class LSDSimpleEntity implements LSDTransferEntity {
    private static final String ID_KEY           = LSDAttribute.ID.getKeyName();
    private static final String TYPE_KEY         = LSDAttribute.TYPE.getKeyName();
    private static final long   serialVersionUID = 1697435148665350511L;
    @Nonnull
    private           LSDPropertyStore lsdProperties;
    @Nullable
    // This is just a cache of the type stored as a string in the properties
    private transient LSDTypeDef       lsdTypeDef;
    @Nullable
    // This is just a cache of the id stored as a string in the properties
    private transient LiquidUUID       uuid;
    private           boolean          readonly;

    protected LSDSimpleEntity(@Nonnull final LSDPropertyStore store) {
        lsdProperties = store.copy();
    }

    private LSDSimpleEntity(@Nonnull final Map<String, String> lsdProperties) {
        for (final Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            if (entry.getValue() == null) {
                throw new NullPointerException("Tried to set the value of " + entry.getKey() + " to null.");
            }
        }
        this.lsdProperties = new LSDMapPropertyStore(lsdProperties);
        initTypeDef();
        initUUID();
    }

    private LSDSimpleEntity(@Nonnull final LSDNode lsdNode) {
        this();
        final String path = "";
        final List<LSDNode> children = lsdNode.getChildren();
        for (final LSDNode child : children) {
            parse("", child, false);
        }
    }

    public LSDSimpleEntity() {
        lsdProperties = new LSDMapPropertyStore(new HashMap<String, String>());
    }

    //    public boolean isValidOrEmptyValue(LSDDictionary key) {
    //        String value = lsdProperties.get(key.getKeyName());
    ////        return value == null || key.isValidFormat(FORMAT_VALIDATOR, value);
    //    }

    @Nonnull
    public static final LSDTransferEntity createNewTransferEntity(@Nonnull final LSDDictionaryTypes type, @Nonnull final LiquidUUID uuid) {
        final LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setType(type);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.ID, uuid.toString());
        return entity;
    }

    @Nonnull
    public static final LSDSimpleEntity createFromNode(@Nonnull final LSDNode lsdNode) {
        return new LSDSimpleEntity(lsdNode);
    }

    @Nonnull
    public static final LSDTransferEntity createNewEntity(@Nonnull final LSDDictionaryTypes type) {
        final LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setType(type);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        return entity;
    }

    @Nonnull
    public static final LSDTransferEntity createNewEntity(@Nonnull final LSDTypeDef type) {
        final LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setTypeDef(type);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
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
                }
                else {
                    //noinspection AssignmentToForLoopParameter
                    ++i;
                    currentList = addToArray(Integer.parseInt(prop) - 1, currentList, props[i], null);
                }
            }
            else {
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
                }
                else {
                    throw new IllegalArgumentException(
                            "Can't mix content and nodes, did you set an x.y='a' value then x.y.z='b'? The property was "
                            + prop);
                }
                result = (List) map.get(prop);
            }
            else {
                result = new ArrayList();
                map.put(prop, result);
            }
        }
        else {
            result.set(arrayPos, value);
        }
        return result;
    }

    @Nonnull
    public static final LSDTransferEntity createNewEntity(@Nonnull final LSDTypeDef type, @Nonnull final LiquidUUID uuid) {
        final LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setTypeDef(type);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.ID, uuid.toString());
        return entity;
    }

    @Nonnull
    public static LSDSimpleEntity createFromProperties(@Nonnull final Map<String, String> lsdProperties) {
        return new LSDSimpleEntity(lsdProperties);
    }

    private static String convertFromCamel(@Nonnull final CharSequence key) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            final char c = key.charAt(i);
            if (Character.isLowerCase(c)) {
                builder.append(c);
            }
            else {
                builder.append('.').append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

    @Nonnull
    public static LSDTransferEntity createEmpty() {
        final LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        return entity;
    }

    private static String convertToCamel(@Nonnull final CharSequence key) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            final char c = key.charAt(i);
            if (c != '.') {
                builder.append(c);
            }
            else {
                //noinspection AssignmentToForLoopParameter
                ++i;
                builder.append(Character.toUpperCase(key.charAt(i)));
            }
        }
        return builder.toString();
    }

    public final void setType(@Nonnull final LSDDictionaryTypes type) {
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
            if (lsdTypeDef == null) {
                final String typeValue = lsdProperties.get(TYPE_KEY);
                if (typeValue != null) {
                    lsdTypeDef = new LSDTypeDefImpl(typeValue);
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

    private void parse(@Nonnull final String path, @Nonnull final LSDNode node, final boolean array) {
        final String newPath;
        if (array) {
            newPath = path;
        }
        else {
            newPath = path.isEmpty() ? node.getName() : path + '.' + node.getName();
        }
        if (node.isLeaf()) {
            lsdProperties.put(newPath, node.getLeafValue());
        }
        else {
            if (node.isArray()) {
                final List<LSDNode> children = node.getChildren();
                int count = 0;
                for (final LSDNode child : children) {
                    if (count == 0 && child.isLeaf()) {
                        parse(newPath, child, true);
                    }
                    else {
                        parse(newPath + '.' + (child.isLeaf() ? count : count + 1), child, true);
                    }
                    count++;
                }
            }
            else {
                final List<LSDNode> lsdNodeList = node.getChildren();
                for (final LSDNode child : lsdNodeList) {
                    parse(newPath, child, false);
                }
            }
        }
    }

    @SuppressWarnings("DesignForExtension") @Override
    public int hashCode() {
        return getURI().hashCode();
    }

    @SuppressWarnings("DesignForExtension") @Override
    public boolean equals(@Nullable final Object o) {
        final LiquidURI uri = getURI();
        return this == o || !(o == null || getClass() != o.getClass()) && uri.equals(((LSDBaseEntity) o).getURI());
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
    public final void addAnonymousSubEntity(@Nonnull final LSDAttribute stem, @Nonnull final LSDBaseEntity entity) {
        assertNotReadonly();
        final Map<String, String> map = entity.getMap();
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            setValue(stem.getKeyName() + '.' + entry.getKey(), entry.getValue());
        }
    }

    @Override
    public final <T extends LSDBaseEntity> void addSubEntities(@Nonnull final LSDAttribute stem, @Nonnull final Collection<T> entities) {
        assertNotReadonly();
        if (!stem.isSubEntity()) {
            throw new IllegalArgumentException("Cannot add a sub entity to a non sub entity property '" + stem.getKeyName() + "'.");
        }
        int count = 1;
        for (final LSDBaseEntity entity : entities) {
            final String stemKey = stem.getKeyName();
            final String existingId = lsdProperties.get(stemKey + '.' + count + ".id");
            if (existingId != null && !existingId.equals(entity.getUUID().toString())) {
                throw new IllegalArgumentException("Attempted to add a sub entity to an entity which already has a different sub object.");
            }
            final Map<String, String> map = entity.getMap();
            for (final Map.Entry<String, String> entry : map.entrySet()) {
                setValue(stemKey + '.' + count + '.' + entry.getKey(), entry.getValue());
            }
            count++;
        }
    }

    @Override
    public final void addSubEntity(@Nonnull final LSDAttribute stem, @Nonnull final LSDBaseEntity entity, final boolean requiresId) {
        assertNotReadonly();
        //noinspection ConstantConditions
        if (entity == null) {
            throw new NullPointerException("Attempted to add a null sub entity using stem " + stem);
        }
        if (!stem.isSubEntity()) {
            throw new IllegalArgumentException("Cannot add a sub entity to a non sub entity property '" + stem.getKeyName() + "'.");
        }
        //noinspection ConstantConditions
        if (requiresId && entity.getUUID() == null) {
            throw new IllegalArgumentException("Attempted to add a sub entity which had no id.");
        }
        final String stemKey = stem.getKeyName();
        final String existingId = lsdProperties.get(stemKey + ".id");
        if (existingId != null && !existingId.equals(entity.getUUID().toString())) {
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
    public final boolean attributeIs(@Nonnull final LSDAttribute attribute, @Nonnull final String comparison) {
        return hasAttribute(attribute) && getAttribute(attribute).equals(comparison);
    }

    @Override
    public final boolean canBe(@Nonnull final LSDDictionaryTypes type) {
        return getTypeDef().canBe(type);
    }

    @Override
    public final void copyAttribute(@Nonnull final LSDBaseEntity entity, @Nonnull final LSDAttribute attribute) {
        setAttribute(attribute, entity.getAttribute(attribute));
    }

    @Override
    public final Object get(@Nonnull final String key) {
        final String dotStyleKey = convertFromCamel(key);
        if (hasSubEntity(dotStyleKey)) {
            return getSubEntity(dotStyleKey, true);
        }
        else {
            return lsdProperties.get(dotStyleKey);
        }
    }

    @Nonnull @Override
    public final String getAttribute(@Nonnull final LSDAttribute attribute, @Nonnull final String defaultValue) {
        final String result = lsdProperties.get(attribute.getKeyName());
        if (result == null || "null".equals(result)) {
            return defaultValue;
        }
        return result;
    }

    @Nonnull @Override
    public final String getAttribute(@Nonnull final LSDAttribute attribute) {
        final String value = lsdProperties.get(attribute.getKeyName());
        //        if (value != null && !key.isValidFormat(FORMAT_VALIDATOR, (value))) {
        //            throw new IllegalArgumentException("The value in this LSDTransferEntity for " + key.name() + " was " + value + " which is invalid according to the dictionary.");
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
    public final List<String> getAttributeAsList(@Nonnull final LSDAttribute attribute) {
        final List<String> values = new ArrayList<String>();
        if (hasAttribute(attribute)) {
            values.add(getAttribute(attribute));
        }
        int count = 1;
        while (lsdProperties.containsProperty(attribute.getKeyName() + '.' + count)) {
            values.add(lsdProperties.get(attribute.getKeyName() + '.' + count));
            count++;
        }
        return values;
    }

    @Nonnull @Override
    public final LiquidURI getAttributeAsURI(@Nonnull final LSDAttribute attribute) {
        if (hasAttribute(attribute)) {
            return new LiquidURI(getAttribute(attribute));
        }
        else {
            throw new IllegalArgumentException("There is no value for key "
                                               + attribute
                                               + " use hasAttribute prior to getAttributeXXX() to avoid this problem");
        }
    }

    @Override
    public final boolean getBooleanAttribute(@Nonnull final LSDAttribute attribute, final boolean defaultValue) {
        if (!hasAttribute(attribute)) {
            return defaultValue;
        }
        final String value = getAttribute(attribute);
        if (!value.isEmpty()) {
            return "true".equals(value);
        }
        else {
            return defaultValue;
        }
    }

    @Override
    public final boolean getBooleanAttribute(@Nonnull final LSDAttribute attribute) {
        final String value = getAttribute(attribute);
        return "true".equals(value);
    }

    @Nonnull @Override
    public final Double getDoubleAttribute(@Nonnull final LSDAttribute attribute) throws NumberFormatException {
        return Double.valueOf(getAttribute(attribute));
    }

    @Override
    public final int getIntegerAttribute(@Nonnull final LSDAttribute attribute, final int defaultValue) throws NumberFormatException {
        return Integer.parseInt(getAttribute(attribute, String.valueOf(defaultValue)));
    }

    @Nonnull @Override
    public final Integer getIntegerAttribute(@Nonnull final LSDAttribute attribute) throws NumberFormatException {
        return Integer.valueOf(getAttribute(attribute));
    }

    @Nonnull @Override
    public final Long getLongAttribute(@Nonnull final LSDAttribute attribute) throws NumberFormatException {
        return Long.valueOf(getAttribute(attribute));
    }

    @Nonnull @Override
    public Date getPublished() {
        if (hasAttribute(LSDAttribute.PUBLISHED)) {
            return new Date(Long.parseLong(lsdProperties.get(LSDAttribute.PUBLISHED.getKeyName())));
        }
        else {
            throw new IllegalStateException("Attempted to get the published property of an entity before it had been set.");
        }
    }

    @Override
    public void setPublished(@Nonnull final Date published) {
        setAttribute(LSDAttribute.PUBLISHED, published);
    }

    @Override
    public final String getRawValue(@Nonnull final LSDAttribute key) {
        return lsdProperties.get(key.getKeyName());
    }

    @Override
    public final String getSubAttribute(@Nonnull final LSDAttribute attribute, @Nonnull final LSDAttribute subAttribute, final String defaultValue) {
        return getValue(attribute.getKeyName() + '.' + subAttribute.getKeyName(), defaultValue);
    }

    @Override @Nonnull
    public final List<? extends LSDBaseEntity> getSubEntities(@Nonnull final LSDAttribute key) {
        final String keyString = key.getKeyName();
        final TreeMap<Integer, LSDSimpleEntity> entities = new TreeMap<Integer, LSDSimpleEntity>();
        for (final String property : lsdProperties.getKeys()) {
            if (property.startsWith(keyString + '.')) {
                final String subEntityKeyFull = property.substring(keyString.length() + 1);
                final int firstDot = subEntityKeyFull.indexOf('.');
                final Integer subEntityNumber;
                final String subEntityKey;
                if (firstDot < 0) {
                    subEntityNumber = 0;
                    subEntityKey = subEntityKeyFull;
                }
                else {
                    final String subEntityString = subEntityKeyFull.substring(0, firstDot);
                    if (subEntityString.matches("[0-9]+")) {
                        subEntityNumber = Integer.valueOf(subEntityString);
                        subEntityKey = subEntityKeyFull.substring(firstDot + 1);
                    }
                    else {
                        subEntityNumber = 0;
                        subEntityKey = subEntityKeyFull;
                    }
                }
                LSDSimpleEntity subEntity = entities.get(subEntityNumber);
                if (subEntity == null) {
                    subEntity = (LSDSimpleEntity) createEmpty();
                    entities.put(subEntityNumber, subEntity);
                }
                subEntity.setValue(subEntityKey, lsdProperties.get(property));
            }
        }
        return new ArrayList<LSDBaseEntity>(entities.values());
    }

    @Override @Nonnull
    public final LSDTransferEntity getSubEntity(@Nonnull final LSDAttribute path, final boolean readonlyEntity) {
        final String keyString = path.getKeyName();
        return getSubEntity(keyString, readonlyEntity);
    }

    @Nonnull @Override
    public final LSDTypeDef getTypeDef() {
        initTypeDef();
        synchronized (lsdProperties) {
            if (lsdTypeDef == null) {
                throw new IllegalStateException("lsdTypeDef was null");
            }
            return lsdTypeDef;
        }
    }

    @Override @Nonnull
    public final LiquidURI getURI() {
        final String uri = lsdProperties.get(LSDAttribute.URI.getKeyName());
        if (uri == null) {
            throw new LSDValidationException("No URI for this entity: " + toString());
        }
        return new LiquidURI(uri);
    }

    @Override
    public final boolean hasURI() {
        return getValue(LSDAttribute.URI.getKeyName()) != null;
    }

    @Nonnull @Override
    public final LiquidURI getURIAttribute(@Nonnull final LSDAttribute attribute) {
        final String value = getAttribute(attribute);
        if (!value.isEmpty()) {
            return new LiquidURI(value);
        }
        else {
            throw new IllegalArgumentException("There is no value for key "
                                               + attribute
                                               + " use hasAttribute prior to getAttributeXXX() to avoid this problem");
        }
    }

    @Nonnull @Override
    public final LiquidUUID getUUID() {
        initUUID();
        if (uuid == null) {
            throw new IllegalStateException("Attempted to access the UUID of an entity before it has been set.");
        }
        return uuid;
    }

    @Nonnull @Override
    public final LiquidUUID getUUIDAttribute(@Nonnull final LSDAttribute attribute) {
        final String result = getAttribute(attribute);
        if (!result.isEmpty()) {
            return LiquidUUID.fromString(result);
        }
        else {
            throw new IllegalArgumentException("There was an emptyy string for key "
                                               + attribute
                                               + " use hasAttribute prior to getAttributeXXX() to avoid this problem");
        }
        //        }        }
    }

    @Nonnull @Override
    public Date getUpdated() {
        if (hasAttribute(LSDAttribute.UPDATED)) {
            return new Date(Long.parseLong(lsdProperties.get(LSDAttribute.UPDATED.getKeyName())));
        }
        else {
            throw new IllegalStateException("Attempted to get the updated property of an entity before it had been set.");
        }
    }

    @Override
    public void setUpdated(@Nonnull final Date updated) {
        setAttribute(LSDAttribute.UPDATED, updated);
    }

    @Override
    public void setValue(@Nonnull final String key, @Nullable final String value) {
        assertNotReadonly();
        if (value == null) {
            throw new NullPointerException("The value for key '" + key + "' was null.");
        }
        if (!key.matches("[a-zA-Z0-9]+[a-zA-Z0-9\\._]*[a-zA-Z0-9_]*?")) {
            throw new IllegalArgumentException("Invalid key name " + key);
        }
        //        System.err.println("Setting " + key + "=" + value);
        lsdProperties.put(key, value);
    }

    @Override
    public void setValues(@Nonnull final LSDAttribute key, @Nonnull final List values) {
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
            }
            else {
                setAttribute(key, value.toString());
            }
            count++;
        }
    }

    @Override
    public void timestamp() {
        lsdProperties.put(LSDAttribute.UPDATED.getKeyName(), String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public boolean wasPublishedAfter(@Nonnull final LSDBaseEntity entity) {
        final Date published = getPublished();
        return published.after(entity.getPublished());
    }

    @Override
    public boolean hasUpdated() {
        return hasAttribute(LSDAttribute.UPDATED);
    }

    @Override public boolean hasId() {
        return hasAttribute(LSDAttribute.ID);
    }

    @Override public String getNameOrId() {
        if (hasAttribute(LSDAttribute.NAME)) {
            return getAttribute(LSDAttribute.NAME);
        }
        else if (hasId()) {
            return getUUID().toString();
        }
        else {
            throw new IllegalStateException("Entity has neither name or id, so cannot retrieve getNameOrId()");
        }
    }

    @Override
    public final String getValue(@Nonnull final String key) {
        return lsdProperties.get(key);
    }

    @Override
    public final boolean hasAttribute(@Nonnull final LSDAttribute key) {
        return lsdProperties.containsProperty(key.getKeyName());
    }

    @Override
    public final boolean hasPermission(@Nonnull final LiquidPermissionScope permissionScope, @Nonnull final LiquidPermission permission) {
        return LiquidPermissionSet.createPermissionSet(getAttribute(LSDAttribute.PERMISSIONS))
                                  .hasPermission(permissionScope, permission);
    }

    @Override
    public final boolean hasSubEntity(@Nonnull final LSDAttribute attribute) {
        final String keyString = attribute.getKeyName();
        return hasSubEntity(keyString);
    }

    @Override
    public final boolean isA(@Nonnull final LSDDictionaryTypes type) {
        return getTypeDef().getPrimaryType().isA(type);
    }

    @Override
    public boolean isA(@Nonnull final LSDTypeDef typeDef) {
        return getTypeDef().equals(typeDef);
    }

    @Override
    public boolean isEmptyValue(@Nonnull final LSDAttribute key) {
        final String value = lsdProperties.get(key.getKeyName());
        return value == null;
    }

    @Override
    public boolean isError() {
        return lsdProperties.get(TYPE_KEY).startsWith("System.Error");
    }

    @Override
    public boolean isNewerThan(@Nonnull final LSDBaseEntity entity) {
        final Date updated = getUpdated();
        return updated.after(entity.getUpdated());
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public void setReadonly(final boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public boolean isSerializable() {
        return lsdProperties.isSerializable();
    }

    @Override
    public void remove(@Nonnull final LSDAttribute key) {
        assertNotReadonly();
        lsdProperties.put(key.getKeyName(), "");
    }

    @Override
    public void removeCompletely(@Nonnull final LSDAttribute attribute) {
        assertNotReadonly();
        lsdProperties.remove(attribute.getKeyName());
    }

    @Override @Nonnull
    public LSDTransferEntity removeSubEntity(@Nonnull final LSDAttribute path) {
        final String keyString = path.getKeyName();
        final LSDTransferEntity subEntity = createEmpty();
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
    public void removeValue(@Nonnull final LSDAttribute id) {
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
    public void setAttribute(@Nonnull final LSDAttribute parent, @Nonnull final LSDAttribute child, final String value) {
        assertNotReadonly();
        setValue(parent.getKeyName() + '.' + child.getKeyName(), value);
    }

    @Override
    public void setAttribute(@Nonnull final LSDAttribute key, @Nonnull final String value) {
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
    }

    @Override
    public void setAttribute(@Nonnull final LSDAttribute checked, final boolean bool) {
        assertNotReadonly();
        setAttribute(checked, bool ? "true" : "false");
    }

    @Override
    public void setAttribute(@Nonnull final LSDAttribute attribute, final long value) {
        assertNotReadonly();
        setAttribute(attribute, String.valueOf(value));
    }

    @Override
    public void setAttribute(@Nonnull final LSDAttribute attribute, @Nonnull final LiquidUUID uuid) {
        assertNotReadonly();
        setAttribute(attribute, uuid.toString());
    }

    @Override
    public void setAttribute(@Nonnull final LSDAttribute attribute, @Nonnull final LiquidURI uri) {
        assertNotReadonly();
        setAttribute(attribute, uri.asString());
    }

    @Override
    public void setAttribute(@Nonnull final LSDAttribute attribute, final double value) {
        assertNotReadonly();
        setAttribute(attribute, String.valueOf(value));
    }

    @Override
    public void setAttribute(@Nonnull final LSDAttribute attribute, @Nonnull final Date value) {
        setAttribute(attribute, value.getTime());
    }

    @Override
    public void setAttributeConditonally(@Nonnull final LSDAttribute key, @Nullable final String value) {
        assertNotReadonly();
        if (value != null) {
            setValue(key.getKeyName(), value);
        }
    }

    @Override
    public void setID(@Nonnull final LiquidUUID id) {
        assertNotReadonly();
        lsdProperties.put(ID_KEY, id.toString().toLowerCase());
    }

    @Override
    public void setId(final String id) {
        assertNotReadonly();
        set(ID_KEY, id);
    }

    @Override
    public final void setURI(@Nonnull final LiquidURI uri) {
        assertNotReadonly();
        setAttribute(LSDAttribute.URI, uri.asString());
    }

    public void setTypeDef(@Nonnull final LSDTypeDef type) {
        lsdTypeDef = type;
        if (type.asString() == null) {
            throw new NullPointerException("Cannot set type def to a null value.");
        }
        lsdProperties.put(TYPE_KEY, type.asString());
    }

    @Override @Nonnull
    public final LSDNode asFormatIndependentTree() {
        final List root = new ArrayList();
        final Map<String, List> values = new HashMap<String, List>();
        for (final String key : lsdProperties.getKeys()) {
            addToStructuredMap(root, key, lsdProperties.get(key));
        }
        return new LSDSimpleNode("root", root);
    }

    @Override @Nonnull
    public final Map<String, String> asMapForPersistence(final boolean ignoreType, final boolean update) {
        final Map<String, String> typedMap = new HashMap<String, String>();
        for (final String key : lsdProperties.getKeys()) {
            final String[] strings = key.split("\\.");
            if (strings.length > 0) {
                final LSDAttribute prefixAttribute = LSDAttribute.valueOf(strings[0]);
                //todo: this should check for all sub-entity prefixes and not assume they are a single word long.
                if (prefixAttribute == null
                    || !prefixAttribute.isSubEntity()
                    || prefixAttribute.includeAttributeInPersistence(ignoreType, update)) {
                    if (prefixAttribute != null && prefixAttribute.isSubEntity()) {
                        typedMap.put(key, lsdProperties.get(key));
                    }
                    else {
                        final LSDAttribute dictionaryKeyName = LSDAttribute.valueOf(key);
                        if (dictionaryKeyName == null) {
                            throw new LSDUnknownAttributeException("Unknown attribute %s", key);
                        }

                        if (dictionaryKeyName.includeAttributeInPersistence(ignoreType, update)) {
                            typedMap.put(key, lsdProperties.get(key));
                        }
                    }
                }
                else {
                    //skip
                    //                    System.err.println("Skipped persisting " + entry.getKey());
                }
            }
        }
        return typedMap;
    }

    @Nonnull @Override
    public final LSDTransferEntity asUpdateEntity() {
        final LSDSimpleEntity newEntity = (LSDSimpleEntity) createNewEntity(getTypeDef(), getUUID());
        newEntity.setURI(getURI());
        return newEntity;
    }

    @Override @Nonnull
    public final LSDTransferEntity copy() {
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
            final LiquidURI uri = getURI();
            if (LiquidBoardURL.isConvertable(uri)) {
                result.put("shortUrl", uri.asShortUrl().asUrlSafe());
            }
        }
        if (hasAttribute(LSDAttribute.SOURCE) && LiquidBoardURL.isConvertable(getURIAttribute(LSDAttribute.SOURCE))) {
            //noinspection ConstantConditions
            result.put("sourceShortUrl", getURIAttribute(LSDAttribute.SOURCE).asShortUrl().asUrlSafe());
        }
        return result;
    }

    @Nonnull @Override
    public final Map<String, String> getMap() {
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
    private LSDTransferEntity getSubEntity(@Nonnull final String keyString, final boolean readonlyEntity) {
        final LSDTransferEntity subEntity = createEmpty();
        for (final String key : lsdProperties.getKeys()) {
            if (key.startsWith(keyString + '.')) {
                final String subEntityKey = key.substring(keyString.length() + 1);
                subEntity.setValue(subEntityKey, lsdProperties.get(key));
            }
        }
        subEntity.setReadonly(readonlyEntity);
        return subEntity;
    }

    private String getValue(final String key, final String defaultValue) {
        final String value = lsdProperties.get(key);
        if (value == null) {
            return defaultValue;
        }
        else {
            return value;
        }
    }
}
