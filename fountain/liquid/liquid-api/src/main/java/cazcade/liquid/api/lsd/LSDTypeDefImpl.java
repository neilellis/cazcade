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

    public LSDTypeDefImpl(@Nullable String typeStringParam) {
        if (typeStringParam == null) {
            throw new LSDTypeDefException("Tried to create a type def from a null type string.");
        }
        this.typeString = typeStringParam.replace(" ", "");
        int firstParen = typeString.indexOf("(");
        if (firstParen < 0) {
            primaryType = convertToType(typeString);
        } else {
            primaryType = convertToType(typeString.substring(0, firstParen));
            int secondParen = typeString.indexOf(")");
            if (secondParen < firstParen) {
                throw new LSDTypeDefException("There should be one open parenthesis matched by one closed parenthesis in a type definition");
            }
            String typeList = typeString.substring(firstParen, secondParen);
            String[] typeStrings = typeList.split(",");
            for (String secondaryTypeString : typeStrings) {
                secondaryTypes.add(convertToType(secondaryTypeString));

            }
        }

    }

    public LSDTypeDefImpl(@Nonnull LSDDictionaryTypes parentType, @Nullable String subType) {
        this(subType == null ? parentType.asString() : (parentType + "." + subType));
    }

    @Nonnull
    private LSDType convertToType(@Nonnull String typeString) {
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

    public boolean isA(LSDDictionaryTypes type) {
        return primaryType.isA(type);
    }

    public boolean canBe(LSDDictionaryTypes type) {
        if (primaryType.canBe(type)) {
            return true;
        }
        for (LSDType secondaryType : secondaryTypes) {
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
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LSDTypeDefImpl that = (LSDTypeDefImpl) o;

        if (!typeString.equals(that.typeString)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return typeString.hashCode();
    }
}
