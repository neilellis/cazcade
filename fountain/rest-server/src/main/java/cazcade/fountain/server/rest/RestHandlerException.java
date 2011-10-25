package cazcade.fountain.server.rest;

import cazcade.fountain.common.error.CazcadeException;

/**
 * @author neilelliz@cazcade.com
 */
public class RestHandlerException extends CazcadeException {
    public RestHandlerException(Throwable throwable) {
        super(throwable);
    }

    public RestHandlerException(String message, Object... params) {
        super(message, params);
    }

    public RestHandlerException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
