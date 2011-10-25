package cazcade.fountain.datastore.api;

/**
 * @author Neil Ellis
 */


public class DeletedEntityException extends DataStoreException {
    public DeletedEntityException(Throwable throwable) {
        super(throwable);
    }

    public DeletedEntityException(String message, Object... params) {
        super(message, params);
    }

    public DeletedEntityException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }

    @Override
    public boolean isClientException() {
        return true;
    }
}