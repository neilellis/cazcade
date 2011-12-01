package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CannotChangeTypeException extends DataStoreException {
    public CannotChangeTypeException(Throwable throwable) {
        super(throwable);
    }

    public CannotChangeTypeException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public CannotChangeTypeException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}
