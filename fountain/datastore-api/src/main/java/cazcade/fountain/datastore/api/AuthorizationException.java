package cazcade.fountain.datastore.api;

/**
 * @author neilelliz@cazcade.com
 */
public class AuthorizationException extends DataStoreException {
    public AuthorizationException(Throwable throwable) {
        super(throwable);
    }

    public AuthorizationException(String message, Object ... params) {
        super(message, params);
    }

    public AuthorizationException(Throwable cause, String message, Object ... params) {
        super(cause, message, params);
    }
}
