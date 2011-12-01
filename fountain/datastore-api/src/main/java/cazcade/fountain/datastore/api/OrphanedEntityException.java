package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class OrphanedEntityException extends DataStoreException {
    public OrphanedEntityException(Throwable throwable) {
        super(throwable);
    }

    public OrphanedEntityException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public OrphanedEntityException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }

    public boolean isClientException() {
        return true;
    }

}