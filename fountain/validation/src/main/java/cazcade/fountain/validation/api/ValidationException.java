package cazcade.fountain.validation.api;

/**
 * @author neilelliz@cazcade.com
 */
public class ValidationException extends RuntimeException {
    public ValidationException() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ValidationException(String message) {
        super(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public ValidationException(Throwable cause) {
        super(cause);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
