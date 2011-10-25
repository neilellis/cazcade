package cazcade.fountain.datastore.api;

/**
 * @author Neil Ellis
 */


public class OrphanedEntityException extends DataStoreException {
    public OrphanedEntityException(Throwable throwable) {
        super(throwable);
    }

    public OrphanedEntityException(String message, Object... params) {
        super(message, params);
    }

    public OrphanedEntityException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }

    public boolean isClientException() {
        return true;
    }

}