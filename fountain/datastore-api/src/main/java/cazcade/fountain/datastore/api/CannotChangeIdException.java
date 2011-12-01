package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CannotChangeIdException extends DataStoreException {
    public CannotChangeIdException(Throwable throwable) {
        super(throwable);
    }

    public CannotChangeIdException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public CannotChangeIdException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}