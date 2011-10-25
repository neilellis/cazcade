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
import cazcade.liquid.api.request.VisitPoolRequest;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class ChangePoolCommand extends AbstractShortLivedCommand {
    private final static Logger log = Logger.getLogger(ChangePoolCommand.class);


    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Change current pool";
    }

    public String getName() {
        return "cd";
    }

    public String run(final String[] args, ShellSession shellSession) throws Exception {
        if (args.length < 1) {
            System.err.println("You must specify the new pool");
            return "";
        }

        String pool = args[0];
        LiquidURI poolURI;
        poolURI = CommandSupport.resolvePoolOrObject(shellSession, pool);
        LiquidMessage response = shellSession.getDataStore().process(new VisitPoolRequest(shellSession.getIdentity(), poolURI));
        final LSDEntity responseEntity = response.getResponse();
        if (response.getState() != LiquidMessageState.SUCCESS) {
            System.err.println(responseEntity.getAttribute(LSDAttribute.DESCRIPTION));
            return null;
        }
        shellSession.setCurrentPool(responseEntity);
        return responseEntity.getURI().toString();
    }


}
