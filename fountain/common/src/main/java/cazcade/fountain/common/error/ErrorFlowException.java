package cazcade.fountain.common.error;

/**
 * @author neilellis@cazcade.com
 */
public class ErrorFlowException extends CazcadeException {
    public ErrorFlowException(Throwable throwable) {
        super(throwable);
    }

    public ErrorFlowException(String message, Object... params) {
        super(message, params);
    }

    public ErrorFlowException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
