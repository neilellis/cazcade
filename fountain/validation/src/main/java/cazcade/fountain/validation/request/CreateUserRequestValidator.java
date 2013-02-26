/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.validation.request;

import cazcade.common.Logger;
import cazcade.fountain.validation.api.ValidationException;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.lsd.Dictionary;
import cazcade.liquid.api.lsd.Types;
import cazcade.liquid.api.request.CreateUserRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CreateUserRequestValidator extends AbstractRequestValidator<CreateUserRequest> {
    @Nonnull
    private static final Logger log = Logger.getLogger(CreateUserRequestValidator.class);

    @Override
    public void validate(@Nonnull final CreateUserRequest request, final ValidationLevel level) {
        log.debug("Validating create user request.");
        if (!request.request().is(Types.T_USER)) {
            throw new ValidationException("The entity supplied is not a user entity.");
        }
        entityValidator.validate(request.request(), level);
        if (!request.request().has$(Dictionary.PLAIN_PASSWORD)) {
            throw new ValidationException("You cannot create a new user without supplying the plain text password.");
        }
    }
}
