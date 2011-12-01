package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class MalformedLiquidURIException extends DataStoreException {
    public MalformedLiquidURIException(Throwable throwable) {
        super(throwable);
    }

    public MalformedLiquidURIException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public MalformedLiquidURIException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}

