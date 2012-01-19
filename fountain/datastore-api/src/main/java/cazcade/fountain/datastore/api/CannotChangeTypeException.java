package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CannotChangeTypeException extends DataStoreException {
    public CannotChangeTypeException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public CannotChangeTypeException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public CannotChangeTypeException(final Throwable throwable) {
        super(throwable);
    }
}
