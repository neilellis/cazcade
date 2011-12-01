package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class StaleUpdateException extends DataStoreException {
    public StaleUpdateException(Throwable throwable) {
        super(throwable);
    }

    public StaleUpdateException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public StaleUpdateException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}