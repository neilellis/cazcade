/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.PropertyFormatValidator;
import cazcade.liquid.api.lsd.PropertyTypeValidator;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class NumberPropertyTypeValidator implements PropertyTypeValidator {
    public boolean validate(@Nonnull final PropertyFormatValidator propertyFormatValidator, final String nextValidationString, final String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}