package cazcade.fountain.common.service;

import cazcade.fountain.common.error.CazcadeException;

import javax.annotation.Nonnull;

/**
 * @author neilellis@cazcade.com
 */
public class StateMachineFailure extends CazcadeException {

    public StateMachineFailure(Throwable throwable) {
        super(throwable);
    }

    public StateMachineFailure(@Nonnull String message, Object... params) {
        super(message, params);
    }

    public StateMachineFailure(Throwable cause, @Nonnull String message, Object... params) {
        super(cause, message, params);
    }
}
