package cazcade.cli.commands;

import cazcade.cli.ShellSession;

public class CommandExecutionContext implements ExecutableCommand {
    private final Command command;
    private final String[] jobArgs;
    private final CommandFactory commandFactory;
    private final CommandExecutor executor;
    private final ShellSession shellSession;


    public CommandExecutionContext(final Command command, final String[] jobArgs, final CommandFactory commandFactory, final CommandExecutor executor, final ShellSession shellSession) {
        this.command = command;
        this.jobArgs = jobArgs;
        this.commandFactory = commandFactory;
        this.executor = executor;
        this.shellSession = shellSession;
    }

    public Command getCommand() {
        return command;
    }

    public String[] getJobArgs() {
        return jobArgs;
    }

    public CommandFactory getCommandFactory() {
        return commandFactory;
    }

    public String execute() throws Exception {
        return executor.execute(command, jobArgs, commandFactory, shellSession);
    }
}
