package cazcade.cli.builtin;

import cazcade.cli.ShellSession;
import cazcade.cli.commands.AbstractShortLivedCommand;
import cazcade.common.Logger;
import cazcade.liquid.api.LiquidURI;
import cazcade.liquid.api.lsd.LSDEntity;
import org.apache.commons.cli.Options;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Neil Ellis
 */

public class PwdCommand extends AbstractShortLivedCommand {
    @Nonnull
    private final static Logger log = Logger.getLogger(PwdCommand.class);

    @Nonnull
    public Options getOptions() {
        return new Options();
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Display url of current pool.";
    }

    @Nonnull
    public String getName() {
        return "pwd";
    }

    @Nullable
    public String run(final String[] args, @Nonnull ShellSession shellSession) throws Exception {
        final LSDEntity currentPool = shellSession.getCurrentPool();
        if (currentPool == null) {
            System.err.println("No current pool, please login.");
            return null;
        } else {
            LiquidURI poolURI = currentPool.getURI();
            System.out.println(poolURI);
            return poolURI.toString();
        }
    }


}
