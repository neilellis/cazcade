package cazcade.liquid.api.exception;

import cazcade.fountain.common.error.CazcadeException;

/**
 * @author neilellis@cazcade.com
 */
public class AliasClaimedBySomeoneElseException extends CazcadeException {
    public AliasClaimedBySomeoneElseException(Throwable throwable) {
        super(throwable);
    }

    public AliasClaimedBySomeoneElseException(String message, Object... params) {
        super(message, params);
    }

    public AliasClaimedBySomeoneElseException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}