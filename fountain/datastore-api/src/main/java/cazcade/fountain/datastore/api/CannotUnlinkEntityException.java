package cazcade.fountain.datastore.api;

/**
 * @author Neil Ellis
 */


public class CannotUnlinkEntityException extends DataStoreException {
    public CannotUnlinkEntityException(Throwable throwable) {
        super(throwable);
    }

    public CannotUnlinkEntityException(String message, Object... params) {
        super(message, params);
    }

    public CannotUnlinkEntityException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }

    public boolean isClientException() {
        return true;
    }

}