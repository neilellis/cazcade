package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class IllegalResourceAccessException extends DataStoreException {
    public IllegalResourceAccessException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public IllegalResourceAccessException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public IllegalResourceAccessException(final Throwable throwable) {
        super(throwable);
    }
}