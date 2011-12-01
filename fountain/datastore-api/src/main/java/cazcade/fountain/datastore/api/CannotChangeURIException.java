package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CannotChangeURIException extends DataStoreException {
    public CannotChangeURIException(final Throwable throwable) {
        super(throwable);
    }

    public CannotChangeURIException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public CannotChangeURIException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }
}
