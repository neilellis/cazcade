package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;

/**
 * @author neilelliz@cazcade.com
 */
public class TypePropertyTypeValidator implements LSDPropertyTypeValidator{
    public boolean validate(LSDPropertyFormatValidator propertyFormatValidator, String nextValidationString, String value) {
        return value.matches("(\\w+\\.)(\\w+\\.)+(\\w+)(\\(((\\w+\\.)(\\w+\\.)+(\\w+)\\,?)+\\))?") && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}