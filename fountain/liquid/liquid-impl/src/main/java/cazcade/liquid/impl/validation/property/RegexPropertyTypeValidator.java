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
public class RegexPropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(final LSDPropertyFormatValidator propertyFormatValidator, final String nextValidationString, @Nonnull final String value) {
        return value.matches(nextValidationString);
    }
}