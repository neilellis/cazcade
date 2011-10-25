package cazcade.liquid.api.exception;

import cazcade.fountain.common.error.CazcadeException;

/**
 * @author neilellis@cazcade.com
 */
public class UnclaimedAliasException extends CazcadeException {
    public UnclaimedAliasException(Throwable throwable) {
        super(throwable);
    }

    public UnclaimedAliasException(String message, Object... params) {
        super(message, params);
    }

    public UnclaimedAliasException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
