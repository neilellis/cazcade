package cazcade.fountain.datastore.api;

/**
 * @author Neil Ellis
 */


public class StaleUpdateException extends DataStoreException {
    public StaleUpdateException(Throwable throwable) {
        super(throwable);
    }

    public StaleUpdateException(String message, Object ... params) {
        super(message, params);
    }

    public StaleUpdateException(Throwable cause, String message, Object ... params) {
        super(cause, message, params);
    }
}