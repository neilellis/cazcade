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

    public boolean isValidFormat(@Nonnull final String validationString, final String value) {
        if (validationString.isEmpty()) {
            return true;
        }
        final int colonIndex = validationString.indexOf(':');
        if (colonIndex < 0) {
            throw new LSDPropertyFormatValidationException("Invalid format " + validationString);
        } else {
            final String schema = validationString.substring(0, colonIndex);
            final String nextValidationString = validationString.substring(colonIndex + 1);
            final LSDPropertyTypeValidator validator = validators.get(schema);
            if (validator == null) {
                throw new LSDPropertyFormatValidationException("Unrecognized property format schema " + schema);
            }
            return validator.validate(this, nextValidationString, value);
        }
    }

    public void setValidators(final Map<String, LSDPropertyTypeValidator> validators) {
        this.validators = validators;
    }
}
