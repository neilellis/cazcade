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
public class TitlePropertyTypeValidator implements PropertyTypeValidator {
    public boolean validate(@Nonnull final PropertyFormatValidator propertyFormatValidator, final String nextValidationString, final String value) {
        return propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}