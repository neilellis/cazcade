/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

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
    private PropertyFormatValidator formatValidator;

    private Map<String, TypeValidator> typeValidatorMap;


    public void setEntityValidatorMap(final Map<String, TypeValidator> typeValidatorMap) {
        this.typeValidatorMap = typeValidatorMap;
    }

    public void validate(@Nonnull final TransferEntity entity, final ValidationLevel level) {
        validateFormat(entity);
        final TypeDef typeDef = entity.type();
        //noinspection ConstantConditions
        if (typeDef == null) {
            throw new ValidationException("All entities must have a type, entity %s had no type.", entity.$(Dictionary.NAME));
        }
        final String value = entity.$(Dictionary.UPDATED);
        //noinspection ConstantConditions
        if (value == null) {
            throw new ValidationException("All entities must be timestamped (i.e. they must have a key: "
                                          + Dictionary.UPDATED
                                                      .getKeyName()
                                          + ").");
        }
        final Type primaryType = typeDef.getPrimaryType();
        validateForType(primaryType, entity, level);
    }

    private void validateFormat(@Nonnull final TransferEntity entity) {
        final Map<String, String> propertyMap = entity.map();
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
            final Attribute attribute = Attribute.valueOf(parts[0]);
            if (attribute != null && attribute.isSubEntity()) {
                validateKeyValue(key.substring(parts[0].length() + 1), value);
                return;
            }
        }
        if (!key.startsWith("x.")) {
            //The x namespace is reserved for custom properties, so we don't validate them.
            // Once they are standardized the x.<domain> part will be removed and the correct property added to the enumeration.
            final Attribute attributeEntry = Attribute.valueOf(key);
            if (attributeEntry == null) {
                throw new ValidationException("The LSD property with the name " +
                                              key +
                                              " is not known to the server, try using the dictionary REST service to find our which are valid property names.");
            }
            if (!formatValidator.isValidFormat(attributeEntry.getFormatValidationString(), value)) {
                throw new ValidationException("The LSD property with the name "
                                              + key
                                              + " has an incorrect value of '"
                                              + value
                                              + "'.");
            }
        }
    }

    private void validateForType(final Type primaryType, final Entity entity, final ValidationLevel level) {
        String typeString;
        String name;
        Type type;
        type = primaryType;
        while (true) {
            typeString = type.asString();
            name = Types.getNameForValue(typeString);
            if (name != null) {
                final TypeValidator typeValidator = typeValidatorMap.get(typeString);
                if (typeValidator == null && level == ValidationLevel.STRICT) {
                    throw new ValidationException("Validation is set to strict but there is no validator for " + typeString + ".");
                }
                if (typeValidator != null) {
                    typeValidator.validate(entity);
                }
                return;
            }
            if (type.getFlavors().isEmpty()) {
                throw new ValidationException("The type " + typeString + " is unknown.");
            }
            type = type.getParentType();
        }
    }

    public void setFormatValidator(final PropertyFormatValidator formatValidator) {
        this.formatValidator = formatValidator;
    }
}