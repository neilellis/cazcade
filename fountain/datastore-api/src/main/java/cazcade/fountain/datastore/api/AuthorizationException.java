package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class AuthorizationException extends DataStoreException {
    public AuthorizationException(Throwable throwable) {
        super(throwable);
    }

    public AuthorizationException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public AuthorizationException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}
