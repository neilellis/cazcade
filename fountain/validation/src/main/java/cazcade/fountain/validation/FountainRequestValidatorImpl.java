/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.validation;

import cazcade.common.Logger;
import cazcade.fountain.validation.api.FountainRequestValidator;
import cazcade.fountain.validation.api.ValidationException;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.LiquidRequest;
import cazcade.liquid.api.RequestType;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * @author neilelliz@cazcade.com
 */
public class FountainRequestValidatorImpl<T extends LiquidRequest> implements FountainRequestValidator<T> {
    private Map<String, FountainRequestValidator> requestTypeValidatorMap;
    private FountainRequestValidator              defaultValidator;
    @Nonnull
    private final Logger log = Logger.getLogger(FountainRequestValidatorImpl.class);

    public void setRequestValidatorMap(final Map<String, FountainRequestValidator> typeValidatorMap) {
        requestTypeValidatorMap = typeValidatorMap;
    }

    public void validate(@Nonnull final T request, final ValidationLevel level) {
        final RequestType requestType = request.requestType();

        //noinspection ConstantConditions
        if (requestType == null) {
            throw new ValidationException("No type specified for the request.");
        }

        log.debug("Looking for validator for : " + requestType.name());
        final FountainRequestValidator validator = requestTypeValidatorMap.get(requestType.name());

        if (validator != null) {
            validator.validate(request, level);
        } else {
            defaultValidator.validate(request, level);
        }
    }

    public void setDefaultValidator(final FountainRequestValidator defaultValidator) {
        this.defaultValidator = defaultValidator;
    }
}
