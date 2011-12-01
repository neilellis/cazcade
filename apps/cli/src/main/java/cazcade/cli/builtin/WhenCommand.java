package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.cli.commands.ExecutableCommand;
import cazcade.common.Logger;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */

public class WhenCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(WhenCommand.class);

    private final ExecutableCommand command;

    public WhenCommand(final ExecutableCommand command) {
        super();
        this.command = command;
    }

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Echoes the parameters passed in.";
    }

    @Nonnull
    public String getName() {
        return "when";
    }

    public long getIntervalSeconds() {
        return 0;
    }

    @Nonnull
    public String run(final String[] args, final ShellSession shellSession) throws Exception {
        return "";
    }

    @Override
    public void stop() {
        //TODO
    }
}