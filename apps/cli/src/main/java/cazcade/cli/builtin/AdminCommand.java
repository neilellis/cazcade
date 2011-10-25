package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.LiquidMessageState;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import cazcade.liquid.api.request.AdminCommandRequest;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class AdminCommand extends AbstractShortLivedCommand {
    private final static Logger log = Logger.getLogger(AdminCommand.class);


    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Send admin request to server.";
    }

    public String getName() {
        return "admin";
    }

    public String run(final String[] args, ShellSession shellSession) throws Exception {
        if (args.length < 1) {
            System.err.println("You must specify at least a command name");
            return "";
        }
        LiquidMessage response = shellSession.getDataStore().process(new AdminCommandRequest(null, shellSession.getIdentity(), args));
        final LSDEntity responseEntity = response.getResponse();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(responseEntity.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        return "";
    }


}
