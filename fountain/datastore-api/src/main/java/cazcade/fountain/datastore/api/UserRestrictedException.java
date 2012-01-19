package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class UserRestrictedException extends DataStoreException {
    public UserRestrictedException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public UserRestrictedException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public UserRestrictedException(final Throwable throwable) {
        super(throwable);
    }
}