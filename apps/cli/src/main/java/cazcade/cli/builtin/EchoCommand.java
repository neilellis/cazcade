package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class EchoCommand extends AbstractShortLivedCommand {
    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Echoes the parameters passed in.";
    }

    public String getName() {
        return "echo";
    }


    public String run(final String[] args, ShellSession shellSession) throws Exception {
        StringBuffer returnValue= new StringBuffer();
        for (String arg : args) {
            returnValue.append(arg).append(" ");
        }
        System.out.println(returnValue);
        return returnValue.toString();
    }


}