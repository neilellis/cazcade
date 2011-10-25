package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class RenamePoolCommand extends AbstractShortLivedCommand {
    private final static Logger log = Logger.getLogger(RenamePoolCommand.class);

    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Rename a pool";
    }

    public String getName() {
        return "rename";
    }

    public String run(final String[] args, ShellSession shellSession) throws Exception {


        String from;
        final String to;
        if (args.length < 2) {
            System.err.println("rename <from> <to>");
            return null;
        } else {
            from = args[0];
            to = args[1];
        }
        LiquidURI poolURI;
        poolURI = CommandSupport.resolvePoolOrObject(shellSession, from);
        return CommandSupport.alterPool(shellSession, poolURI, new CommandSupport.AlterEntityCallback() {
            @Override
            public LSDEntity alter(LSDEntity entity) {
                entity.setAttribute(LSDAttribute.NAME, to);
                return entity;

            }
        });


    }


}
