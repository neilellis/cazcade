package cazcade.fountain.common.error;

/**
 * This exception and subclasses should be thrown if an error occurred that is
 * expected by the system occurs i.e. the error is not due to unexpected system behaviour.
 *
 * @author neilellis@cazcade.com
 */
public class NormalFlowException extends CazcadeException {
    public NormalFlowException(Throwable throwable) {
        super(throwable);
    }

    public NormalFlowException(String message, Object... params) {
        super(message, params);
    }

    public NormalFlowException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
