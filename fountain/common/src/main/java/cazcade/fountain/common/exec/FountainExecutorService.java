package cazcade.fountain.common.exec;

import cazcade.fountain.common.service.ServiceStateMachine;


/**
 * @author Neil Ellis
 */

public interface FountainExecutorService extends ServiceStateMachine {
    /**
     * Execute some executable code in one of a pool of threads. If retry is specified (that is environmental conditions
     * could change and allow a succesful completion at a later date) then the job will be executed multiple times.
     * <br/> The hashObject is used to reduce collisitons, so for example if the hash object is a file name and the job
     * is writing to that filename then no two commands can alter that file simultaneously. This enforces single threaded
     * behaviour towards a given resource. <br/> Note that if the execution queue is full for the job this method will
     * block until succesful queueing.
     *
     * @param retry      should execution be re-attempted on failure.
     * @param hashObject an object which determines through it's hashcode which thread this should be executed on.
     * @param executable some executable code.
     *
     * @throws InterruptedException if the submission thread (this thread) is interrupted while attempting to queue.
     */
    void execute(boolean retry, Object hashObject, FountainExecutable executable) throws InterruptedException;

    /**
     * No retry, random hash.
     *
     * @see #execute(boolean, Object, FountainExecutable)
     */
    void execute(FountainExecutable executable) throws InterruptedException;

    /**
     * Waits until all the thread pools are empty, do not submit new work while this is running.
     *
     * @throws InterruptedException if the thread is interrupted.
     */
    void waitForExecutionsToFinish() throws InterruptedException;

}
