package cazcade.cli.commands;

import cazcade.cli.ShellSession;
import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Neil Ellis
 */

public class CommandExecutorImpl implements CommandExecutor {
    private final static Logger log = Logger.getLogger(CommandExecutorImpl.class);

    private ScheduledExecutorService schedulor;
    private AtomicInteger count = new AtomicInteger(0);
    private boolean shutdown;


    public CommandExecutorImpl(int threads) {
        schedulor = new ScheduledThreadPoolExecutor(threads);

    }

    public String execute(final Command command, final String[] args, CommandFactory commandFactory, final ShellSession shellSession) throws Exception {
        count.incrementAndGet();
        command.init(new CommandInitContext(this, commandFactory));
        command.start();
        final String[] expandedArgs = expand(args, shellSession);
        if (command.isShortLived()) {
            try {
                return runCommandDirect(command, expandedArgs, shellSession);
            } finally {
                command.stop();
            }
        }
        final long interval = command.getIntervalSeconds();
        if (interval > 0) {

            schedulor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    try {
                        command.run(args, shellSession);
                    } catch (InterruptedException ie) {
                        //http://java.sun.com/docs/books/tutorial/essential/concurrency/interrupt.html
                        Thread.interrupted();
                        try {
                            command.stop();
                        } catch (Exception e) {
                            log.error(e);
                        }
                        log.warn("Aborted {} due to interrupt.", command.getName());

                    } catch (Exception e) {
                        log.error(e);
                    } catch (Throwable throwable) {
                        log.error(throwable);
                        System.exit(-1);
                    }
                }
                //Why the random : http://apiblog.twitter.com/scheduling-your-twitter-bots
                //so we're trying not to impact resources with precise scheduling, but attempting to get average
                //scheduling correct (i.e. the average of Math.random() is 0.5 so the average initial delay
                // (over multiple executions) will be equal to the initial delay specified. This is in case
                // any of our commands are externally scheduled (i.e. by cron).
            }, (long) (0.5 + Math.random() * command.getInitialDelaySeconds()),
                    interval, TimeUnit.SECONDS);

        } else {
            schedulor.schedule(new Runnable() {
                public void run() {
                    try {
                        runCommandDirect(command, expandedArgs, shellSession);
                    } finally {
                        command.stop();
                    }
                }
            }, command.getInitialDelaySeconds(), TimeUnit.SECONDS);
        }
        return "";

    }

    private String[] expand(String[] args, ShellSession shellSession) {
        List<String> expanded= new ArrayList<String>();
        for (String arg : args) {
            if (arg.contains("*")) {
                String regex = arg.replaceAll("\\*", "(.*)");
                regex = regex.replaceAll("\\.", "\\.");
                regex = regex.replaceAll("\\+", "\\+");
                LSDEntity curr = shellSession.getCurrentPool();
                final List<LSDEntity> childPools = curr.getSubEntities(LSDAttribute.CHILD);
                for (LSDEntity childPool : childPools) {
                    if (childPool.getAttribute(LSDAttribute.NAME).matches(regex)) {
                        expanded.add(childPool.getURI().toString());
                    }
                }
            }else {
                expanded.add(arg);
            }
        }
        return expanded.toArray(new String[expanded.size()]);
    }

    private String runCommandDirect(Command command, String[] args, ShellSession shellSession) {
        try {
            return command.run(args, shellSession);
        } catch (InterruptedException ie) {
            //http://java.sun.com/docs/books/tutorial/essential/concurrency/interrupt.html
            Thread.interrupted();
            System.err.println("Aborted " + command.getName() + " due to interrupt.");
            return "";
        } catch (Exception e) {
            e.printStackTrace(System.err);
            log.error(e);
            return "";
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            log.error(t);
            log.error("Unrecoverable error.");
            System.exit(-1);
        } finally {
            log.info("Command " + command.getName() + " finished, " + count.decrementAndGet() + " remaining");
        }
        return "";
    }

    public void shutdown() {
        schedulor.shutdownNow();
        shutdown = true;
    }


    public void waitForCompletion() throws InterruptedException {
        while (count.get() > 0 && !shutdown) {
            Thread.sleep(100);
        }
    }
}
