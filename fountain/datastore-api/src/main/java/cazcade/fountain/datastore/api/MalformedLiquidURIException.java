package cazcade.fountain.datastore.api;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */


public class MalformedLiquidURIException extends DataStoreException {
    public MalformedLiquidURIException(final Throwable throwable) {
        super(throwable);
    }

    public MalformedLiquidURIException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public MalformedLiquidURIException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }
}

