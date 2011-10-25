package cazcade.fountain.validation;

import cazcade.fountain.validation.api.TypeValidator;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.lsd.*;

import java.util.Map;

/**
 * @author neilelliz@cazcade.com
 */
public class FountainEntityValidator {
    private LSDPropertyFormatValidator formatValidator;

    private Map<String, TypeValidator> typeValidatorMap;

    public void validate(LSDEntity entity, ValidationLevel level) {
        validateFormat(entity);
        LSDTypeDef lsdTypeDef = entity.getTypeDef();
        if (lsdTypeDef == null) {
            throw new LSDValidationException("All entities must have a type, entity %s had no type.", entity.getAttribute(LSDAttribute.NAME));
        }
        String value = entity.getAttribute(LSDAttribute.UPDATED);
        if (value == null) {
            throw new LSDValidationException("All entities must be timestamped (i.e. they must have a key: " + LSDAttribute.UPDATED.getKeyName() + ").");
        }
        LSDType primaryType = lsdTypeDef.getPrimaryType();
        validateForType(primaryType, entity, level);
    }

    private void validateForType(LSDType primaryType, LSDEntity entity, ValidationLevel level) {
        String typeString;
        String name;
        LSDType lsdType;
        lsdType = primaryType;
        while (true) {
            typeString = lsdType.asString();
            name = LSDDictionaryTypes.getNameForValue(typeString);
            if (name != null) {
                TypeValidator typeValidator = typeValidatorMap.get(typeString);
                if (typeValidator == null && level == ValidationLevel.STRICT) {
                    throw new LSDValidationException("Validation is set to strict but there is no validator for " + typeString + ".");
                }
                if (typeValidator != null) {
                    typeValidator.validate(entity);
                }
                return;
            }
            if (lsdType.getFlavors().size() == 0) {
                throw new LSDValidationException("The type " + typeString + " is unknown.");
            }
            lsdType = lsdType.getParentType();
        }


    }


    private void validateFormat(LSDEntity entity) {
        Map<String, String> propertyMap = entity.getMap();
        for (Map.Entry<String, String> entry : propertyMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            //Note we remove all the array entry numbers, not required for format validation.
            validateKeyValue(key.replaceAll("\\.[0-9]+\\.", "\\."), value);
        }
    }

    private void validateKeyValue(String key, String value) {
        String[] parts = key.split("\\.");
        if (parts.length > 0) {
            final LSDAttribute attribute = LSDAttribute.valueOf(parts[0]);
            if (attribute != null && attribute.isSubEntity()) {
                validateKeyValue(key.substring(parts[0].length() + 1), value);
                return;
            }
        }
        if (!key.startsWith("x.")) {
            //The x namespace is reserved for custom properties, so we don't validate them.
            // Once they are standardized the x.<domain> part will be removed and the correct property added to the enumeration.
            LSDAttribute attributeEntry = LSDAttribute.valueOf(key);
            if (attributeEntry == null) {
                throw new LSDValidationException("The LSD property with the name " + key + " is not known to the server, try using the dictionary REST service to find our which are valid property names.");
            }
            if (!formatValidator.isValidFormat(attributeEntry.getFormatValidationString(), value)) {
                throw new LSDValidationException("The LSD property with the name " + key + " has an incorrect value of '" + value + "'.");
            }
        }
    }


    public void setEntityValidatorMap(Map<String, TypeValidator> typeValidatorMap) {
        this.typeValidatorMap = typeValidatorMap;
    }

    public void setFormatValidator(LSDPropertyFormatValidator formatValidator) {
        this.formatValidator = formatValidator;
    }
}