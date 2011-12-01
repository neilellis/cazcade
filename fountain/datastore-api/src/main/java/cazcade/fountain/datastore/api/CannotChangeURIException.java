package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class CannotChangeURIException extends DataStoreException {
    public CannotChangeURIException(Throwable throwable) {
        super(throwable);
    }

    public CannotChangeURIException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public CannotChangeURIException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}
