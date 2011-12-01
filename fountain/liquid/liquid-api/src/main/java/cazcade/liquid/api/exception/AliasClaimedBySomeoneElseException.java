package cazcade.liquid.api.exception;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AliasClaimedBySomeoneElseException extends CazcadeException {
    public AliasClaimedBySomeoneElseException(final Throwable throwable) {
        super(throwable);
    }

    public AliasClaimedBySomeoneElseException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public AliasClaimedBySomeoneElseException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }
}