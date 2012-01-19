package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class IllegalMultipleAliasingException extends DataStoreException {
    public IllegalMultipleAliasingException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public IllegalMultipleAliasingException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public IllegalMultipleAliasingException(final Throwable throwable) {
        super(throwable);
    }
}
