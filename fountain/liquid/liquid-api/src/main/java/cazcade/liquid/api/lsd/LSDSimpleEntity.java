package cazcade.liquid.api.lsd;

import cazcade.liquid.api.*;

import java.util.*;


/**
 * @author Neil Ellis
 */

public class LSDSimpleEntity implements LSDEntity {
    private static final String ID_KEY = LSDAttribute.ID.getKeyName();
    private static final String TYPE_KEY = LSDAttribute.TYPE.getKeyName();

    private TreeMap<String, String> lsdProperties = new TreeMap<String, String>();
    private LSDTypeDef lsdTypeDef;
    private LiquidUUID uuid;

    private LSDSimpleEntity() {
    }

    private LSDSimpleEntity(Map<String, String> lsdProperties) {
        for (Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            if (entry.getValue() == null) {
                throw new NullPointerException("Tried to set the value of " + entry.getKey() + " to null.");
            }
        }
        this.lsdProperties.putAll(lsdProperties);
        initTypeDef();
        initUUID();
    }

    private LSDSimpleEntity(LSDNode lsdNode) {
        String path = "";
        List<LSDNode> children = lsdNode.getChildren();
        for (LSDNode child : children) {
            parse("", child, false);
        }


    }

    private void parse(String path, LSDNode node, boolean array) {
        String newPath;
        if (array) {
            newPath = path;
        } else {
            newPath = path.isEmpty() ? node.getName() : path + "." + node.getName();
        }
        if (node.isLeaf()) {
            if (node.getLeafValue() != null) {
                lsdProperties.put(newPath, node.getLeafValue());
            }
        } else {
            if (node.isArray()) {
                List<LSDNode> children = node.getChildren();
                int count = 0;
                for (LSDNode child : children) {
                    if (count == 0 && child.isLeaf()) {
                        parse(newPath, child, true);
                    } else {
                        parse(newPath + "." + (child.isLeaf() ? count : count + 1), child, true);
                    }
                    count++;
                }
            } else {
                List<LSDNode> lsdNodeList = node.getChildren();
                for (LSDNode child : lsdNodeList) {
                    parse(newPath, child, false);
                }
            }
        }
    }

    private void initUUID() {
        if (uuid == null) {
            String id = lsdProperties.get(ID_KEY);
            if (id != null) {
                uuid = LiquidUUID.fromString(id);
            }
        }
    }

    private synchronized void initTypeDef() {
        if (lsdTypeDef == null) {
            String typeValue = this.lsdProperties.get(TYPE_KEY);
            if (typeValue != null) {
                lsdTypeDef = new LSDTypeDefImpl(typeValue);
            }
        }
    }

    public LSDEntity getSubEntity(LSDAttribute path) {
        final String keyString = path.getKeyName();
        return getSubEntity(keyString);
    }

    private LSDEntity getSubEntity(String keyString) {
        LSDSimpleEntity subEntity = createEmpty();
        for (Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            if (entry.getKey().startsWith(keyString + ".")) {
                String subEntityKey = entry.getKey().substring(keyString.length() + 1);
                subEntity.setValue(subEntityKey, entry.getValue());
            }

        }
        return subEntity;
    }

    public LSDEntity removeSubEntity(LSDAttribute path) {
        final String keyString = path.getKeyName();
        LSDSimpleEntity subEntity = createEmpty();
        List<String> toDelete = new ArrayList<String>();
        for (Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            if (entry.getKey().startsWith(keyString + ".")) {
                String subEntityKey = entry.getKey().substring(keyString.length() + 1);
                subEntity.setValue(subEntityKey, entry.getValue());
                toDelete.add(entry.getKey());
            }

        }
        for (String key : toDelete) {
            lsdProperties.remove(key);

        }
        return subEntity;
    }

    public LiquidUUID getID() {
        initUUID();
        return uuid;
    }

    public LiquidURI getURI() {
        final String uri = lsdProperties.get(LSDAttribute.URI.getKeyName());
        if (uri == null) {
            return null;
        }
        return new LiquidURI(uri);
    }

    public void setURI(LiquidURI uri) {
        setAttribute(LSDAttribute.URI, uri.asString());
    }

    @Override
    public Long getLongAttribute(LSDAttribute attribute) {
        return Long.valueOf(getAttribute(attribute));
    }

    @Override
    public void setAttribute(LSDAttribute attribute, long value) {
        setAttribute(attribute, String.valueOf(value));
    }

    @Override
    public Integer getIntegerAttribute(LSDAttribute attribute) {
        return Integer.valueOf(getAttribute(attribute));
    }

    @Override
    public LiquidUUID getUUIDAttribute(LSDAttribute attribute) {
        final String result = getAttribute(attribute);
        if (result != null && !result.isEmpty()) {
            return LiquidUUID.fromString(result);
        } else {
            return null;
        }
    }

    @Override
    public void setAttribute(LSDAttribute attribute, LiquidUUID uuid) {
        setAttribute(attribute, uuid.toString());
    }

    @Override
    public LiquidURI getURIAttribute(LSDAttribute attribute) {
        final String value = getAttribute(attribute);
        if (value != null && !value.isEmpty()) {
            return new LiquidURI(value);
        } else {
            return null;
        }
    }

    @Override
    public void setAttribute(LSDAttribute attribute, LiquidURI uri) {
        setAttribute(attribute, uri.asString());
    }

    @Override
    public Double getDoubleAttribute(LSDAttribute attribute) {
        return Double.valueOf(getAttribute(attribute));
    }

    @Override
    public void setAttribute(LSDAttribute attribute, double value) {
        setAttribute(attribute, String.valueOf(value));
    }

    @Override
    public int getIntegerAttribute(LSDAttribute attribute, int defaultValue) {
        return Integer.parseInt(getAttribute(attribute, String.valueOf(defaultValue)));
    }

    @Override
    public boolean hasSubEntity(LSDAttribute attribute) {
        String keyString = attribute.getKeyName();
        for (Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            if (entry.getKey().startsWith(keyString + ".")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean getBooleanAttribute(LSDAttribute attribute, boolean defaultValue) {
        final String value = getAttribute(attribute);
        if (value != null && !value.isEmpty()) {
            return "true".equals(value);
        } else {
            return defaultValue;
        }

    }

    @Override
    public void removeCompletely(LSDAttribute attribute) {
        lsdProperties.remove(attribute.getKeyName());
    }


    public LSDTypeDef getTypeDef() {
        initTypeDef();
        return lsdTypeDef;
    }

    public List<LSDEntity> getSubEntities(LSDAttribute key) {
        final String keyString = key.getKeyName();
        TreeMap<Integer, LSDSimpleEntity> entities = new TreeMap<Integer, LSDSimpleEntity>();
        for (Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            if (entry.getKey().startsWith(keyString + ".")) {
                String subEntityKeyFull = entry.getKey().substring(keyString.length() + 1);
                int firstDot = subEntityKeyFull.indexOf('.');
                Integer subEntityNumber;
                String subEntityKey;
                if (firstDot < 0) {
                    subEntityNumber = 0;
                    subEntityKey = subEntityKeyFull;
                } else {
                    String subEntityString = subEntityKeyFull.substring(0, firstDot);
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
                subEntity.setValue(subEntityKey, entry.getValue());
            }

        }
        return new ArrayList<LSDEntity>(entities.values());
    }

    public Map<String, String> getMap() {
        return lsdProperties;
    }

    @Override
    public Map<String, String> getCamelCaseMap() {
        final HashMap<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            result.put(convertToCamel(entry.getKey()), entry.getValue());
        }
        return result;
    }

    public Map<String, String> asMapForPersistence(boolean ignoreType, boolean update) {
        Map<String, String> typedMap = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            String[] strings = entry.getKey().split("\\.");
            if (strings.length > 0) {
                LSDAttribute prefixAttribute = LSDAttribute.valueOf(strings[0]);
                //todo: this should check for all sub-entity prefixes and not assume they are a single word long.
                if (prefixAttribute == null || !prefixAttribute.isSubEntity() || includeAttributeInPersistence(ignoreType, update, prefixAttribute)) {
                    if (prefixAttribute != null && prefixAttribute.isSubEntity()) {
                        typedMap.put(entry.getKey(), entry.getValue());
                    } else {
                        LSDAttribute dictionaryKeyName = LSDAttribute.valueOf(entry.getKey());
                        if (dictionaryKeyName == null) {
                            throw new LSDUnknownAttributeException("Unknown attribute %s", entry.getKey());
                        }

                        if (includeAttributeInPersistence(ignoreType, update, dictionaryKeyName)) {
                            typedMap.put(entry.getKey(), entry.getValue());
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

    private boolean includeAttributeInPersistence(boolean ignoreType, boolean update, LSDAttribute dictionaryKeyName) {
        return (dictionaryKeyName.isPersistable() || dictionaryKeyName.isTransient())
                && (dictionaryKeyName.isUpdateable() || !update)
                && !dictionaryKeyName.isSystemGenerated()
                && (dictionaryKeyName != LSDAttribute.TYPE || !ignoreType);
    }

    public LSDNode asFormatIndependentTree() {
        List root = new ArrayList();
        Map<String, List> values = new HashMap<String, List>();
        for (Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            addToStructuredMap(root, entry.getKey(), entry.getValue());
        }
        return new LSDSimpleNode("root", root);
    }

    public String getAttribute(LSDAttribute attribute) {
        String value = lsdProperties.get(attribute.getKeyName());
//        if (value != null && !key.isValidFormat(FORMAT_VALIDATOR, (value))) {
//            throw new IllegalArgumentException("The value in this LSDEntity for " + key.name() + " was " + value + " which is invalid according to the dictionary.");
//        }
        return value;
    }

    @Override
    public LiquidURI getAttributeAsURI(LSDAttribute attribute) {
        if (hasAttribute(attribute)) {
            return new LiquidURI(getAttribute(attribute));
        } else {
            return null;
        }
    }

    public String getRawValue(LSDAttribute key) {
        return lsdProperties.get(key.getKeyName());
    }

    public boolean isError() {
        return lsdProperties.get(TYPE_KEY).startsWith("System.Error");
    }

    public boolean isEmptyValue(LSDAttribute key) {
        String value = lsdProperties.get(key.getKeyName());
        return value == null;
    }

//    public boolean isValidOrEmptyValue(LSDDictionary key) {
//        String value = lsdProperties.get(key.getKeyName());
////        return value == null || key.isValidFormat(FORMAT_VALIDATOR, value);
//    }

    public void setValue(String key, String value) {
        if (value == null) {
            throw new NullPointerException("The value for key '" + key + "' was null.");
        }
        if (!key.matches("[a-zA-Z0-9]+[a-zA-Z0-9\\._]*[a-zA-Z0-9_]*?")) {
            throw new IllegalArgumentException("Invalid key name " + key);
        }
//        System.err.println("Setting " + key + "=" + value);
        lsdProperties.put(key, value);
    }

    public void setAttribute(LSDAttribute key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("Cannot set a value for a null key.");
        }
        if (value == null) {
            throw new NullPointerException("Cannot set an attribute to a null value, only an empty string.");
        }
        setValue(key.getKeyName(), value);
    }

    public void setAttributeConditonally(LSDAttribute key, String value) {
        if (value != null) {
            setValue(key.getKeyName(), value);
        }
    }

    public boolean attributeIs(LSDAttribute attribute, String comparison) {
        return hasAttribute(attribute) && getAttribute(attribute).equals(comparison);
    }

    @Override
    public List<String> getAttributeAsList(LSDAttribute attribute) {
        List<String> values = new ArrayList<String>();
        if (hasAttribute(attribute)) {
            values.add(getAttribute(attribute));
        }
        int count = 1;
        while (lsdProperties.containsKey(attribute.getKeyName() + "." + count)) {
            values.add(lsdProperties.get(attribute.getKeyName() + "." + count));
            count++;
        }
        return values;
    }

    @Override
    public Date getUpdated() {
        if (hasAttribute(LSDAttribute.UPDATED)) {
            return new Date(Long.parseLong(lsdProperties.get(LSDAttribute.UPDATED.getKeyName())));
        } else {
            return null;
        }
    }

    @Override
    public Date getPublished() {
        if (hasAttribute(LSDAttribute.PUBLISHED)) {
            return new Date(Long.parseLong(lsdProperties.get(LSDAttribute.PUBLISHED.getKeyName())));
        } else {
            return null;
        }
    }

    public void setValues(LSDAttribute key, List values) {
        for (Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            if (entry.getKey().startsWith(key.getKeyName() + ".")) {
                entry.setValue("");
            }
        }
        int count = 0;
        for (Object value : values) {
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
                setValue(key.getKeyName() + "." + count, value.toString());
            } else {
                setAttribute(key, value.toString());
            }
            count++;
        }
    }

    public void remove(LSDAttribute key) {
        lsdProperties.put(key.getKeyName(), "");
    }

    @Override
    public void removeValue(LSDAttribute id) {
        lsdProperties.remove(id.toString());
    }

    @Override
    public boolean getBooleanAttribute(LSDAttribute attribute) {
        final String value = getAttribute(attribute);
        return "true".equals(value);
    }

    @Override
    public void setAttribute(LSDAttribute checked, boolean bool) {
        setAttribute(checked, bool ? "true" : "false");
    }

    @Override
    public boolean hasPermission(LiquidPermissionScope permissionScope, LiquidPermission permission) {
        return LiquidPermissionSet.createPermissionSet(getAttribute(LSDAttribute.PERMISSIONS)).hasPermission(permissionScope, permission);
    }

    @Override
    public Object get(String key) {
        String dotStyleKey = convertFromCamel(key);
        LSDEntity subEntity = getSubEntity(dotStyleKey);
        if (subEntity == null) {
            return lsdProperties.get(dotStyleKey);
        } else {
            return subEntity;
        }
    }

    @Override
    public Object set(String key, String value) {
        if (value == null) {
            throw new NullPointerException("Cannot set " + key + " to a null value.");
        }
        return lsdProperties.put(convertFromCamel(key), value);
    }

    @Override
    public String getAttribute(LSDAttribute attribute, String defaultValue) {
        String result = getAttribute(attribute);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    @Override
    public void setId(String id) {
        set(ID_KEY, id);
    }

    private String convertFromCamel(String key) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (Character.isLowerCase(c)) {
                builder.append(c);
            } else {
                builder.append('.').append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

    private String convertToCamel(String key) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (c != '.') {
                builder.append(c);
            } else {
                builder.append(Character.toUpperCase(key.charAt(++i)));
            }
        }
        return builder.toString();
    }

    public boolean hasAttribute(LSDAttribute key) {
        return lsdProperties.containsKey(key.getKeyName());
    }

    @Override
    public void addAnonymousSubEntity(LSDAttribute stem, LSDEntity entity) {
        Map<String, String> map = entity.getMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            setValue(stem.getKeyName() + "." + entry.getKey(), entry.getValue());
        }
    }

    public void addSubEntity(LSDAttribute stem, LSDEntity entity, boolean requiresId) {
        if (!stem.isSubEntity()) {
            throw new IllegalArgumentException("Cannot add a sub entity to a non sub entity property '" + stem.getKeyName() + "'.");
        }
        if (requiresId && entity.getID() == null) {
            throw new IllegalArgumentException("Attempted to add a sub entity which had no id.");
        }
        String stemKey = stem.getKeyName();
        String existingId = lsdProperties.get(stemKey + ".id");
        if (existingId != null && !existingId.equals(entity.getID().toString())) {
            throw new IllegalArgumentException("Attempted to add a sub entity to an entity which already has a different sub object.");
        }
        addAnonymousSubEntity(stem, entity);

    }


    public void addSubEntities(LSDAttribute stem, Collection<LSDEntity> entities) {
        if (!stem.isSubEntity()) {
            throw new IllegalArgumentException("Cannot add a sub entity to a non sub entity property '" + stem.getKeyName() + "'.");
        }
        int count = 1;
        for (LSDEntity entity : entities) {
            if (entity.getID() == null) {
                throw new IllegalArgumentException("Attempted to add a sub entity which had no id.");
            }
            String stemKey = stem.getKeyName();
            String existingId = lsdProperties.get(stemKey + "." + count + ".id");
            if (existingId != null && !existingId.equals(entity.getID().toString())) {
                throw new IllegalArgumentException("Attempted to add a sub entity to an entity which already has a different sub object.");
            }
            Map<String, String> map = entity.getMap();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                setValue(stemKey + "." + count + "." + entry.getKey(), entry.getValue());
            }
            count++;
        }
    }

    public String dump() {
        StringBuffer buffer = new StringBuffer();
        for (Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            buffer.append(entry.getKey()).append('=').append(entry.getValue()).append("\n");
        }
        return buffer.toString();
    }

    public boolean canBe(LSDDictionaryTypes type) {
        return getTypeDef().canBe(type);
    }

    public boolean isA(LSDDictionaryTypes type) {
        return getTypeDef() != null && getTypeDef().getPrimaryType().isA(type);
    }

    public LSDEntity copy() {
        return createFromProperties(lsdProperties);
    }

    //I can never figure our how this works, truly it drives me potty :-)
    private void addToStructuredMap(List list, String key, String value) {
        final String[] props = key.split("\\.");
        List currentList = list;
        List<List> listStack = new ArrayList<List>();
        int count = 0;
        for (int i = 0; i < props.length; i++) {
            String prop = props[i];
            if (prop.matches("[0-9]+")) {
                //Numeric so we're creating an array.
                if (i == props.length - 1) {
//                    final ArrayList arrayList = new ArrayList();
//                    final Map<String, ArrayList> map = Collections.singletonMap(key, arrayList);
//                    currentList.add(map);
//                    currentList = arrayList;
                    currentList = addToArray(Integer.parseInt(prop), currentList, props[i - 1], value);
                    return;
                } else {
                    currentList = addToArray(Integer.parseInt(prop) - 1, currentList, props[++i], null);
                }
            } else {
                currentList = addToArray(0, currentList, prop, null);
            }

        }
        currentList.add(value);
    }

    private List addToArray(int arrayPos, List currentList, String prop, String value) {
//        if (currentList.size() < arrayPos) {
//            throw new IllegalStateException("Missing array position for " + prop + " have you missed an entry or more before " + (arrayPos + 1) + " current size is " + currentList.size());
//        }
        int oldSize = currentList.size();
        if (oldSize <= arrayPos) {
            for (int i = 0; i <= (arrayPos - oldSize); i++) {
                currentList.add(new HashMap<String, List>());
            }
        }
        if ((currentList.get(arrayPos) instanceof String) && value != null) {
            ArrayList newList = new ArrayList();
            final HashMap<String, List> newMap = new HashMap<String, List>();
            newMap.put(prop, newList);
            newList.add(currentList.get(arrayPos));
            newList.add(value);
            currentList.set(arrayPos, newMap);
            return newList;

        }
        if (value == null) {
            final Map map = (Map) currentList.get(arrayPos);
            if (map.containsKey(prop)) {
                if (map.get(prop) instanceof List) {
                } else {
                    throw new IllegalArgumentException("Can't mix content and nodes, did you set an x.y='a' value then x.y.z='b'? The property was " + prop);
                }
                currentList = (List) map.get(prop);
            } else {
                currentList = new ArrayList();
                map.put(prop, currentList);
            }
        } else {
            currentList.set(arrayPos, value);
        }
        return currentList;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("{\n");
        for (Map.Entry<String, String> entry : lsdProperties.entrySet()) {
            buffer.append('{').append(entry.getKey()).append('=').append('\'').append(entry.getValue()).append('\'').append('}').append('\n');
        }
        buffer.append("}\n");
        return buffer.toString();
    }


    public void setType(LSDDictionaryTypes type) {
        if (type.getValue() == null) {
            throw new NullPointerException("Cannot set type to a null value.");
        }

        lsdProperties.put(TYPE_KEY, type.getValue());
    }

    public void setID(LiquidUUID id) {
        lsdProperties.put(ID_KEY, id.toString().toLowerCase());
    }

    public void timestamp() {
        lsdProperties.put(LSDAttribute.UPDATED.getKeyName(), String.valueOf(System.currentTimeMillis()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return getURI().equals(((LSDSimpleEntity) o).getURI());
    }

    @Override
    public int hashCode() {
        return getURI() != null ? getURI().hashCode() : 0;
    }

    public static LSDSimpleEntity createEmpty() {
        return new LSDSimpleEntity();
    }


    public static LSDSimpleEntity createNewEntity(LSDDictionaryTypes type, LiquidUUID uuid) {
        LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setType(type);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.ID, uuid.toString());
        return entity;
    }

    public static LSDSimpleEntity createNewEntity(LSDDictionaryTypes type) {
        LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setType(type);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        return entity;
    }

    public static LSDSimpleEntity createNewEntity(LSDTypeDef type, LiquidUUID uuid) {
        LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setTypeDef(type);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.ID, uuid.toString());
        return entity;
    }

    public static LSDSimpleEntity createNewEntity(LSDTypeDef type) {
        LSDSimpleEntity entity = new LSDSimpleEntity();
        entity.setTypeDef(type);
        entity.setAttribute(LSDAttribute.UPDATED, String.valueOf(System.currentTimeMillis()));
        entity.setAttribute(LSDAttribute.PUBLISHED, String.valueOf(System.currentTimeMillis()));
        return entity;
    }


    public void setTypeDef(LSDTypeDef type) {
        this.lsdTypeDef = type;
        if (type.asString() == null) {
            throw new NullPointerException("Cannot set type def to a null value.");
        }
        lsdProperties.put(TYPE_KEY, type.asString());
    }

    @Override
    public String asFreeText() {
        String s = "";
        for (String value : lsdProperties.values()) {
            s += value;
            s += " ";
        }
        return s;
    }

    @Override
    public boolean isNewerThan(LSDEntity entity) {
        return getUpdated().after(entity.getUpdated());
    }

    @Override
    public boolean wasPublishedAfter(LSDEntity entity) {
        return getPublished().after(entity.getPublished());
    }

    @Override
    public LSDEntity asUpdateEntity() {
        final LSDSimpleEntity newEntity = createNewEntity(getTypeDef(), getID());
        newEntity.setURI(getURI());
        return newEntity;
    }

    @Override
    public String getEURI() {
        return getAttribute(LSDAttribute.EURI);
    }


    public static LSDSimpleEntity createFromProperties(Map<String, String> lsdProperties) {
        return new LSDSimpleEntity(lsdProperties);
    }

    public static LSDSimpleEntity createFromNode(LSDNode lsdNode) {
        return new LSDSimpleEntity(lsdNode);
    }
}
