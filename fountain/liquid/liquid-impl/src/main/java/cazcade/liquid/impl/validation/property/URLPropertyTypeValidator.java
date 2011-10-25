package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;

/**
 * @author neilelliz@cazcade.com
 */
public class URLPropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(LSDPropertyFormatValidator propertyFormatValidator, String nextValidationString, String value) {
        return value.toLowerCase().matches("^((http[s]?|ftp|mailto):)(.*)$") && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}