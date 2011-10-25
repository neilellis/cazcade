package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;
import org.apache.commons.lang.StringUtils;

/**
 * @author neilelliz@cazcade.com
 */
public class IntegerPropertyTypeValidator implements LSDPropertyTypeValidator{
    public boolean validate(LSDPropertyFormatValidator propertyFormatValidator, String nextValidationString, String value) {
        return value.matches("(\\-|\\+)?[0-9]+") && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}