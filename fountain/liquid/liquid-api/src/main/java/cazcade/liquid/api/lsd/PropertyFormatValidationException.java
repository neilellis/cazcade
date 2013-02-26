/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.liquid.api.lsd;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class PropertyFormatValidationException extends ValidationException {
    public PropertyFormatValidationException(@Nonnull final String message, final Throwable cause) {
        super(message, cause);
    }

    public PropertyFormatValidationException(@Nonnull final String message) {
        super(message);
    }

    public PropertyFormatValidationException(final Throwable cause) {
        super(cause);
    }
}
