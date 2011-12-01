package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CannotChangeIdException extends DataStoreException {
    public CannotChangeIdException(final Throwable throwable) {
        super(throwable);
    }

    public CannotChangeIdException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public CannotChangeIdException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }
}