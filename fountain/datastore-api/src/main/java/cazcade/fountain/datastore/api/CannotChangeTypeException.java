package cazcade.fountain.datastore.api;

/**
 * @author neilelliz@cazcade.com
 */
public class CannotChangeTypeException extends DataStoreException {
    public CannotChangeTypeException(Throwable throwable) {
        super(throwable);
    }

    public CannotChangeTypeException(String message, Object ... params) {
        super(message, params);
    }

    public CannotChangeTypeException(Throwable cause, String message, Object ... params) {
        super(cause, message, params);
    }
}
