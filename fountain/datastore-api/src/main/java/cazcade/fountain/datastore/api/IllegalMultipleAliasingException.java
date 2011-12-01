package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author neilelliz@cazcade.com
 */
public class IllegalMultipleAliasingException extends DataStoreException {
    public IllegalMultipleAliasingException(Throwable throwable) {
        super(throwable);
    }

    public IllegalMultipleAliasingException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public IllegalMultipleAliasingException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}
