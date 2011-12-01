package cazcade.fountain.common.service;

import cazcade.common.Logger;
import cazcade.fountain.common.error.ErrorHandler;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Neil Ellis
 */

public abstract class AbstractServiceStateMachine implements ServiceStateMachine {

    @Nonnull
    private static final Logger log = Logger.getLogger(AbstractServiceStateMachine.class);
    public static final int LOCK_TRY_TIMEOUT_IN_SECS = 20;

    private enum State {
        STOPPED, INITIALISATION, STARTED, PAUSED
    }

    @Nonnull
    private volatile State state = State.STOPPED;


    @Nonnull
    private final AtomicInteger activeCount = new AtomicInteger(0);
    @Nonnull
    private final AtomicBoolean locked = new AtomicBoolean(false);

    protected AbstractServiceStateMachine() {
    }

    public void begin() throws InterruptedException {
        if (state == State.STOPPED) {
            throw new IllegalStateException("Cannot begin an action as the service is stopped.");
        }
        if (state == State.PAUSED) {
            log.debug("Waiting on paused service.");
        }
        if (locked.get()) {
            waitUntilUnlocked();
        }
        activeCount.incrementAndGet();

    }

    private void waitUntilUnlocked() throws InterruptedException {
        log.info("Waiting for service lock...");
        int count = 0;
        while (locked.get() && count++ < LOCK_TRY_TIMEOUT_IN_SECS * 10) {
            Thread.sleep(100);
            System.err.print(".");
            if (state == State.STOPPED) {
                log.info("Service stopped, giving up on lock.");
                throw new IllegalStateException("Cannot begin an action as the service is stopped.");
            }
        }
        if (locked.get()) {
            throw new StateMachineFailure("Failed waiting for unlock with %s attempts.", count);
        }

    }

    public void end() {
        activeCount.decrementAndGet();

    }


    public void start() throws Exception {
        if (state == State.STARTED) {
            throw new IllegalStateException("Tried to start a started service.");
        }
        if (state == State.PAUSED) {
            throw new IllegalStateException("Tried to start a paused service.");
        }
        state = State.STARTED;


    }

    public void stop() {
        synchronized (locked) {
            changeStateToStopped();
            try {
                if (!locked.get()) {
                    lock();
                }
            } catch (InterruptedException e) {
                //Interruptions at this point shouldn't stop a shutdown process.
                Thread.interrupted();
                ErrorHandler.handle(e);
            }
        }
    }

    protected void changeStateToStopped() {
        if (state == State.STOPPED) {
            log.warn("Tried to stop a stopped service.");
        }
        state = State.STOPPED;
    }

    public void pause() throws Exception {
        if (state == State.STOPPED) {
            throw new IllegalStateException("Tried to pause a stopped service.");
        }
        if (state == State.PAUSED) {
            throw new IllegalStateException("Tried to pause a paused service.");
        }
        state = State.PAUSED;
        lock();
    }

    public void lock() throws InterruptedException {
        synchronized (locked) {
            if (locked.get()) {
                log.error("Already locked.");
            } else {
                locked.set(true);
            }
            int count = 0;
            while (activeCount.get() > 0 && count++ < LOCK_TRY_TIMEOUT_IN_SECS) {
                //noinspection WaitWithoutCorrespondingNotify
                locked.wait(1000);
                log.info("Awaiting active count (" + activeCount.get() + ") on {0}.", getClass());
            }
            if (count >= LOCK_TRY_TIMEOUT_IN_SECS) {
                unlock();
                throw new StateMachineFailure("Lock failed with %s attempts %s active.", count, activeCount);
            }
        }
    }

    public void resume() throws Exception {
        if (state == State.STOPPED) {
            throw new IllegalStateException("Tried to resume a stopped service.");
        }
        if (state == State.STARTED) {
            throw new IllegalStateException("Tried to resume a started service.");
        }
        state = State.STARTED;
        unlock();
    }

    public void unlock() {
        synchronized (locked) {
            locked.set(false);
        }
    }

    public boolean isStopped() {
        return state == State.STOPPED;
    }

    public boolean isPaused() {
        return state == State.PAUSED;
    }

    public boolean isStarted() {
        return state == State.STARTED;
    }

    public final void stopIfNotStopped() {
        synchronized (locked) {
            if (!isStopped()) {
                stop();
                unlock();
            }
        }
    }

    public final void startIfNotStarted() throws Exception {
        synchronized (locked) {
            if (!isStarted()) {
                start();
            }
        }
    }

    public synchronized void hardstop() {
        state = State.STOPPED;
        locked.set(false);
    }
}
