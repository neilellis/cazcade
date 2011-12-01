package cazcade.liquid.impl;

import cazcade.liquid.api.lsd.LSDPropertyFormatValidationException;
import cazcade.liquid.api.lsd.LSDPropertyFormatValidator;
import cazcade.liquid.api.lsd.LSDPropertyTypeValidator;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDPropertyFormatValidatorImpl implements LSDPropertyFormatValidator {

    private Map<String, LSDPropertyTypeValidator> validators;

    public boolean isValidFormat(@Nonnull String validationString, String value) {
        if (validationString.isEmpty()) {
            return true;
        }
        int colonIndex = validationString.indexOf(":");
        if (colonIndex < 0) {
            throw new LSDPropertyFormatValidationException("Invalid format " + validationString);
        } else {
            String schema = validationString.substring(0, colonIndex);
            String nextValidationString = validationString.substring(colonIndex + 1);
            LSDPropertyTypeValidator validator = validators.get(schema);
            if (validator == null) {
                throw new LSDPropertyFormatValidationException("Unrecognized property format schema " + schema);
            }
            return validator.validate(this, nextValidationString, value);
        }
    }

    public void setValidators(Map<String, LSDPropertyTypeValidator> validators) {
        this.validators = validators;
    }
}
