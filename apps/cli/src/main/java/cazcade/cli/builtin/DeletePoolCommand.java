package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDBaseEntity;
import cazcade.liquid.api.request.DeletePoolRequest;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public class DeletePoolCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(DeletePoolCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Deletes a pool";
    }

    @Nonnull
    public String getName() {
        return "rmdir";
    }

    @Nullable
    public String run(@Nonnull final String[] args, @Nonnull final ShellSession shellSession) throws Exception {
        if (args.length < 1) {
            System.err.println("rmdir <pool>");
        }
        final LiquidURI poolURI = CommandSupport.resolvePoolOrObject(shellSession, args[0]);
        final LiquidMessage response = shellSession.getDataStore().process(new DeletePoolRequest(shellSession.getIdentity(), poolURI));
        final LSDBaseEntity entity = response.getResponse();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        return entity.getURI().toString();
    }


}
