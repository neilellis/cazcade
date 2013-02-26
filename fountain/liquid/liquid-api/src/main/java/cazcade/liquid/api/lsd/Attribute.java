/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import cazcade.liquid.api.LiquidUUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;


public final class Attribute {


    /*
      * boards
     */


    /*
     *  Style
     */


    public static Attribute valueOf(@Nonnull final String key) {
        if (key.startsWith("x.")) {
            return new Attribute(key);
        }
        return ReverseLookup.map.get(key);
    }

    @Nonnull
    protected static Attribute create(final String key, final String validationString, final String description, final boolean nonupdateable, final boolean systemGenerated, final boolean subEntity, final boolean topLevel, final boolean hidden, final boolean common, final boolean isTransient) {
        final Attribute attribute = new Attribute(key, validationString, description, nonupdateable, systemGenerated, subEntity, topLevel, hidden, common, isTransient);
        ReverseLookup.map.put(key, attribute);
        return attribute;
    }

    @Nonnull
    protected static Attribute create(final String key, final String description, final boolean nonupdateable, final boolean systemGenerated, final boolean subEntity, final boolean topLevel, final boolean hidden, final boolean common, final boolean isTransient) {
        final Attribute attribute = new Attribute(key, "", description, nonupdateable, systemGenerated, subEntity, topLevel, hidden, common, isTransient);
        ReverseLookup.map.put(key, attribute);
        return attribute;
    }

    public static Attribute[] values() {
        return ReverseLookup.map.values().toArray(new Attribute[ReverseLookup.map.size()]);
    }

    protected static class ReverseLookup {
        @Nonnull
        private static final Map<String, Attribute> map = new HashMap<String, Attribute>();
    }

    private final String  key;
    private final String  validationString;
    private final String  description;
    private final boolean nonupdateable;
    private final boolean isTransient;

    private final boolean systemGenerated;
    private final boolean subEntity;
    private final boolean hidden;
    private final boolean common;
    private final boolean topLevelEntity;


    Attribute(final String key, final String validationString, final String description, final boolean nonupdateable, final boolean systemGenerated, final boolean subEntity, final boolean topLevel, final boolean hidden, final boolean common, final boolean isTransient) {
        this.key = key;
        this.validationString = validationString;
        this.description = description;
        this.nonupdateable = nonupdateable;
        this.systemGenerated = systemGenerated;
        this.subEntity = subEntity;
        topLevelEntity = topLevel;
        this.hidden = hidden;
        this.common = common;
        this.isTransient = isTransient;
        if (!nonupdateable && systemGenerated) {
            throw new IllegalArgumentException("Can't be writeable and system generated.");
        }
    }

    private Attribute(final String key, final String description, final boolean nonupdateable, final boolean systemGenerated, final boolean subEntity, final boolean hidden, final boolean common, final boolean isTransient) {
        this(key, "", description, nonupdateable, systemGenerated, subEntity, false, hidden, common, isTransient);
    }

    Attribute(@Nonnull final String key) {
        this(key, "", "", false, false, false, false, false, false, false);
        if (!key.startsWith("x.")) {
            throw new IllegalArgumentException("Attempted to create an attribute from a non 'x.' key.");
        }
    }

    public String getFormatValidationString() {
        return validationString;
    }

    @Nullable
    public LiquidUUID getId() {
        return null;
    }

    public String getKeyName() {
        return key;
    }

    public boolean isFreeTexSearchable() {
        return true;
    }

    public String name() {
        return getKeyName().replaceAll("\\.", "_").toUpperCase();
    }

    @Override
    public String toString() {
        return key;
    }

    boolean includeAttributeInPersistence(final boolean ignoreType, final boolean update) {
        return !isTransient() && (isUpdateable() || !update) && !isSystemGenerated() && (!this.equals(Dictionary.TYPE)
                                                                                         || !ignoreType);
    }


    public boolean isTransient() {
        return isTransient;
    }

    public boolean isUpdateable() {
        return !nonupdateable;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCommon() {
        return common;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isSubEntity() {
        return subEntity;
    }

    public boolean isSystemGenerated() {
        return systemGenerated;
    }

    public boolean isTopLevelEntity() {
        return topLevelEntity;
    }

}
