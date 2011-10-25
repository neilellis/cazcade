package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDEntity;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class SetCommand extends AbstractShortLivedCommand {
    private final static Logger log = Logger.getLogger(SetCommand.class);

    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Set a value on the entity retrieved by 'with'.";
    }

    public String getName() {
        return "set";
    }

    public String run(final String[] args, ShellSession shellSession) throws Exception {
        if(!CommandSupport.checkEntityOnStack(shellSession)) {return null;}
        if (args.length < 2) {
            System.err.println("set <attribute-name> <value>");
        }

        final LSDEntity currentEntity = shellSession.getCurrentEntity();
        currentEntity.setValue(args[0], args[1]);
        return args[1];
    }


}
