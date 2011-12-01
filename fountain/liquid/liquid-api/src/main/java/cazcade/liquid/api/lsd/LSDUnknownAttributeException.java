package cazcade.liquid.api.lsd;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDUnknownAttributeException extends CazcadeException {
    public LSDUnknownAttributeException(final Throwable throwable) {
        super(throwable);
    }

    public LSDUnknownAttributeException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public LSDUnknownAttributeException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }
}