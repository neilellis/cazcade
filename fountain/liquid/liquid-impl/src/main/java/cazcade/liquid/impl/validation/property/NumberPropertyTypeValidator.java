/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class NumberPropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(@Nonnull final LSDPropertyFormatValidator propertyFormatValidator, final String nextValidationString, final String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException nfe) {
            return false;
        }

        return propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}