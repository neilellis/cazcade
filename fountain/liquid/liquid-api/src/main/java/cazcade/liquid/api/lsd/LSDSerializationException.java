package cazcade.liquid.api.lsd;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDSerializationException extends CazcadeException {
    public LSDSerializationException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public LSDSerializationException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public LSDSerializationException(final Throwable throwable) {
        super(throwable);
    }
}
