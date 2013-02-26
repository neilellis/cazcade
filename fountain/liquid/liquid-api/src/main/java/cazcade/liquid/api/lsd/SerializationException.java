/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class SerializationException extends CazcadeException {
    public SerializationException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public SerializationException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public SerializationException(final Throwable throwable) {
        super(throwable);
    }
}
