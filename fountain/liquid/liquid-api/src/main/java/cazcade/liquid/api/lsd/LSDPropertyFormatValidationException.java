package cazcade.liquid.api.lsd;

/**
 * @author neilelliz@cazcade.com
 */
public class LSDPropertyFormatValidationException extends LSDValidationException {
    public LSDPropertyFormatValidationException(String message) {
        super(message);
    }

    public LSDPropertyFormatValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public LSDPropertyFormatValidationException(Throwable cause) {
        super(cause);
    }
}
