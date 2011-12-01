package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;

/**
 * @author Neil Ellis
 */

public class EchoCommand extends AbstractShortLivedCommand {
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
        return "echo";
    }


    public String run(@Nonnull final String[] args, final ShellSession shellSession) throws Exception {
        final StringBuffer returnValue = new StringBuffer();
        for (final String arg : args) {
            returnValue.append(arg).append(" ");
        }
        System.out.println(returnValue);
        return returnValue.toString();
    }


}