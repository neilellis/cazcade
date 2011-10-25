package cazcade.fountain.datastore.api;

/**
 * @author Neil Ellis
 */


public class IllegalResourceAccessException extends DataStoreException {
    public IllegalResourceAccessException(Throwable throwable) {
        super(throwable);
    }

    public IllegalResourceAccessException(String message, Object... params) {
        super(message, params);
    }

    public IllegalResourceAccessException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}