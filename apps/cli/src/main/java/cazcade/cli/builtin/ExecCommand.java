package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class ExecCommand extends AbstractShortLivedCommand {
    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Executes a file.";
    }

    public String getName() {
        return "exec";
    }


    public String run(final String[] args, ShellSession shellSession) throws Exception {
       return null;
    }

}