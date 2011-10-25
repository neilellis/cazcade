package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.builtin.support.CommandSupport;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.cli.commands.ExecutableCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.lsd.LSDEntity;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class WithCommand extends AbstractShortLivedCommand {
    private final static Logger log = Logger.getLogger(WithCommand.class);

    private ExecutableCommand command;

    public WithCommand(ExecutableCommand command) {
        this.command = command;
    }

    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Executes commands within the context of an entity and then persists the changes.";
    }

    public String getName() {
        return "with";
    }


    public String run(final String[] args, final ShellSession shellSession) throws Exception {
        if (args.length < 2) {
            System.err.println("with (user|alias|pool) <identifier> { <commands> }");
            return null;
        } else {
            final CommandSupport.AlterEntityCallback callback = new CommandSupport.AlterEntityCallback() {
                @Override
                public LSDEntity alter(LSDEntity entity) {
                    shellSession.pushEntity(entity);
                    try {
                        command.execute();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    return shellSession.popEntity();

                }
            };
            if (args[0].equals("user")) {
                return CommandSupport.alterUser(shellSession, CommandSupport.resolveUser(shellSession, args[1]), callback);
            } else if (args[0].equals("alias")) {
                return CommandSupport.alterAlias(shellSession, CommandSupport.resolveAlias(shellSession, args[1]), callback);
            } else if (args[0].equals("pool")) {
                return CommandSupport.alterPool(shellSession, CommandSupport.resolvePoolOrObject(shellSession, args[1]), callback);
            } else if (args[0].equals("object")) {
                return CommandSupport.alterObject(shellSession, CommandSupport.resolvePoolOrObject(shellSession, args[1]), callback);
            } else {
                System.err.println("with (user|alias|pool|object) <identifier> { <commands> }");
                return null;
            }
        }
    }


}