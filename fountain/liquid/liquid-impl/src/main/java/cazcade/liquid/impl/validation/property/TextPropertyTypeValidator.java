package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class TextPropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(@Nonnull final LSDPropertyFormatValidator propertyFormatValidator, final String nextValidationString, final String value) {
        return propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}
