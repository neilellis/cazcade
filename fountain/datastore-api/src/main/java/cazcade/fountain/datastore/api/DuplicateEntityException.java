package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class DuplicateEntityException extends DataStoreException {
    public DuplicateEntityException(final Throwable throwable) {
        super(throwable);
    }

    public DuplicateEntityException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public DuplicateEntityException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public boolean isClientException() {
        return true;
    }

}