package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;

/**
 * @author neilelliz@cazcade.com
 */
public class ShortNamePropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(LSDPropertyFormatValidator propertyFormatValidator, String nextValidationString, String value) {
        return value.matches("\\.?[a-zA-Z0-9\\_]+") && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}