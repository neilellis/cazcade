/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Neil Ellis
 */

public class TypeDefImpl implements TypeDef {
    private Type primaryType;
    @Nonnull
    private final List<Type> secondaryTypes = new ArrayList<Type>();
    private String typeString;

    public TypeDefImpl(@Nonnull final Types parentType, @Nullable final String subType) {
        this(subType == null ? parentType.asString() : parentType + "." + subType);
    }

    public TypeDefImpl(@Nullable final String typeStringParam) {
        if (typeStringParam == null) {
            throw new TypeDefException("Tried to create a type def from a null type string.");
        }
        typeString = typeStringParam.replace(" ", "");
        final int firstParen = typeString.indexOf('(');
        if (firstParen < 0) {
            primaryType = convertToType(typeString);
        } else {
            primaryType = convertToType(typeString.substring(0, firstParen));
            final int secondParen = typeString.indexOf(')');
            if (secondParen < firstParen) {
                throw new TypeDefException("There should be one open parenthesis matched by one closed parenthesis in a type definition");
            }
            final String typeList = typeString.substring(firstParen, secondParen);
            final String[] typeStrings = typeList.split(",");
            for (final String secondaryTypeString : typeStrings) {
                secondaryTypes.add(convertToType(secondaryTypeString));
            }
        }
    }

    @Nonnull
    private Type convertToType(@Nonnull final String typeString) {
        return new TypeImpl(typeString);
    }

    public TypeDefImpl() {
    }

    public boolean canBe(final Types type) {
        if (primaryType.canBe(type)) {
            return true;
        }
        for (final Type secondaryType : secondaryTypes) {
            if (secondaryType.canBe(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final TypeDefImpl that = (TypeDefImpl) o;

        if (!typeString.equals(that.typeString)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return typeString.hashCode();
    }

    public boolean isA(final Types type) {
        return primaryType.isA(type);
    }

    @Override
    public String toString() {
        return asString();
    }

    public String asString() {
        return typeString;
    }

    public Type getPrimaryType() {
        return primaryType;
    }

    @Nonnull
    public List<Type> getSecondaryTypes() {
        return secondaryTypes;
    }
}
