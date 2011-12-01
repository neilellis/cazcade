package cazcade.liquid.impl.validation.property;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class EmailPropertyTypeValidator implements LSDPropertyTypeValidator {
    public boolean validate(@Nonnull LSDPropertyFormatValidator propertyFormatValidator, String nextValidationString, @Nonnull String value) {
        return value.matches("^[\\w\\-]([\\.\\w])*[\\w]?+@([\\w\\-]+\\.)+([\\w\\-]+)$") && propertyFormatValidator.isValidFormat(nextValidationString, value);
    }
}