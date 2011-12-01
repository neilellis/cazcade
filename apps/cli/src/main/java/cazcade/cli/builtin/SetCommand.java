package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDEntity;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public class SetCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(SetCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Set a value on the entity retrieved by 'with'.";
    }

    @Nonnull
    public String getName() {
        return "set";
    }

    @Nullable
    public String run(@Nonnull final String[] args, @Nonnull final ShellSession shellSession) throws Exception {
        if (!CommandSupport.checkEntityOnStack(shellSession)) {
            return null;
        }
        if (args.length < 2) {
            System.err.println("set <attribute-name> <value>");
        }

        final LSDEntity currentEntity = shellSession.getCurrentEntity();
        currentEntity.setValue(args[0], args[1]);
        return args[1];
    }


}
