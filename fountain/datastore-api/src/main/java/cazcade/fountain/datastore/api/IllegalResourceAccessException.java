package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class IllegalResourceAccessException extends DataStoreException {
    public IllegalResourceAccessException(Throwable throwable) {
        super(throwable);
    }

    public IllegalResourceAccessException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public IllegalResourceAccessException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}