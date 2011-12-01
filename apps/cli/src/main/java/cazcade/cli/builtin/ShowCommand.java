package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public class ShowCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(ShowCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Show a resource e.g. user, session etc.";
    }

    @Nonnull
    public String getName() {
        return "show";
    }

    @Nullable
    public String run(@Nonnull final String[] args, @Nonnull final ShellSession shellSession) throws Exception {

        if (args.length < 2) {
            System.err.println("show <resource-type> <resource-name>");
            return null;
        }

        final LiquidMessage response;
        response = CommandSupport.retrieveObject(args, shellSession);
        if (response == null) {
            return null;
        }
        final LSDBaseEntity entity = response.getResponse();
        System.out.println(entity);
        return entity.getUUID().toString();
    }


}
