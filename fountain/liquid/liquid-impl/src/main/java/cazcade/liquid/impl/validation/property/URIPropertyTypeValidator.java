package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class URIPropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(@Nonnull LSDPropertyFormatValidator propertyFormatValidator, String nextValidationString, @Nonnull String value) {
        return value.matches("^(([a-zA-Z0-9+.-]+):)?.*$") && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}