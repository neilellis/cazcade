package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class DeletedEntityException extends DataStoreException {
    public DeletedEntityException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public DeletedEntityException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public DeletedEntityException(final Throwable throwable) {
        super(throwable);
    }

    @Override
    public boolean isClientException() {
        return true;
    }
}