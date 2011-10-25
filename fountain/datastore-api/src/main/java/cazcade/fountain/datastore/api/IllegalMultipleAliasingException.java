package cazcade.fountain.datastore.api;

/**
 * @author neilelliz@cazcade.com
 */
public class IllegalMultipleAliasingException extends DataStoreException {
    public IllegalMultipleAliasingException(Throwable throwable) {
        super(throwable);
    }

    public IllegalMultipleAliasingException(String message, Object ... params) {
        super(message, params);
    }

    public IllegalMultipleAliasingException(Throwable cause, String message, Object ... params) {
        super(cause, message, params);
    }
}
