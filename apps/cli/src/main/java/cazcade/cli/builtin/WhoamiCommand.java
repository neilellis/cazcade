package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidSessionIdentifier;
import org.apache.commons.cli.Options;

/**
 * @author Neil Ellis
 */

public class WhoamiCommand extends AbstractShortLivedCommand {
    private final static Logger log = Logger.getLogger(WhoamiCommand.class);

    public Options getOptions() {
        return new Options();
    }

    @Override
    public String getDescription() {
        return "Show details of the current user and session.";
    }

    public String getName() {
        return "whoami";
    }

    public String run(final String[] args, ShellSession shellSession) throws Exception {
        LiquidSessionIdentifier identity = shellSession.getIdentity();
        if (identity == null) {
            System.err.println("Not logged in.");
            return null;
        } else {
            System.out.println(identity);
            if(identity.getName().equals("admin") || identity.getName().equals("neo")) {
                System.err.println("Currently your username is hardwired to have super-user privileges, tread carefully young Skywalker.");
            }
            return identity.toString();
        }
    }


}
