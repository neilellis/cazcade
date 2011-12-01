package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class DuplicateEntityException extends DataStoreException {
    public DuplicateEntityException(Throwable throwable) {
        super(throwable);
    }

    public DuplicateEntityException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public DuplicateEntityException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }

    public boolean isClientException() {
        return true;
    }

}