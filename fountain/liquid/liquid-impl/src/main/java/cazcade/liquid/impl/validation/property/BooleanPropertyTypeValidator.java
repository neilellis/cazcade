package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;
import org.apache.commons.lang.StringUtils;

/**
 * @author neilelliz@cazcade.com
 */
public class BooleanPropertyTypeValidator implements LSDPropertyTypeValidator{
    public boolean validate(LSDPropertyFormatValidator propertyFormatValidator, String nextValidationString, String value) {
        return ("false".equals(value) || "true".equals(value)) && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}