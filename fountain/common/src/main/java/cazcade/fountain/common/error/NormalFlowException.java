package cazcade.fountain.common.error;

import javax.annotation.Nonnull;

/**
 * This exception and subclasses should be thrown if an error occurred that is
 * expected by the system occurs i.e. the error is not due to unexpected system behaviour.
 *
 * @author neilellis@cazcade.com
 */
public class NormalFlowException extends CazcadeException {
    public NormalFlowException(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public NormalFlowException(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public NormalFlowException(final Throwable throwable) {
        super(throwable);
    }
}
