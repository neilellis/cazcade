package cazcade.fountain.datastore.api;

/**
 * @author Neil Ellis
 */


public class EntityNotFoundException extends DataStoreException {
    public EntityNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public EntityNotFoundException(String message, Object ... params) {
        super(message, params);
    }

    public EntityNotFoundException(Throwable cause, String message, Object ... params) {
        super(cause, message, params);
    }
}