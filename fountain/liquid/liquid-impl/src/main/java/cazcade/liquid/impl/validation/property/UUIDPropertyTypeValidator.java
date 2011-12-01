package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UUIDPropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(@Nonnull LSDPropertyFormatValidator propertyFormatValidator, String nextValidationString, @Nonnull String value) {
        return value.matches("\\p{XDigit}{8}\\-\\p{XDigit}{4}\\-\\p{XDigit}{4}\\-\\p{XDigit}{4}\\-\\p{XDigit}{12}") && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}