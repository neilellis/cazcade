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
import cazcade.liquid.api.request.VisitPoolRequest;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public class ChangePoolCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(ChangePoolCommand.class);


    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Change current pool";
    }

    @Nonnull
    public String getName() {
        return "cd";
    }

    @Nullable
    public String run(@Nonnull final String[] args, @Nonnull final ShellSession shellSession) throws Exception {
        if (args.length < 1) {
            System.err.println("You must specify the new pool");
            return "";
        }

        final String pool = args[0];
        final LiquidURI poolURI;
        poolURI = CommandSupport.resolvePoolOrObject(shellSession, pool);
        final LiquidMessage response = shellSession.getDataStore().process(new VisitPoolRequest(shellSession.getIdentity(), poolURI));
        final LSDBaseEntity responseEntity = response.getResponse();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(responseEntity.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        shellSession.setCurrentPool(responseEntity);
        return responseEntity.getURI().toString();
    }


}
