/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import cazcade.fountain.common.error.ClientCausedException;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class ValidationException extends ClientCausedException {
    public ValidationException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public ValidationException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public ValidationException(final Throwable throwable) {
        super(throwable);
    }
}
