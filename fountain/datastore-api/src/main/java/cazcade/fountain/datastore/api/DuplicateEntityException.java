package cazcade.fountain.datastore.api;

/**
 * @author Neil Ellis
 */


public class DuplicateEntityException extends DataStoreException {
    public DuplicateEntityException(Throwable throwable) {
        super(throwable);
    }

    public DuplicateEntityException(String message, Object... params) {
        super(message, params);
    }

    public DuplicateEntityException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }

    public boolean isClientException() {
        return true;
    }

}