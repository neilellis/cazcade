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

public class UnsetCommand extends AbstractShortLivedCommand {
    @Nonnull
    private final static Logger log = Logger.getLogger(UnsetCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Removes an attribute from an entity.";
    }

    @Nonnull
    public String getName() {
        return "unset";
    }

    @Nullable
    public String run(@Nonnull final String[] args, @Nonnull ShellSession shellSession) throws Exception {
        if (!CommandSupport.checkEntityOnStack(shellSession)) {
            return null;
        }
        if (args.length < 1) {
            System.err.println("unset <attribute-name>");
        }

        final LSDEntity currentEntity = shellSession.getCurrentEntity();
        //The empty string causes the server to remove the value completely.
        currentEntity.setValue(args[0], "");
        return args[0];
    }


}
