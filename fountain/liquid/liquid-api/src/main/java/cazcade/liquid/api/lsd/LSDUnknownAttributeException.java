package cazcade.liquid.api.lsd;

import cazcade.fountain.common.error.CazcadeException;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDUnknownAttributeException extends CazcadeException {
    public LSDUnknownAttributeException(Throwable throwable) {
        super(throwable);
    }

    public LSDUnknownAttributeException(String message, Object... params) {
        super(message, params);
    }

    public LSDUnknownAttributeException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}