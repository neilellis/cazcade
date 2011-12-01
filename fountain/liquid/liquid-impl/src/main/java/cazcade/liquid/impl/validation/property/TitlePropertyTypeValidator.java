package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class TitlePropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(@Nonnull LSDPropertyFormatValidator propertyFormatValidator, String nextValidationString, String value) {
        return propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}