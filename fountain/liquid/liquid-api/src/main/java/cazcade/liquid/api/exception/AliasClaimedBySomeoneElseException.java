package cazcade.liquid.api.exception;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class AliasClaimedBySomeoneElseException extends CazcadeException {
    public AliasClaimedBySomeoneElseException(Throwable throwable) {
        super(throwable);
    }

    public AliasClaimedBySomeoneElseException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public AliasClaimedBySomeoneElseException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}