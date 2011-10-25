package cazcade.liquid.api.lsd;

import cazcade.fountain.common.error.CazcadeException;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDSerializationException extends CazcadeException {

    public LSDSerializationException(Throwable throwable) {
        super(throwable);
    }

    public LSDSerializationException(String message, Object... params) {
        super(message, params);
    }

    public LSDSerializationException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
