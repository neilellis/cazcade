package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class EntityNotFoundException extends DataStoreException {
    public EntityNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public EntityNotFoundException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public EntityNotFoundException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}