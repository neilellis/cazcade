package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class UserRestrictedException extends DataStoreException {
    public UserRestrictedException(Throwable throwable) {
        super(throwable);
    }

    public UserRestrictedException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public UserRestrictedException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}