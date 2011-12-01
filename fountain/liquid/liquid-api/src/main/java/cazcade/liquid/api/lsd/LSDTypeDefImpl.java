package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Neil Ellis
 */

public class LSDTypeDefImpl implements LSDTypeDef {

    private LSDType primaryType;
    @Nonnull
    private final List<LSDType> secondaryTypes = new ArrayList<LSDType>();
    private String typeString;


    public LSDTypeDefImpl() {
    }

    public LSDTypeDefImpl(@Nullable final String typeStringParam) {
        if (typeStringParam == null) {
            throw new LSDTypeDefException("Tried to create a type def from a null type string.");
        }
        typeString = typeStringParam.replace(" ", "");
        final int firstParen = typeString.indexOf('(');
        if (firstParen < 0) {
            primaryType = convertToType(typeString);
        } else {
            primaryType = convertToType(typeString.substring(0, firstParen));
            final int secondParen = typeString.indexOf(')');
            if (secondParen < firstParen) {
                throw new LSDTypeDefException("There should be one open parenthesis matched by one closed parenthesis in a type definition");
            }
            final String typeList = typeString.substring(firstParen, secondParen);
            final String[] typeStrings = typeList.split(",");
            for (final String secondaryTypeString : typeStrings) {
                secondaryTypes.add(convertToType(secondaryTypeString));

            }
        }

    }

    public LSDTypeDefImpl(@Nonnull final LSDDictionaryTypes parentType, @Nullable final String subType) {
        this(subType == null ? parentType.asString() : parentType + "." + subType);
    }

    @Nonnull
    private LSDType convertToType(@Nonnull final String typeString) {
        return new LSDTypeImpl(typeString);
    }

    public LSDType getPrimaryType() {
        return primaryType;
    }

    @Nonnull
    public List<LSDType> getSecondaryTypes() {
        return secondaryTypes;
    }

    public String asString() {
        return typeString;
    }

    public boolean isA(final LSDDictionaryTypes type) {
        return primaryType.isA(type);
    }

    public boolean canBe(final LSDDictionaryTypes type) {
        if (primaryType.canBe(type)) {
            return true;
        }
        for (final LSDType secondaryType : secondaryTypes) {
            if (secondaryType.canBe(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final LSDTypeDefImpl that = (LSDTypeDefImpl) o;

        if (!typeString.equals(that.typeString)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return typeString.hashCode();
    }
}
