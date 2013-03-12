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
public class URLPropertyTypeValidator implements PropertyTypeValidator {
    public boolean validate(@Nonnull final PropertyFormatValidator propertyFormatValidator, final String nextValidationString, @Nonnull final String value) {
        return value.startsWith("/")
               || value.startsWith("./")
               || value.toLowerCase().matches("^((http[s]?|ftp|mailto):)(.*)$")
                  && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}