package cazcade.liquid.api.lsd;

import cazcade.fountain.common.error.ClientCausedException;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDValidationException extends ClientCausedException {
    public LSDValidationException(final Throwable throwable) {
        super(throwable);
    }

    public LSDValidationException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public LSDValidationException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }
}
