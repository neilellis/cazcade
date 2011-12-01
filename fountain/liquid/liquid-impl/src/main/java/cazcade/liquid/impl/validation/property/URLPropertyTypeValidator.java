package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class URLPropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(@Nonnull final LSDPropertyFormatValidator propertyFormatValidator, final String nextValidationString, @Nonnull final String value) {
        return value.toLowerCase().matches("^((http[s]?|ftp|mailto):)(.*)$") && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}