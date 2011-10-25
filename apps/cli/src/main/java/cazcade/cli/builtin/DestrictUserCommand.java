package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class DestrictUserCommand extends AbstractShortLivedCommand {
    private final static Logger log = Logger.getLogger(DestrictUserCommand.class);

    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "De-restrict a user.";
    }

    public String getName() {
        return "destrict";
    }

    public String run(final String[] args, ShellSession shellSession) throws Exception {
        if (args.length < 1) {
            System.err.println("destrict <user>");
            return null;
        } else {
            return CommandSupport.alterUser(shellSession, CommandSupport.resolveUser(shellSession, args[0]), new CommandSupport.AlterEntityCallback() {
                @Override
                public LSDEntity alter(LSDEntity entity) {
                    entity.setAttribute(LSDAttribute.SECURITY_RESTRICTED, "false");
                    return entity;

                }
            });
        }


    }


}
