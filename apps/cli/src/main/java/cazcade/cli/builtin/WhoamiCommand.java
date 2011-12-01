package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidSessionIdentifier;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public class WhoamiCommand extends AbstractShortLivedCommand {
    @Nonnull
    private static final Logger log = Logger.getLogger(WhoamiCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Show details of the current user and session.";
    }

    @Nonnull
    public String getName() {
        return "whoami";
    }

    @Nullable
    public String run(final String[] args, @Nonnull final ShellSession shellSession) throws Exception {
        final LiquidSessionIdentifier identity = shellSession.getIdentity();
        if (identity == null) {
            System.err.println("Not logged in.");
            return null;
        } else {
            System.out.println(identity);
            if ("admin".equals(identity.getName()) || "neo".equals(identity.getName())) {
                System.err.println("Currently your username is hardwired to have super-user privileges, tread carefully young Skywalker.");
            }
            return identity.toString();
        }
    }


}
