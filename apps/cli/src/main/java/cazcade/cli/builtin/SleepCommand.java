package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class SleepCommand extends AbstractShortLivedCommand {



    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Sleeps for the supplied time";
    }

    public String getName() {
        return "sleep";
    }
    public String run(final String[] args, ShellSession shellSession) throws Exception {
        Thread.sleep(Integer.valueOf(args[0]));
        return "";
    }
}