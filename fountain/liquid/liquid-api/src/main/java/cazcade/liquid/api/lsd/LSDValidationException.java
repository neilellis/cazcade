package cazcade.liquid.api.lsd;

import cazcade.fountain.common.error.ClientCausedException;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDValidationException extends ClientCausedException {
    public LSDValidationException(Throwable throwable) {
        super(throwable);
    }

    public LSDValidationException(String message, Object... params) {
        super(message, params);
    }

    public LSDValidationException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
