package cazcade.cli.commands;

import cazcade.cli.ShellSession;

/**
 * The JobExecutor, funnily enough, executes commands.
 *
 * @author Neil Ellis
 */

public interface CommandExecutor {



    /**
     * Shuts down the executor.
     */
    void shutdown();


    /**
     * Waits for all commands to finish. However even a single repeating job will prevent this method from ever returning.
     *
     * @throws InterruptedException if the waiting thread (this thread) is interrupted.
     */
    void waitForCompletion() throws InterruptedException;

    /**
     * Execute a Job with the arguments specified see {@link Command#run(String[], cazcade.cli.ShellSession)}
     */
    String execute(Command command, String[] jobArgs, CommandFactory commandFactory, ShellSession shellSession) throws Exception;
}
