package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class DeletedEntityException extends DataStoreException {
    public DeletedEntityException(Throwable throwable) {
        super(throwable);
    }

    public DeletedEntityException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public DeletedEntityException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }

    @Override
    public boolean isClientException() {
        return true;
    }
}