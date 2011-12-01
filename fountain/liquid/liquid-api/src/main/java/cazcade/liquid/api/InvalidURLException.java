package cazcade.liquid.api;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class InvalidURLException extends CazcadeException {
    public InvalidURLException(final Throwable throwable) {
        super(throwable);
    }

    public InvalidURLException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public InvalidURLException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }
}
