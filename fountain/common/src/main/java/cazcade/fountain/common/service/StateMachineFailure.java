package cazcade.fountain.common.service;

import cazcade.fountain.common.error.CazcadeException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author neilellis@cazcade.com
 */
public class StateMachineFailure extends CazcadeException {

    public StateMachineFailure(Throwable throwable) {
        super(throwable);
    }

    public StateMachineFailure(String message, Object... params) {
        super(message, params);
    }

    public StateMachineFailure(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
