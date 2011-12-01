package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.cli.commands.ExecutableCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDEntity;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public class WithCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(WithCommand.class);

    private final ExecutableCommand command;

    public WithCommand(final ExecutableCommand command) {
        super();
        this.command = command;
    }

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Executes commands within the context of an entity and then persists the changes.";
    }

    @Nonnull
    public String getName() {
        return "with";
    }


    @Nullable
    public String run(@Nonnull final String[] args, @Nonnull final ShellSession shellSession) throws Exception {
        if (args.length < 2) {
            System.err.println("with (user|alias|pool) <identifier> { <commands> }");
            return null;
        } else {
            final CommandSupport.AlterEntityCallback callback = new CommandSupport.AlterEntityCallback() {
                @Override
                public LSDEntity alter(final LSDEntity entity) {
                    shellSession.pushEntity(entity);
                    try {
                        command.execute();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    return shellSession.popEntity();

                }
            };
            if ("user".equals(args[0])) {
                return CommandSupport.alterUser(shellSession, CommandSupport.resolveUser(shellSession, args[1]), callback);
            } else if ("alias".equals(args[0])) {
                return CommandSupport.alterAlias(shellSession, CommandSupport.resolveAlias(shellSession, args[1]), callback);
            } else if ("pool".equals(args[0])) {
                return CommandSupport.alterPool(shellSession, CommandSupport.resolvePoolOrObject(shellSession, args[1]), callback);
            } else if ("object".equals(args[0])) {
                return CommandSupport.alterObject(shellSession, CommandSupport.resolvePoolOrObject(shellSession, args[1]), callback);
            } else {
                System.err.println("with (user|alias|pool|object) <identifier> { <commands> }");
                return null;
            }
        }
    }


}