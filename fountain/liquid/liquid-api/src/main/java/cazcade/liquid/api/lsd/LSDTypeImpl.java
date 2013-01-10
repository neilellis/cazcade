/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDTypeImpl implements LSDType {
    @Nonnull
    private String genus;
    @Nullable
    private String family;
    @Nullable
    private String typeClass;
    @Nonnull
    private final List<String> flavors = new ArrayList<String>();
    @Nonnull
    private String typeString;

    public LSDTypeImpl(@Nonnull final String genus, @Nullable final String family, @Nullable final String typeClass) {
        //noinspection ConstantConditions
        if (genus == null || family == null || typeClass == null) {
            throw new LSDTypeException("Types must be at least Genus.Family.Class");
        }
        this.genus = genus;
        this.family = family;
        this.typeClass = typeClass;
        typeString = genus + '.' + family + '.' + typeClass;
    }

    protected LSDTypeImpl(@Nonnull final String typeString, final boolean fullType) {
        this.typeString = typeString;
        final String[] types = typeString.split("\\.");
        if (fullType && types.length < 3) {
            throw new LSDTypeException("Types must be at least Genus.Family.Class");
        }
        genus = types[0];
        if (types.length > 1) {
            family = types[1];
        }
        if (types.length > 2) {
            typeClass = types[2];
        }
        if (types.length > 3) {
            flavors.addAll(Arrays.asList(types).subList(3, types.length));
        }
    }

    public LSDTypeImpl(@Nonnull final String typeString) {
        this(typeString, true);
    }

    public LSDTypeImpl() {
    }

    @Override
    public boolean canBe(@Nonnull final LSDDictionaryTypes type) {
        return isA(type) || typeString.startsWith(type.getValue() + '.');
    }

    @Override
    public boolean isA(@Nonnull final LSDDictionaryTypes dictionaryType) {
        return typeString.equals(dictionaryType.getValue());
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LSDTypeImpl lsdType = (LSDTypeImpl) o;

        if (!typeString.equals(lsdType.typeString)) {
            return false;
        }

        return true;
    }

    @Override @Nonnull
    public LSDType getClassOnlyType() {
        return new LSDTypeImpl(genus, family, typeClass);
    }

    @Override @Nonnull
    public LSDType getParentType() {
        final int index = typeString.lastIndexOf('.');
        if (index < 0) {
            throw new LSDTypeException("Cannot get the parent of a Genus only.");
        }
        return new LSDTypeImpl(typeString.substring(0, index));
    }

    @Override
    public int hashCode() {
        return typeString.hashCode();
    }

    @Override
    public boolean isSystemType() {
        return typeString.startsWith("System");
    }

    @Nullable @Override
    public String toString() {
        return asString();
    }

    @Nonnull
    public String asString() {
        return typeString;
    }

    @Nullable
    public String getFamily() {
        return family;
    }

    @Nonnull
    public List<String> getFlavors() {
        return flavors;
    }

    @Nonnull
    public String getGenus() {
        return genus;
    }

    @Nullable
    public String getTypeClass() {
        return typeClass;
    }
}
