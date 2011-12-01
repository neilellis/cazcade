package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class CannotUnlinkEntityException extends DataStoreException {
    public CannotUnlinkEntityException(Throwable throwable) {
        super(throwable);
    }

    public CannotUnlinkEntityException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public CannotUnlinkEntityException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }

    public boolean isClientException() {
        return true;
    }

}