package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class EntityNotFoundException extends DataStoreException {
    public EntityNotFoundException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public EntityNotFoundException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public EntityNotFoundException(final Throwable throwable) {
        super(throwable);
    }
}