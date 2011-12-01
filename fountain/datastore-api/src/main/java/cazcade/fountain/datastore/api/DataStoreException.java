package cazcade.fountain.datastore.api;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class DataStoreException extends CazcadeException {
    public DataStoreException(final Throwable throwable) {
        super(throwable);
    }

    public DataStoreException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public DataStoreException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }
}