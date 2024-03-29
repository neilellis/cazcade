/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.validation.request;

import cazcade.common.Logger;
import cazcade.fountain.validation.api.ValidationLevel;
import cazcade.liquid.api.LiquidRequest;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class DefaultRequestValidator extends AbstractRequestValidator<LiquidRequest> {
    @Nonnull
    private static final Logger log = Logger.getLogger(DefaultRequestValidator.class);

    public void validate(@Nonnull final LiquidRequest request, final ValidationLevel level) {
        log.debug("Default validator.");
        if (request.hasRequestEntity()) {
            entityValidator.validate(request.request(), level);
        }
    }
}