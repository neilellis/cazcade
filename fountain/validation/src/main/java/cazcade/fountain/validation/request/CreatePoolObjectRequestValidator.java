/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.validation.request;

import cazcade.common.Logger;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.request.CreatePoolObjectRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CreatePoolObjectRequestValidator extends AbstractRequestValidator<CreatePoolObjectRequest> {
    @Nonnull
    private static final Logger log = Logger.getLogger(CreatePoolObjectRequestValidator.class);

    public void validate(@Nonnull final CreatePoolObjectRequest request, final ValidationLevel level) {
        log.debug("Validating create pool object request.");
        validPoolObject(request);

        entityValidator.validate(request.request(), level);
    }
}