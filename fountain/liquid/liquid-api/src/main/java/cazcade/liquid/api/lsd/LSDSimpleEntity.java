package cazcade.liquid.api.lsd;

import cazcade.liquid.api.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;


/**
 * @author Neil Ellis
 */

public class LSDSimpleEntity implements LSDEntity {
    private static final String ID_KEY = LSDAttribute.ID.getKeyName();
    private static final String TYPE_KEY = LSDAttribute.TYPE.getKeyName();

    @Nonnull
    private final LSDPropertyStore lsdProperties;
    private LSDTypeDef lsdTypeDef;
    private LiquidUUID uuid;
    private boolean readonly;

    public LSDSimpleEntity() {
        super();
        lsdProperties = new LSDMapPropertyStore(new HashMap<String, String>());
    }

    protected LSDSimpleEntity(@Nonnull final LSDPropertyStore store) {
        super();
        lsdProperties = store.copy();
    }

    private LSDSimpleEntity(@Nonnull final Map<String, String> lsdProperties) {
        super();
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

    private void assertNotReadonly() {
        if (readonly) {
            throw new IllegalStateException("Entity is readonly, cannot be mutated.");
        }
    }

    private void parse(@Nonnull final String path, @Nonnull final LSDNode node, final boolean array) {
        final String newPath;
        if (array) {
            newPath = path;
        } else {
            newPath = path.isEmpty() ? node.getName() : path + '.' + node.getName();
        }
        if (node.isLeaf()) {
            if (node.getLeafValue() != null) {
                lsdProperties.put(newPath, node.getLeafValue());
            }
        } else {
            if (node.isArray()) {
                final List<LSDNode> children = node.getChildren();
                int count = 0;
                for (final LSDNode child : children) {
                    if (count == 0 && child.isLeaf()) {
                        parse(newPath, child, true);
                    } else {
                        parse(newPath + '.' + (child.isLeaf() ? count : count + 1), child, true);
                    }
                    count++;
                }
            } else {
                final List<LSDNode> lsdNodeList = node.getChildren();
                for (final LSDNode child : lsdNodeList) {
                    parse(newPath, child, false);
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

    @Override
    @Nonnull
    public LSDEntity getSubEntity(@Nonnull final LSDAttribute path, final boolean readonlyEntity) {
        final String keyString = path.getKeyName();
        return getSubEntity(keyString, readonlyEntity);
    }

    @Nonnull
    private LSDEntity getSubEntity(@Nonnull final String keyString, final boolean readonlyEntity) {
        final LSDEntity subEntity = createEmpty();
        for (final String key : lsdProperties.getProperties()) {
            if (key.startsWith(keyString + '.')) {
                final String subEntityKey = key.substring(keyString.length() + 1);
                subEntity.setValue(subEntityKey, lsdProperties.get(key));
            }

        }
        subEntity.setReadonly(readonlyEntity);
        return subEntity;
    }

    @Override
    @Nonnull
    public LSDEntity removeSubEntity(@Nonnull final LSDAttribute path) {
        final String keyString = path.getKeyName();
        final LSDSimpleEntity subEntity = createEmpty();
        final Collection<String> toDelete = new ArrayList<String>();
        for (final String key : lsdProperties.getProperties()) {
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
    public LiquidUUID getUUID() {
        initUUID();
        return uuid;
    }

    @Override
    @Nullable
    public LiquidURI getURI() {
        final String uri = lsdProperties.get(LSDAttribute.URI.getKeyName());
        if (uri == null) {
            return null;
        }
        return new LiquidURI(uri);
    }

    @Override
    public void setURI(@Nonnull final LiquidURI uri) {
        assertNotReadonly();
        setAttribute(LSDAttribute.URI, uri.asString());
    }

    @Override
    public Long getLongAttribute(@Nonnull final LSDAttribute attribute) {
        return Long.valueOf(getAttribute(attribute));
    }

    @Override
    public void setAttribute(final LSDAttribute attribute, final long value) {
        assertNotReadonly();
        setAttribute(attribute, String.valueOf(value));
    }

    @Override
    public Integer getIntegerAttribute(@Nonnull final LSDAttribute attribute) {
        return Integer.valueOf(getAttribute(attribute));
    }

    @Nullable
    @Override
    public LiquidUUID getUUIDAttribute(@Nonnull final LSDAttribute attribute) {
        final String result = getAttribute(attribute);
        if (result != null && !result.isEmpty()) {
            return LiquidUUID.fromString(result);
        } else {
            return null;
        }
    }

    @Override
    public void setAttribute(final LSDAttribute attribute, @Nonnull final LiquidUUID uuid) {
        assertNotReadonly();
        setAttribute(attribute, uuid.toString());
    }

    @Nullable
    @Override
    public LiquidURI getURIAttribute(@Nonnull final LSDAttribute attribute) {
        final String value = getAttribute(attribute);
        if (value != null && !value.isEmpty()) {
            return new LiquidURI(value);
        } else {
            return null;
        }
    }

    @Override
    public void setAttribute(final LSDAttribute attribute, @Nonnull final LiquidURI uri) {
        assertNotReadonly();
        setAttribute(attribute, uri.asString());
    }

    @Override
    public Double getDoubleAttribute(@Nonnull final LSDAttribute attribute) {
        return Double.valueOf(getAttribute(attribute));
    }

    @Override
    public void setAttribute(final LSDAttribute attribute, final double value) {
        assertNotReadonly();
        setAttribute(attribute, String.valueOf(value));
    }

    @Override
    public int getIntegerAttribute(@Nonnull final LSDAttribute attribute, final int defaultValue) {
        return Integer.parseInt(getAttribute(attribute, String.valueOf(defaultValue)));
    }

    @Override
    public boolean hasSubEntity(@Nonnull final LSDAttribute attribute) {
        final String keyString = attribute.getKeyName();
        return hasSubEntity(keyString);
    }

    private boolean hasSubEntity(final String keyString) {
        for (final String key : lsdProperties.getProperties()) {
            if (key.startsWith(keyString + '.')) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean getBooleanAttribute(@Nonnull final LSDAttribute attribute, final boolean defaultValue) {
        if (!hasAttribute(attribute)) {
            return defaultValue;
        }
        final String value = getAttribute(attribute);
        if (value != null && !value.isEmpty()) {
            return "true".equals(value);
        } else {
            return defaultValue;
        }

    }

    @Override
    public void removeCompletely(@Nonnull final LSDAttribute attribute) {
        assertNotReadonly();
        lsdProperties.remove(attribute.getKeyName());
    }

    @Override
    public void setReadonly(final boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public void setAttribute(@Nonnull final LSDAttribute parent, @Nonnull final LSDAttribute child, final String value) {
        assertNotReadonly();
        setValue(parent.getKeyName() + '.' + child.getKeyName(), value);
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public String getSubAttribute(@Nonnull final LSDAttribute attribute, @Nonnull final LSDAttribute subAttribute, final String defaultValue) {
        return getValue(attribute.getKeyName() + '.' + subAttribute.getKeyName(), defaultValue);
    }

    @Override
    public void setUpdated(@Nonnull final Date updated) {
        setAttribute(LSDAttribute.UPDATED, updated);
    }

    @Override
    public void setAttribute(final LSDAttribute attribute, @Nonnull final Date value) {
        setAttribute(attribute, value.getTime());
    }

    @Override
    public void setPublished(@Nonnull final Date published) {
        setAttribute(LSDAttribute.PUBLISHED, published);
    }

    @Override
    public void copyAttribute(@Nonnull final LSDEntity entity, final LSDAttribute attribute) {
        setAttribute(attribute, entity.getAttribute(attribute));
    }

    private String getValue(final String key, final String defaultValue) {
        final String value = lsdProperties.get(key);
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }


    @Override
    public LSDTypeDef getTypeDef() {
        initTypeDef();
        return lsdTypeDef;
    }

    @Override
    @Nonnull
    public List<LSDEntity> getSubEntities(@Nonnull final LSDAttribute key) {
        final String keyString = key.getKeyName();
        final TreeMap<Integer, LSDSimpleEntity> entities = new TreeMap<Integer, LSDSimpleEntity>();
        for (final String property : lsdProperties.getProperties()) {
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
                LSDSimpleEntity subEntity = entities.get(subEntityNumber);
                if (subEntity == null) {
                    subEntity = createEmpty();
                    entities.put(subEntityNumber, subEntity);
                }
                subEntity.setValue(subEntityKey, lsdProperties.get(property));
            }

        }
        return new ArrayList<LSDEntity>(entities.values());
    }

    @Nonnull
    @Override
    public Map<String, String> getMap() {
        return lsdProperties.asMap();
    }

    @Nonnull
    @Override
    public Map<String, String> getCamelCaseMap() {
        final Map<String, String> result = new HashMap<String, String>();
        for (final String key : lsdProperties.getProperties()) {
            result.put(convertToCamel(key), lsdProperties.get(key));
        }
        final LiquidURI uri = getURI();
        if (uri != null && LiquidBoardURL.isConvertable(uri)) {
            result.put("shortUrl", uri.asShortUrl().asUrlSafe());
        }
        if (hasAttribute(LSDAttribute.SOURCE) && LiquidBoardURL.isConvertable(getURIAttribute(LSDAttribute.SOURCE))) {
            //noinspection ConstantConditions
            result.put("sourceShortUrl", getURIAttribute(LSDAttribute.SOURCE).asShortUrl().asUrlSafe());
        }
        return result;
    }

    @Override
    @Nonnull
    public Map<String, String> asMapForPersistence(final boolean ignoreType, final boolean update) {
        final Map<String, String> typedMap = new HashMap<String, String>();
        for (final String key : lsdProperties.getProperties()) {
            final String[] strings = key.split("\\.");
            if (strings.length > 0) {
                final LSDAttribute prefixAttribute = LSDAttribute.valueOf(strings[0]);
                //todo: this should check for all sub-entity prefixes and not assume they are a single word long.
                if (prefixAttribute == null || !prefixAttribute.isSubEntity() || prefixAttribute.includeAttributeInPersistence(ignoreType, update)) {
                    if (prefixAttribute != null && prefixAttribute.isSubEntity()) {
                        typedMap.put(key, lsdProperties.get(key));
                    } else {
                        final LSDAttribute dictionaryKeyName = LSDAttribute.valueOf(key);
                        if (dictionaryKeyName == null) {
                            throw new LSDUnknownAttributeException("Unknown attribute %s", key);
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

    @Override
    @Nonnull
    public LSDNode asFormatIndependentTree() {
        final List root = new ArrayList();
        final Map<String, List> values = new HashMap<String, List>();
        for (final String key : lsdProperties.getProperties()) {
            addToStructuredMap(root, key, lsdProperties.get(key));
        }
        return new LSDSimpleNode("root", root);
    }

    @Override
    public String getAttribute(@Nonnull final LSDAttribute attribute) {
        final String value = lsdProperties.get(attribute.getKeyName());
//        if (value != null && !key.isValidFormat(FORMAT_VALIDATOR, (value))) {
//            throw new IllegalArgumentException("The value in this LSDEntity for " + key.name() + " was " + value + " which is invalid according to the dictionary.");
//        }
        return value;
    }

    @Nullable
    @Override
    public LiquidURI getAttributeAsURI(@Nonnull final LSDAttribute attribute) {
        if (hasAttribute(attribute)) {
            return new LiquidURI(getAttribute(attribute));
        } else {
            return null;
        }
    }

    @Override
    public String getRawValue(@Nonnull final LSDAttribute key) {
        return lsdProperties.get(key.getKeyName());
    }

    @Override
    public boolean isError() {
        return lsdProperties.get(TYPE_KEY).startsWith("System.Error");
    }

    @Override
    public boolean isEmptyValue(@Nonnull final LSDAttribute key) {
        final String value = lsdProperties.get(key.getKeyName());
        return value == null;
    }

//    public boolean isValidOrEmptyValue(LSDDictionary key) {
//        String value = lsdProperties.get(key.getKeyName());
////        return value == null || key.isValidFormat(FORMAT_VALIDATOR, value);
//    }

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
    public String getValue(final String key) {
        return lsdProperties.get(key);
    }

    @Override
    public void setAttribute(@Nullable final LSDAttribute key, @Nullable final String value) {
        assertNotReadonly();
        if (key == null) {
            throw new IllegalArgumentException("Cannot set a value for a null key.");
        }
        if (value == null) {
            throw new NullPointerException("Cannot set an attribute to a null value, only an empty string.");
        }
        setValue(key.getKeyName(), value);
    }

    @Override
    public void setAttributeConditonally(@Nonnull final LSDAttribute key, @Nullable final String value) {
        assertNotReadonly();
        if (value != null) {
            setValue(key.getKeyName(), value);
        }
    }

    @Override
    public boolean attributeIs(@Nonnull final LSDAttribute attribute, final String comparison) {
        return hasAttribute(attribute) && getAttribute(attribute).equals(comparison);
    }

    @Nonnull
    @Override
    public List<String> getAttributeAsList(@Nonnull final LSDAttribute attribute) {
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

    @Nullable
    @Override
    public Date getUpdated() {
        if (hasAttribute(LSDAttribute.UPDATED)) {
            return new Date(Long.parseLong(lsdProperties.get(LSDAttribute.UPDATED.getKeyName())));
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public Date getPublished() {
        if (hasAttribute(LSDAttribute.PUBLISHED)) {
            return new Date(Long.parseLong(lsdProperties.get(LSDAttribute.PUBLISHED.getKeyName())));
        } else {
            return null;
        }
    }

    @Override
    public void setValues(@Nonnull final LSDAttribute key, @Nonnull final List values) {
        assertNotReadonly();
        for (final String property : lsdProperties.getProperties()) {
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
                setAttribute(key, value.toString());
            }
            count++;
        }
    }

    @Override
    public void remove(@Nonnull final LSDAttribute key) {
        assertNotReadonly();
        lsdProperties.put(key.getKeyName(), "");
    }

    @Override
    public void removeValue(@Nonnull final LSDAttribute id) {
        assertNotReadonly();
        lsdProperties.remove(id.toString());
    }

    @Override
    public boolean getBooleanAttribute(@Nonnull final LSDAttribute attribute) {
        final String value = getAttribute(attribute);
        return "true".equals(value);
    }

    @Override
    public void setAttribute(final LSDAttribute checked, final boolean bool) {
        assertNotReadonly();
        setAttribute(checked, bool ? "true" : "false");
    }

    @Override
    public boolean hasPermission(@Nonnull final LiquidPermissionScope permissionScope, @Nonnull final LiquidPermission permission) {
        return LiquidPermissionSet.createPermissionSet(getAttribute(LSDAttribute.PERMISSIONS)).hasPermission(permissionScope, permission);
    }

    @Override
    public Object get(@Nonnull final String key) {
        final String dotStyleKey = convertFromCamel(key);
        if (hasSubEntity(dotStyleKey)) {
            return getSubEntity(dotStyleKey, true);
        } else {
            return lsdProperties.get(dotStyleKey);
        }
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
    public String getAttribute(@Nonnull final LSDAttribute attribute, final String defaultValue) {
        final String result = getAttribute(attribute);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    @Override
    public void setId(final String id) {
        assertNotReadonly();
        set(ID_KEY, id);
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

    @Override
    public boolean hasAttribute(@Nonnull final LSDAttribute key) {
        return lsdProperties.containsProperty(key.getKeyName());
    }

    @Override
    public void addAnonymousSubEntity(@Nonnull final LSDAttribute stem, @Nonnull final LSDEntity entity) {
        assertNotReadonly();
        final Map<String, String> map = entity.getMap();
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            setValue(stem.getKeyName() + '.' + entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void addSubEntity(@Nonnull final LSDAttribute stem, @Nullable final LSDEntity entity, final boolean requiresId) {
        assertNotReadonly();
        if (entity == null) {
            throw new NullPointerException("Attempted to add a null sub entity using stem " + stem);
        }
        if (!stem.isSubEntity()) {
            throw new IllegalArgumentException("Cannot add a sub entity to a non sub entity property '" + stem.getKeyName() + "'.");
        }
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


    @Override
    public void addSubEntities(@Nonnull final LSDAttribute stem, @Nonnull final Collection<LSDEntity> entities) {
        assertNotReadonly();
        if (!stem.isSubEntity()) {
            throw new IllegalArgumentException("Cannot add a sub entity to a non sub entity property '" + stem.getKeyName() + "'.");
        }
        int count = 1;
        for (final LSDEntity entity : entities) {
            if (entity.getUUID() == null) {
                throw new IllegalArgumentException("Attempted to add a sub entity which had no id.");
            }
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
    public String dump() {
        final StringBuilder buffer = new StringBuilder();
        for (final String property : lsdProperties.getProperties()) {
            buffer.append(property).append('=').append(lsdProperties.get(property)).append("\n");
        }
        return buffer.toString();
    }

    @Override
    public boolean canBe(final LSDDictionaryTypes type) {
        return getTypeDef().canBe(type);
    }

    @Override
    public boolean isA(final LSDDictionaryTypes type) {
        return getTypeDef() != null && getTypeDef().getPrimaryType().isA(type);
    }

    @Override
    public boolean isA(final LSDTypeDef typeDef) {
        return getTypeDef().equals(typeDef);
    }

    @Override
    @Nonnull
    public LSDEntity copy() {
        return createFromProperties(lsdProperties.asMap());
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

    @SuppressWarnings({"unchecked"})
    @Nonnull
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
                    throw new IllegalArgumentException("Can't mix content and nodes, did you set an x.y='a' value then x.y.z='b'? The property was " + prop);
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

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder("{\n");
        for (final String property : lsdProperties.getProperties()) {
            buffer.append('{').append(property).append('=').append('\'').append(lsdProperties.get(property)).append('\'').append('}').append('\n');
        }
        buffer.append("}\n");
        return buffer.toString();
    }


    @Override
    public void setType(@Nonnull final LSDDictionaryTypes type) {
        assertNotReadonly();
        if (type.getValue() == null) {
            throw new NullPointerException("Cannot set type to a null value.");
        }

        lsdProperties.put(TYPE_KEY, type.getValue());
    }

    @Override
    public void setID(@Nonnull final LiquidUUID id) {
        assertNotReadonly();
        lsdProperties.put(ID_KEY, id.toString().toLowerCase());
    }

    @Override
    public void timestamp() {
        lsdProperties.put(LSDAttribute.UPDATED.getKeyName(), String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        final LiquidURI uri = getURI();
        return this == o || !(o == null || getClass() != o.getClass()) && uri != null && uri.equals(((LSDEntity) o).getURI());
    }

    @Override
    public int hashCode() {
        final LiquidURI uri = getURI();
        return uri != null ? uri.hashCode() : 0;
    }

    @Nonnull
    public static LSDSimpleEntity createEmpty() {
        final LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        return entity;
    }


    @Nonnull
    public static LSDSimpleEntity createNewEntity(@Nonnull final LSDDictionaryTypes type, @Nonnull final LiquidUUID uuid) {
        final LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setType(type);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.ID, uuid.toString());
        return entity;
    }

    @Nonnull
    public static LSDSimpleEntity createNewEntity(@Nonnull final LSDDictionaryTypes type) {
        final LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setType(type);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        return entity;
    }

    @Nonnull
    public static LSDSimpleEntity createNewEntity(@Nonnull final LSDTypeDef type, @Nonnull final LiquidUUID uuid) {
        final LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setTypeDef(type);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.ID, uuid.toString());
        return entity;
    }

    @Nonnull
    public static LSDSimpleEntity createNewEntity(@Nonnull final LSDTypeDef type) {
        final LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setTypeDef(type);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        return entity;
    }


    @Override
    public void setTypeDef(@Nonnull final LSDTypeDef type) {
        lsdTypeDef = type;
        if (type.asString() == null) {
            throw new NullPointerException("Cannot set type def to a null value.");
        }
        lsdProperties.put(TYPE_KEY, type.asString());
    }

    @Override
    public String asFreeText() {
        final StringBuilder s = new StringBuilder();
        for (final String value : lsdProperties.valueIterator()) {
            s.append(value);
            s.append(' ');
        }
        return s.toString();
    }

    @Override
    public boolean isNewerThan(@Nonnull final LSDEntity entity) {
        final Date updated = getUpdated();
        return updated != null && updated.after(entity.getUpdated());
    }

    @Override
    public boolean wasPublishedAfter(@Nonnull final LSDEntity entity) {
        final Date published = getPublished();
        return published != null && published.after(entity.getPublished());
    }

    @Nonnull
    @Override
    public LSDEntity asUpdateEntity() {
        final LSDSimpleEntity newEntity = createNewEntity(getTypeDef(), getUUID());
        final LiquidURI uri = getURI();
        if (uri == null) {
            throw new NullPointerException("Attempted to user a null uri in 'asUpdateEntity' in LSDSimpleEntity.");
        }
        newEntity.setURI(uri);
        return newEntity;
    }

    @Override
    public String getEURI() {
        return getAttribute(LSDAttribute.EURI);
    }


    @Nonnull
    public static LSDSimpleEntity createFromProperties(@Nonnull final Map<String, String> lsdProperties) {
        return new LSDSimpleEntity(lsdProperties);
    }

    @Nonnull
    public static LSDSimpleEntity createFromNode(@Nonnull final LSDNode lsdNode) {
        return new LSDSimpleEntity(lsdNode);
    }
}
