package cazcade.fountain.common.error;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class ErrorFlowException extends CazcadeException {
    public ErrorFlowException(final Throwable throwable) {
        super(throwable);
    }

    public ErrorFlowException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public ErrorFlowException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }
}
