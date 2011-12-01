package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class OrphanedEntityException extends DataStoreException {
    public OrphanedEntityException(final Throwable throwable) {
        super(throwable);
    }

    public OrphanedEntityException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public OrphanedEntityException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public boolean isClientException() {
        return true;
    }

}