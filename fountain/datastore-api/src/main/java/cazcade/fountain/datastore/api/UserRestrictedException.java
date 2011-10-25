package cazcade.fountain.datastore.api;

/**
 * @author Neil Ellis
 */


public class UserRestrictedException extends DataStoreException {
    public UserRestrictedException(Throwable throwable) {
        super(throwable);
    }

    public UserRestrictedException(String message, Object... params) {
        super(message, params);
    }

    public UserRestrictedException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}