package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;

/**
 * @author neilelliz@cazcade.com
 */
public class TextPropertyTypeValidator implements LSDPropertyTypeValidator{
    public boolean validate(LSDPropertyFormatValidator propertyFormatValidator, String nextValidationString, String value) {
        return propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}
