package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.DeletePoolRequest;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class DeletePoolCommand extends AbstractShortLivedCommand {
    private final static Logger log = Logger.getLogger(DeletePoolCommand.class);

    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Deletes a pool";
    }

    public String getName() {
        return "rmdir";
    }

    public String run(final String[] args, ShellSession shellSession) throws Exception {
        if (args.length < 1) {
            System.err.println("rmdir <pool>");
        }
        LiquidURI poolURI = CommandSupport.resolvePoolOrObject(shellSession, args[0]);
        LiquidMessage response = shellSession.getDataStore().process(new DeletePoolRequest(shellSession.getIdentity(), poolURI));
        final LSDEntity entity = response.getResponse();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(entity.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        return entity.getURI().toString();
    }


}
