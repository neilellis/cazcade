package cazcade.cli.commands;

/**
 * @author Neil Ellis
 */

public class CommandInitContext {
    private final CommandExecutor commandExecutor;
    private final CommandFactory commandFactory;

    public CommandInitContext(final CommandExecutor commandExecutor, final CommandFactory commandFactory) {
        this.commandExecutor = commandExecutor;
        this.commandFactory = commandFactory;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public CommandFactory getCommandFactory() {
        return commandFactory;
    }
}
