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
public class TypeImpl implements Type {
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

    public TypeImpl(@Nonnull final String genus, @Nullable final String family, @Nullable final String typeClass) {
        //noinspection ConstantConditions
        if (genus == null || family == null || typeClass == null) {
            throw new TypeException("Types must be at least Genus.Family.Class");
        }
        this.genus = genus;
        this.family = family;
        this.typeClass = typeClass;
        typeString = genus + '.' + family + '.' + typeClass;
    }

    protected TypeImpl(@Nonnull final String typeString, final boolean fullType) {
        this.typeString = typeString;
        final String[] types = typeString.split("\\.");
        if (fullType && types.length < 3) {
            throw new TypeException("Types must be at least Genus.Family.Class");
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

    public TypeImpl(@Nonnull final String typeString) {
        this(typeString, true);
    }

    public TypeImpl() {
    }

    @Override
    public boolean canBe(@Nonnull final Types type) {
        return isA(type) || typeString.startsWith(type.getValue() + '.');
    }

    @Override
    public boolean isA(@Nonnull final Types dictionaryType) {
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

        final TypeImpl lsdType = (TypeImpl) o;

        if (!typeString.equals(lsdType.typeString)) {
            return false;
        }

        return true;
    }

    @Override @Nonnull
    public Type getClassOnlyType() {
        return new TypeImpl(genus, family, typeClass);
    }

    @Override @Nonnull
    public Type getParentType() {
        final int index = typeString.lastIndexOf('.');
        if (index < 0) {
            throw new TypeException("Cannot get the parent of a Genus only.");
        }
        return new TypeImpl(typeString.substring(0, index));
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