package cazcade.fountain.common.error;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ErrorFlowException extends CazcadeException {
    public ErrorFlowException(Throwable throwable) {
        super(throwable);
    }

    public ErrorFlowException(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public ErrorFlowException(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}
