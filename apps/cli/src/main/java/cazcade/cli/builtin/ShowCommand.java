package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidMessage;
import cazcade.liquid.api.lsd.LSDEntity;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class ShowCommand extends AbstractShortLivedCommand {
    private final static Logger log = Logger.getLogger(ShowCommand.class);

    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Show a resource e.g. user, session etc.";
    }

    public String getName() {
        return "show";
    }

    public String run(final String[] args, ShellSession shellSession) throws Exception {

        if (args.length < 2) {
            System.err.println("show <resource-type> <resource-name>");
            return null;
        }

        LiquidMessage response;
        response = CommandSupport.retrieveObject(args, shellSession);
        if(response == null) {
            return null;
        }
        final LSDEntity entity = response.getResponse();
        System.out.println(entity);
        return entity.getID().toString();
    }


}
