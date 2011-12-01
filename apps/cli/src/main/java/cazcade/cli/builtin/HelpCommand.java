package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.cli.commands.Command;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Neil Ellis
 */

public class HelpCommand extends AbstractShortLivedCommand {
    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    public String getName() {
        return "help";
    }

    @Override
    public String getShortName() {
        return "h";
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Does exactly what it says on the tin...";
    }

    public String run(@Nonnull String[] args, ShellSession shellSession) throws Exception {
        final Collection<Command> all = getContext().getCommandFactory().getAll();
        if (args.length == 1) {
            for (Command command : all) {
                if (command.getName().equals(args[0])) {
                    HelpFormatter f = new HelpFormatter();
                    f.printHelp(command.getName(), command.getOptions());
                }
            }
        } else {
            System.err.println("Help can only accept one argument.");
        }
        return "";
    }

}
