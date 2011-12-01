package cazcade.fountain.server.rest;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class RestHandlerException extends CazcadeException {
    public RestHandlerException(final Throwable throwable) {
        super(throwable);
    }

    public RestHandlerException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public RestHandlerException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }
}
