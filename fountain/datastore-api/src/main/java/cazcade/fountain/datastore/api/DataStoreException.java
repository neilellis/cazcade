package cazcade.fountain.datastore.api;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class DataStoreException extends CazcadeException {
    public DataStoreException(Throwable throwable) {
        super(throwable);
    }

    public DataStoreException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public DataStoreException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}