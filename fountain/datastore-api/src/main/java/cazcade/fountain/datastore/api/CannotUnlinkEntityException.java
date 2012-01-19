package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class CannotUnlinkEntityException extends DataStoreException {
    public CannotUnlinkEntityException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public CannotUnlinkEntityException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public CannotUnlinkEntityException(final Throwable throwable) {
        super(throwable);
    }

    public boolean isClientException() {
        return true;
    }
}