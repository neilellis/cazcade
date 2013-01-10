/*
 * Copyright (c) 2009-2013 Cazcade Limited  - All Rights Reserved
 */

package cazcade.fountain.common.exec;

import cazcade.common.Logger;
import cazcade.fountain.common.error.ErrorHandler;
import cazcade.fountain.common.service.AbstractServiceStateMachine;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Neil Ellis
 */

public class FountainExecutorServiceImpl extends AbstractServiceStateMachine implements FountainExecutorService {
    @Nonnull
    private static final Logger log = Logger.getLogger(FountainExecutorServiceImpl.class);

    private final int maxRetry;

    private final int threadPoolSize;

    private       List<ThreadPoolExecutor> executors;
    private final int                      queueSize;
    private final int                      requeueDelay;

    private final int threadsPerBucket;

    @Nonnull
    private final AtomicLong count = new AtomicLong();

    public FountainExecutorServiceImpl(final int maxRetry, final int threadPoolSize, final int queueSize, final int requeueDelay, final int threadsPerBucket) {
        super();
        this.maxRetry = maxRetry;
        this.threadPoolSize = threadPoolSize;
        this.queueSize = queueSize;
        this.requeueDelay = requeueDelay;
        this.threadsPerBucket = threadsPerBucket;
    }

    public void execute(final boolean retry, @Nonnull final Object key, @Nonnull final FountainExecutable executable) throws InterruptedException {
        begin();
        try {
            final int executorId = Math.abs(key.hashCode() % threadPoolSize);
            final ThreadPoolExecutor threadPoolExecutor = executors.get(executorId);
            executeInternal(retry, executable, threadPoolExecutor);
        } finally {
            end();
        }
    }

    private void executeInternal(final boolean retry, @Nonnull final FountainExecutable executable, @Nonnull final ThreadPoolExecutor threadPoolExecutor) throws InterruptedException {
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

    public void execute(final boolean retry, @Nonnull final FountainExecutable executable) throws InterruptedException {
        begin();
        try {
            final int minimum = Integer.MAX_VALUE;
            ThreadPoolExecutor executor = null;
            for (final ThreadPoolExecutor threadPoolExecutor : executors) {
                final int i = threadPoolExecutor.getQueue().size();
                if (i < minimum) {
                    executor = threadPoolExecutor;
                }
            }
            assert executor != null;
            executeInternal(retry, executable, executor);
        } finally {
            end();
        }
    }

    public void execute(@Nonnull final FountainExecutable executable) throws InterruptedException {
        final ThreadPoolExecutor threadPoolExecutor = executors.get((int) (threadPoolSize * Math.random()));
        executeInternal(false, executable, threadPoolExecutor);
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
