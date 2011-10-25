package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.cli.commands.ExecutableCommand;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class WhenCommand extends AbstractShortLivedCommand {
    private final static Logger log = Logger.getLogger(WhenCommand.class);

    private ExecutableCommand command;

    public WhenCommand(ExecutableCommand command) {
        this.command = command;
    }

    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Echoes the parameters passed in.";
    }

    public String getName() {
        return "when";
    }

    public long getIntervalSeconds() {
        return 0;
    }

    public String run(final String[] args, ShellSession shellSession) throws Exception {
        return "";
    }

    @Override
    public void stop() {
        //TODO
    }
}