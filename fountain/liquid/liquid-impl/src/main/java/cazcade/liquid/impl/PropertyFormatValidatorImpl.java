/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.PropertyFormatValidationException;
import cazcade.liquid.api.lsd.PropertyFormatValidator;
import cazcade.liquid.api.lsd.PropertyTypeValidator;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author neilelliz@cazcade.com
 */
public class PropertyFormatValidatorImpl implements PropertyFormatValidator {
    private Map<String, PropertyTypeValidator> validators;

    public boolean isValidFormat(@Nonnull final String validationString, final String value) {
        if (validationString.isEmpty()) {
            return true;
        }
        final int colonIndex = validationString.indexOf(':');
        if (colonIndex < 0) {
            throw new PropertyFormatValidationException("Invalid format " + validationString);
        } else {
            final String schema = validationString.substring(0, colonIndex);
            final String nextValidationString = validationString.substring(colonIndex + 1);
            final PropertyTypeValidator validator = validators.get(schema);
            if (validator == null) {
                throw new PropertyFormatValidationException("Unrecognized property format schema " + schema);
            }
            return validator.validate(this, nextValidationString, value);
        }
    }

    public void setValidators(final Map<String, PropertyTypeValidator> validators) {
        this.validators = validators;
    }
}
