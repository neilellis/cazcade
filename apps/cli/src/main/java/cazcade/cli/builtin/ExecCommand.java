package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public class ExecCommand extends AbstractShortLivedCommand {
    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Executes a file.";
    }

    @Nonnull
    public String getName() {
        return "exec";
    }


    @Nullable
    public String run(final String[] args, ShellSession shellSession) throws Exception {
        return null;
    }

}