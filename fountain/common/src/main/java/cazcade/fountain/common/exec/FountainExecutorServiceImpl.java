package cazcade.fountain.common.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import cazcade.fountain.common.error.ErrorHandler;
import cazcade.fountain.common.service.AbstractServiceStateMachine;

import cazcade.common.Logger;
/**
 * @author Neil Ellis
 */

public class FountainExecutorServiceImpl extends AbstractServiceStateMachine implements FountainExecutorService {
    private final static Logger log = Logger.getLogger(FountainExecutorServiceImpl.class);

    private final int maxRetry;

    private final int threadPoolSize;

    private List<ThreadPoolExecutor> executors;
    private final int queueSize;
    private final int requeueDelay;

    private final int threadsPerBucket;

    private AtomicLong count = new AtomicLong();

    public FountainExecutorServiceImpl(int maxRetry, int threadPoolSize, int queueSize, int requeueDelay, int threadsPerBucket) {
        this.maxRetry = maxRetry;
        this.threadPoolSize = threadPoolSize;
        this.queueSize = queueSize;
        this.requeueDelay = requeueDelay;
        this.threadsPerBucket = threadsPerBucket;
    }

    @Override
    public void start() throws Exception {
        super.start();
        executors = new ArrayList<ThreadPoolExecutor>(threadPoolSize);
        for (int i = 0; i < threadPoolSize; i++) {
            executors.add(new ThreadPoolExecutor(threadsPerBucket, threadsPerBucket, 3600, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(queueSize)));
        }
        unlock();
    }


    @Override
    public void stop() {
        log.info("Stopping Fountain Executor.");
        super.stop();
        try {
            for (int i = 0; i < threadPoolSize; i++) {
                executors.get(i).shutdownNow();
            }
            executors.clear();
            log.info("Fountain Executor STOPPED");
        } catch (Exception e) {
            ErrorHandler.handle(e);
        }
    }


    public void execute(final boolean retry, Object key, final FountainExecutable executable) throws InterruptedException {
        begin();
        try {
            int executorId = Math.abs(key.hashCode() % threadPoolSize);
            final ThreadPoolExecutor threadPoolExecutor = executors.get(executorId);
            executeInternal(retry, executable, threadPoolExecutor);
        } finally {
            end();
        }
    }

    public void execute(FountainExecutable executable) throws InterruptedException {
        final ThreadPoolExecutor threadPoolExecutor = executors.get((int) (threadPoolSize * Math.random()));
        executeInternal(false, executable, threadPoolExecutor);
    }

    public void execute(final boolean retry, final FountainExecutable executable) throws InterruptedException {
        begin();
        try {
            int minimum = Integer.MAX_VALUE;
            ThreadPoolExecutor executor = null;
            for (ThreadPoolExecutor threadPoolExecutor : executors) {
                final int i = threadPoolExecutor.getQueue().size();
                if (i < minimum) {
                    executor = threadPoolExecutor;
                }
            }
            executeInternal(retry, executable, executor);
        } finally {
            end();

        }
    }

    private void executeInternal(final boolean retry, final FountainExecutable executable, ThreadPoolExecutor threadPoolExecutor) throws InterruptedException {
        boolean cont = true;
        while (cont) {
            try {
                threadPoolExecutor.execute(new Runnable() {
                    public void run() {
                        try {
                            boolean fail = true;
                            int count = 0;
                            do {
                                try {
                                    if (isStopped()) {
                                        return;
                                    }
                                    if (isPaused()) {
                                        Thread.sleep(100);
                                        continue;
                                    }
                                    executable.run();
                                    fail = false;
                                } catch (InterruptedException ie) {
                                    Thread.interrupted();
                                    log.warn("Aborted due to interrupt.");
                                    return;
                                } catch (Exception e) {
                                    ErrorHandler.handle(e);
                                    try {
                                        Thread.sleep(10);
                                    } catch (InterruptedException ie) {
                                        Thread.interrupted();
                                        log.warn("Aborted due to interrupt.");
                                        return;
                                    }
                                } catch (Throwable t) {
                                    ErrorHandler.handle(t);
                                    log.error("Unrecoverable error.", t);

//                                    System.exit(-1);
                                }
                            } while (retry && fail && count++ < maxRetry);
                        } finally {
                            count.decrementAndGet();
                        }
                    }
                });
                cont = false;
                count.incrementAndGet();
            } catch (RejectedExecutionException e) {
                Thread.sleep(requeueDelay);
            }

        }
    }


    public void waitForExecutionsToFinish() throws InterruptedException {
        log.info("Waiting for executions to finish.");
        do {

            Thread.sleep(100);
            log.debug("Waiting for executions to finish.");
        } while (!isStopped() && count.get() > 0);
        if (count.get() > 0 && isStopped()) {
            log.info("Given up on waiting for executions to finish service is stopped.");

        }
    }

}
