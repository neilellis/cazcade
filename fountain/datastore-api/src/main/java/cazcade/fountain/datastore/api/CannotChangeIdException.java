package cazcade.fountain.datastore.api;

/**
 * @author neilelliz@cazcade.com
 */
public class CannotChangeIdException extends DataStoreException {
    public CannotChangeIdException(Throwable throwable) {
        super(throwable);
    }

    public CannotChangeIdException(String message, Object ... params) {
        super(message, params);
    }

    public CannotChangeIdException(Throwable cause, String message, Object ... params) {
        super(cause, message, params);
    }
}