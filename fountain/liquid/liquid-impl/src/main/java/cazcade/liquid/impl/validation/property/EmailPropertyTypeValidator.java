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
public class EmailPropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(@Nonnull final LSDPropertyFormatValidator propertyFormatValidator, final String nextValidationString, @Nonnull final String value) {
        return value.matches("^[\\w\\-]([\\.\\w])*[\\w]?+@([\\w\\-]+\\.)+([\\w\\-]+)$")
               && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}