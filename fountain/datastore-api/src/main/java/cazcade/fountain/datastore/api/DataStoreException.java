package cazcade.fountain.datastore.api;

import cazcade.fountain.common.error.CazcadeException;

/**
 * @author Neil Ellis
 */


public class DataStoreException extends CazcadeException {
    public DataStoreException(Throwable throwable) {
        super(throwable);
    }

    public DataStoreException(String message, Object... params) {
        super(message, params);
    }

    public DataStoreException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}