/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class UnknownAttributeException extends CazcadeException {
    public UnknownAttributeException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public UnknownAttributeException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public UnknownAttributeException(final Throwable throwable) {
        super(throwable);
    }
}