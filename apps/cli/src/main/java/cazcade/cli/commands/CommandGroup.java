package cazcade.cli.commands;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Neil Ellis
 */

public class CommandGroup implements ExecutableCommand {

    private List<ExecutableCommand> commands = new ArrayList<ExecutableCommand>();

    public void add(ExecutableCommand command) {
        commands.add(command);
    }

    public String execute() throws Exception {
        for (ExecutableCommand command : commands) {
            command.execute();
        }
        return "";
    }
}
