package cazcade.liquid.api;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class InvalidURLException extends CazcadeException {
    public InvalidURLException(Throwable throwable) {
        super(throwable);
    }

    public InvalidURLException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public InvalidURLException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}
