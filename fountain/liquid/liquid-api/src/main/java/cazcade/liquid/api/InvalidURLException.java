package cazcade.liquid.api;

import cazcade.fountain.common.error.CazcadeException;

/**
 * @author neilelliz@cazcade.com
 */
public class InvalidURLException extends CazcadeException {
    public InvalidURLException(Throwable throwable) {
        super(throwable);
    }

    public InvalidURLException(String message, Object... params) {
        super(message, params);
    }

    public InvalidURLException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
