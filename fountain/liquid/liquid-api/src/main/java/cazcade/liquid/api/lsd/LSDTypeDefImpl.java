package cazcade.liquid.api.lsd;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Neil Ellis
 */

public class LSDTypeDefImpl implements LSDTypeDef {

    private LSDType primaryType;
    private List<LSDType> secondaryTypes = new ArrayList<LSDType>();
    private String typeString;


    public LSDTypeDefImpl() {
    }

    public LSDTypeDefImpl(String typeStringParam) {
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

    public LSDTypeDefImpl(LSDDictionaryTypes parentType, String subType) {
        this(parentType+"."+subType);
    }

    private LSDType convertToType(String typeString) {
        return new LSDTypeImpl(typeString);
    }

    public LSDType getPrimaryType() {
        return primaryType;
    }

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
}
