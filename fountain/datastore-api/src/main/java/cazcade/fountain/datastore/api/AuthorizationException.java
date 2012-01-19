package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class AuthorizationException extends DataStoreException {
    public AuthorizationException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public AuthorizationException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public AuthorizationException(final Throwable throwable) {
        super(throwable);
    }
}
