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
public class TitlePropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(@Nonnull final LSDPropertyFormatValidator propertyFormatValidator, final String nextValidationString, final String value) {
        return propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}