package cazcade.fountain.datastore.api;

/**
 * @author neilelliz@cazcade.com
 */
public class CannotChangeURIException extends DataStoreException {
    public CannotChangeURIException(Throwable throwable) {
        super(throwable);
    }

    public CannotChangeURIException(String message, Object... params) {
        super(message, params);
    }

    public CannotChangeURIException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
