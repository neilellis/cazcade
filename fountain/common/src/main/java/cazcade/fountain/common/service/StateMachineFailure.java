package cazcade.fountain.common.service;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class StateMachineFailure extends CazcadeException {
    public StateMachineFailure(final Throwable cause, @Nonnull final String message, final Object... params) {
        super(cause, message, params);
    }

    public StateMachineFailure(@Nonnull final String message, final Object... params) {
        super(message, params);
    }

    public StateMachineFailure(final Throwable throwable) {
        super(throwable);
    }
}
