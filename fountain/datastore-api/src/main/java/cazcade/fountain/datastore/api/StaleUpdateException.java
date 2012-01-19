package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class StaleUpdateException extends DataStoreException {
    public StaleUpdateException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public StaleUpdateException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public StaleUpdateException(final Throwable throwable) {
        super(throwable);
    }
}