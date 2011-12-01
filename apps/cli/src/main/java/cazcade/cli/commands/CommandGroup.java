package cazcade.cli.commands;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Neil Ellis
 */

public class CommandGroup implements ExecutableCommand {

    @Nonnull
    private final List<ExecutableCommand> commands = new ArrayList<ExecutableCommand>();

    public void add(ExecutableCommand command) {
        commands.add(command);
    }

    @Nonnull
    public String execute() throws Exception {
        for (ExecutableCommand command : commands) {
            command.execute();
        }
        return "";
    }
}
