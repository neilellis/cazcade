package cazcade.fountain.validation;

import cazcade.fountain.validation.api.TypeValidator;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.lsd.*;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author neilelliz@cazcade.com
 */
public class FountainEntityValidator {
    private LSDPropertyFormatValidator formatValidator;

    private Map<String, TypeValidator> typeValidatorMap;


    public void setEntityValidatorMap(final Map<String, TypeValidator> typeValidatorMap) {
        this.typeValidatorMap = typeValidatorMap;
    }

    public void validate(@Nonnull final LSDTransferEntity entity, final ValidationLevel level) {
        validateFormat(entity);
        final LSDTypeDef lsdTypeDef = entity.getTypeDef();
        if (lsdTypeDef == null) {
            throw new LSDValidationException("All entities must have a type, entity %s had no type.", entity.getAttribute(
                    LSDAttribute.NAME
                                                                                                                         )
            );
        }
        final String value = entity.getAttribute(LSDAttribute.UPDATED);
        if (value == null) {
            throw new LSDValidationException(
                    "All entities must be timestamped (i.e. they must have a key: " + LSDAttribute.UPDATED.getKeyName() + ")."
            );
        }
        final LSDType primaryType = lsdTypeDef.getPrimaryType();
        validateForType(primaryType, entity, level);
    }

    private void validateFormat(@Nonnull final LSDTransferEntity entity) {
        final Map<String, String> propertyMap = entity.getMap();
        for (final Map.Entry<String, String> entry : propertyMap.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();
            //Note we remove all the array entry numbers, not required for format validation.
            validateKeyValue(key.replaceAll("\\.[0-9]+\\.", "\\."), value);
        }
    }

    private void validateKeyValue(@Nonnull final String key, final String value) {
        final String[] parts = key.split("\\.");
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
            final LSDAttribute attributeEntry = LSDAttribute.valueOf(key);
            if (attributeEntry == null) {
                throw new LSDValidationException("The LSD property with the name " +
                                                 key +
                                                 " is not known to the server, try using the dictionary REST service to find our which are valid property names."
                );
            }
            if (!formatValidator.isValidFormat(attributeEntry.getFormatValidationString(), value)) {
                throw new LSDValidationException(
                        "The LSD property with the name " + key + " has an incorrect value of '" + value + "'."
                );
            }
        }
    }

    private void validateForType(final LSDType primaryType, final LSDBaseEntity entity, final ValidationLevel level) {
        String typeString;
        String name;
        LSDType lsdType;
        lsdType = primaryType;
        while (true) {
            typeString = lsdType.asString();
            name = LSDDictionaryTypes.getNameForValue(typeString);
            if (name != null) {
                final TypeValidator typeValidator = typeValidatorMap.get(typeString);
                if (typeValidator == null && level == ValidationLevel.STRICT) {
                    throw new LSDValidationException(
                            "Validation is set to strict but there is no validator for " + typeString + "."
                    );
                }
                if (typeValidator != null) {
                    typeValidator.validate(entity);
                }
                return;
            }
            if (lsdType.getFlavors().isEmpty()) {
                throw new LSDValidationException("The type " + typeString + " is unknown.");
            }
            lsdType = lsdType.getParentType();
        }
    }

    public void setFormatValidator(final LSDPropertyFormatValidator formatValidator) {
        this.formatValidator = formatValidator;
    }
}