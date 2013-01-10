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
public class ShortNamePropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(@Nonnull final LSDPropertyFormatValidator propertyFormatValidator, final String nextValidationString, @Nonnull final String value) {
        return value.matches("\\.?[a-zA-Z0-9\\_]+") && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}