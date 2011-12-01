package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDAttribute;
import cazcade.liquid.api.lsd.LSDEntity;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public class DestrictUserCommand extends AbstractShortLivedCommand {
    @Nonnull
    private final static Logger log = Logger.getLogger(DestrictUserCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "De-restrict a user.";
    }

    @Nonnull
    public String getName() {
        return "destrict";
    }

    @Nullable
    public String run(@Nonnull final String[] args, @Nonnull ShellSession shellSession) throws Exception {
        if (args.length < 1) {
            System.err.println("destrict <user>");
            return null;
        } else {
            return CommandSupport.alterUser(shellSession, CommandSupport.resolveUser(shellSession, args[0]), new CommandSupport.AlterEntityCallback() {
                @Nonnull
                @Override
                public LSDEntity alter(@Nonnull LSDEntity entity) {
                    entity.setAttribute(LSDAttribute.SECURITY_RESTRICTED, "false");
                    return entity;

                }
            });
        }


    }


}
