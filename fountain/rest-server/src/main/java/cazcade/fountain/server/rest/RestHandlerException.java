package cazcade.fountain.server.rest;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class RestHandlerException extends CazcadeException {
    public RestHandlerException(Throwable throwable) {
        super(throwable);
    }

    public RestHandlerException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public RestHandlerException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}
