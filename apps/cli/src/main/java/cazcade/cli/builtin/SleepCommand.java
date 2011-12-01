package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */

public class SleepCommand extends AbstractShortLivedCommand {


    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Sleeps for the supplied time";
    }

    @Nonnull
    public String getName() {
        return "sleep";
    }

    @Nonnull
    public String run(final String[] args, final ShellSession shellSession) throws Exception {
        Thread.sleep(Integer.valueOf(args[0]));
        return "";
    }
}