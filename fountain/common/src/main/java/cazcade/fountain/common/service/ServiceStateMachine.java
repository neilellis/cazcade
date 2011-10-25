package cazcade.fountain.common.service;

/**
 * The ServiceStateMachine interface defines a service which can pass through a start/pause/resume/stop lifecycle.
 *
 * @author Neil Ellis
 */

public interface ServiceStateMachine {

    /**
     * This method should trigger allocation of resources, background threads etc. and then call unlock();
     *
     * @throws Exception if the implementing method throws an exception.
     */
    public void start() throws Exception;

    /**
     * This method should call lock() then trigger deallocation of resources and terminate background threads.
     * <p/>
     * This method should never throw exceptions.
     */
    public void stop();

    /**
     * Pauses the service, i.e. it waits for all methods to finish and stops new ones from being called. However no
     * resources are deallocated. unlock() should be called at the end of the method.
     *
     * @throws Exception if the implementing method throws an exception.
     */
    public void pause() throws Exception;

    /**
     * Calls unlock and resumes the service.
     *
     * @throws Exception if the implementing method throws an exception.
     */
    public void resume() throws Exception;


    /**
     * This marks that a method on the service has been called. It should block if lock() has been called.
     *
     * @throws InterruptedException if the thread is interrupted while attempting to get a lock.
     */
    void begin() throws InterruptedException;

    /**
     * This method is called at the end of a service method and should be in a try/finally after the begin() method. It
     * is used to keep track of the number of active method calls.
     */
    void end();

    /**
     * Called by begin() to obtain a lock.
     * <p/>
     * As a rule this method should not be called and is really for internal use.
     * <p/>
     * This method will check to see how many active method calls exist and wait for all of them to finish before
     * returning.
     *
     * @throws InterruptedException if the thread is interrupted while attempting to get a lock.
     */
    void lock() throws InterruptedException;


    /**
     * This method should be called during start() it releases begin() method calls.
     */
    void unlock();

    boolean isStopped();

    boolean isStarted();

    boolean isPaused();

    void stopIfNotStopped();

    void startIfNotStarted() throws Exception;

    void hardstop();


}
