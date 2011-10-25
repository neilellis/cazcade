package cazcade.fountain.datastore.api;

/**
 * @author Neil Ellis
 */


public class MalformedLiquidURIException extends DataStoreException {
    public MalformedLiquidURIException(Throwable throwable) {
        super(throwable);
    }

    public MalformedLiquidURIException(String message, Object... params) {
        super(message, params);
    }

    public MalformedLiquidURIException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}

