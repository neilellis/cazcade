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
public class URIPropertyTypeValidator implements PropertyTypeValidator {
    public boolean validate(@Nonnull final PropertyFormatValidator propertyFormatValidator, final String nextValidationString, @Nonnull final String value) {
        return value.matches("^(([a-zA-Z0-9+.-]+):)?.*$") && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}